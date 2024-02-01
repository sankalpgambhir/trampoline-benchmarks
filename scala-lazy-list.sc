
def double[A](l: LazyList[A]): LazyList[A] =
    if l.isEmpty then
        LazyList.empty
    else
        l.head #:: l.head #:: double(l.tail)

def time[A](f: () => A): Long =
    val start = System.currentTimeMillis
    f()
    val end = System.currentTimeMillis
    end - start

def run(): Long = double(LazyList.fill(10_000_000)(1)).length

def main =
    val times = (1 to 15).map(_ => time(run)).drop(5)
    println(s"Average time: ${times.sum / times.length} ms")

main
