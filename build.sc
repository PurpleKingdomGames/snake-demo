import scala.util.Success
import scala.util.Try
import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._
import mill.scalalib.scalafmt._
import coursier.maven.MavenRepository

import $ivy.`io.indigoengine::mill-indigo:0.15.1`, millindigo._

import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.5`
import io.github.davidgregory084.TpolecatModule

object snake extends MillIndigo with TpolecatModule with ScalafmtModule {
  def scalaVersion   = "3.3.1"
  def scalaJSVersion = "1.13.2"

  val indigoOptions: IndigoOptions =
    IndigoOptions.defaults
      .withTitle("Snake - Made with Indigo")
      .withWindowWidth(720)
      .withWindowHeight(516)
      .withBackgroundColor("black")
      .excludeAssets {
        case p if p.startsWith(os.RelPath("asset_dev")) => true
        case _                                          => false
      }

  val indigoGenerators: IndigoGenerators =
    IndigoGenerators("snake.generated")
      .listAssets("Assets", indigoOptions.assets)
      .generateConfig("SnakeConfig", indigoOptions)

  def buildGame() = T.command {
    T {
      compile()
      fastLinkJS()
      indigoBuild()()
    }
  }

  def buildGameFull() = T.command {
    T {
      compile()
      fullLinkJS()
      indigoBuildFull()()
    }
  }

  def runGame() = T.command {
    T {
      compile()
      fastLinkJS()
      indigoRun()()
    }
  }

  def runGameFull() = T.command {
    T {
      compile()
      fullLinkJS()
      indigoRunFull()()
    }
  }

  val indigoVersion = "0.15.1"

  def ivyDeps = Agg(
    ivy"io.indigoengine::indigo-json-circe::$indigoVersion",
    ivy"io.indigoengine::indigo::$indigoVersion",
    ivy"io.indigoengine::indigo-extras::$indigoVersion"
  )

  object test extends ScalaJSTests {

    def ivyDeps = Agg(
      ivy"org.scalameta::munit::0.7.29"
    )

    def testFramework = "munit.Framework"

    override def moduleKind = T(mill.scalajslib.api.ModuleKind.CommonJSModule)

  }

  def publishGame() = T.command {
    val docs = os.pwd / "docs"

    if (os.exists(docs)) {
      os.remove.all(docs)
    }

    os.makeDir.all(docs)

    val outPath = os.pwd / "docs"
    os.makeDir.all(outPath)

    val buildDir = os.pwd / "out" / "snake" / "indigoBuildFull.dest"

    os.list(buildDir)
      .toList
      .foreach { p =>
        os.copy(p, outPath / p.last)
      }
  }

}
