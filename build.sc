import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._
import mill.scalalib.scalafmt._

import $ivy.`io.indigoengine::mill-indigo:0.19.0`, millindigo._

object snake extends MillIndigo with ScalafmtModule {
  def scalaVersion   = "3.6.2"
  def scalaJSVersion = "1.18.2"

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

  val indigoVersion = "0.19.0"

  def ivyDeps = Agg(
    ivy"io.indigoengine::indigo-json-circe::$indigoVersion",
    ivy"io.indigoengine::indigo::$indigoVersion",
    ivy"io.indigoengine::indigo-extras::$indigoVersion"
  )

  object test extends ScalaJSTests {

    def ivyDeps = Agg(
      ivy"org.scalameta::munit::1.1.0"
    )

    def testFramework = "munit.Framework"

    override def moduleKind = T(mill.scalajslib.api.ModuleKind.CommonJSModule)

  }

  // This is the root directory of the workspace / project.
  private val workspaceDir: os.Path =
    sys.env
      .get("MILL_WORKSPACE_ROOT")
      .map(os.Path(_))
      .getOrElse(os.pwd)

  def publishGame() = T.command {
    val docs = workspaceDir / "docs"

    if (os.exists(docs)) {
      os.remove.all(docs)
    }

    os.makeDir.all(docs)

    val outPath = workspaceDir / "docs"
    os.makeDir.all(outPath)

    val buildDir = workspaceDir / "out" / "snake" / "indigoBuildFull.dest"

    os.list(buildDir)
      .toList
      .foreach { p =>
        os.copy(p, outPath / p.last)
      }
  }

}
