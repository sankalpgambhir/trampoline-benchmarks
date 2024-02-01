
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

val haskellFile = "haskell-list.hs"
val scalaLazyFile = "scala-lazy-list.sc"
val scalaTailFile = "scala-tail-call.sc"

// output files
val haskellOutputBase = haskellFile.takeWhile(_ != '.')
val haskellNoOptOut = s"$haskellOutputBase.no-opt.o"
val haskellOptOut = s"$haskellOutputBase.opt.o"

def haskellScriptBench(fileName: String): String = 
    Process(s"stack script --resolver nightly $fileName").!!(ProcessLogger(_ => ()))

def haskellCompileBench(fileName: String, outName: String, opts: String = ""): String =
    // compile and throw away output
    Process(s"stack ghc --resolver nightly -- $fileName -o $outName $opts").!!(ProcessLogger(_ => ()))
    Process(s"./$outName").!!

def scalaScriptBench(fileName: String): String =
    Process(s"scala-cli $fileName").!!(ProcessLogger(_ => ()))

val benchmarks: Seq[(String, () => String)] =
    Seq(
        ("Haskell (stack script)", () => haskellScriptBench(haskellFile)),
        ("Haskell (ghc)", () => haskellCompileBench(haskellFile, haskellNoOptOut)),
        ("Haskell (ghc -O)", () => haskellCompileBench(haskellFile, haskellOptOut, "-O")),
        ("Scala Lazy", () => scalaScriptBench(scalaLazyFile)),
        ("Scala Tail Call", () => scalaScriptBench(scalaTailFile))
    )

def bench(name: String, fun: () => String): String =
    val res = fun()
    val out = s"""
                |----------------------------------------------------------
                |----------------------------------------------------------
                |$name
                |----------------------------------------------------------
                ${res.split("\n").map("    |" + _).mkString("\n")}
                |----------------------------------------------------------
                |""".stripMargin
    out

def main =
    benchmarks.map {case (name, fun) => bench(name, fun)}.foreach(println)

main
