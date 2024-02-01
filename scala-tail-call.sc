
sealed trait TailCall[V]
case class TailCall0[V](v: V) extends TailCall[V]
case class TailCall1[U, V](f: U => TailCall[V], arg: U) extends TailCall[V]
case class TailCall2[U1, U2, V](f: (U1, U2) => TailCall[V], arg1: U1, arg2: U2) extends TailCall[V]

def execute[V](t: TailCall[V]): V =
    var tc = t
    while true do
        tc match
            case TailCall0(v) => return v
            case TailCall1(f, arg) => tc = f(arg)
            case TailCall2(f, arg1, arg2) => tc = f(arg1, arg2)
    null.asInstanceOf[V]

def double[A](l: List[A], k: List[A] => TailCall[List[A]]): TailCall[List[A]] =
    l match
        case Nil => k(Nil)
        case x :: xs => TailCall2[List[A], List[A] => TailCall[List[A]], List[A]](double, xs, ys => TailCall1(k, x :: x :: ys))

def time[A](f: () => A): Long =
    val start = System.currentTimeMillis
    f()
    val end = System.currentTimeMillis
    end - start

def run(): Long = execute(double(List.fill(10_000_000)(1), TailCall0.apply)).length

def main =
    val times = (1 to 15).map(_ => time(run)).drop(5)
    println(s"Average time: ${times.sum / times.length} ms")

main
