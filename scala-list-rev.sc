
def double[A](l: List[A]): List[A] =
    @annotation.tailrec
    def rec(l: List[A], acc: List[A]): List[A] =
        l match
            case Nil => acc
            case x :: xs => rec(xs, x :: x :: acc)
    rec(l, Nil).reverse

def time[A](f: () => A): Long =
    val start = System.currentTimeMillis
    f()
    val end = System.currentTimeMillis
    end - start

def run(): Long = double(List.fill(10_000_000)(1)).length

def main =
    val times = (1 to 15).map(_ => time(run)).drop(5)
    println(s"Average time: ${times.sum / times.length} ms")

main
