import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.util.concurrent.Executor

class SerialExecutor extends Executor:
  var active: Runnable = _
  def execute(runnable: Runnable): Unit =
    active = runnable
  @volatile var shouldRun = true
  new Thread {
    override def run(): Unit =
      while shouldRun do
        if active != null then
          active.run()
  }.start()

val exec = new SerialExecutor()
given ExecutionContext = ExecutionContext.fromExecutor(exec)

def double[A](l: List[A], k: List[A] => Future[List[A]]): Future[List[A]] =
    if l.isEmpty then
        Future { k(Nil) }.flatten
    else
        Future { double(l.tail, ys => Future { k(l.head :: l.head :: ys) }.flatten) }.flatten

def time[A](f: () => A): Long =
    val start = System.currentTimeMillis
    f()
    val end = System.currentTimeMillis
    end - start

def run(): Long =
  Await.result(double(List.fill(1_000_000)(1), Future.apply), Duration.Inf).length

def main =
    val times = (1 to 15).map(_ => time(run)).drop(5)
    println(s"Average time: ${times.sum / times.length} ms")
    exec.shouldRun = false

main
