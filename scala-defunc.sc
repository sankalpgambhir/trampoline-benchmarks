
sealed trait Call[V]
case class Call0[V](v: V) extends Call[V]
case class Call1[U, V](f: Callable[U, V], arg: U) extends Call[V]

sealed trait Callable[U, V]:
  def apply(u: U): Call[V]

case class Identity[V]() extends Callable[V, V]:
  def apply(v: V): Call[V] = Call0(v)

case class Double[V]() extends Callable[(List[V], Callable[List[V], List[V]]), List[V]]:
  def apply(lk: (List[V], Callable[List[V], List[V]])): Call[List[V]] =
    val (l, k) = lk
    if l.isEmpty then
      Call1(k, Nil)
    else
      val (hd, tl) = (l.head, l.tail)
      Call1(Double(), (tl, Internal(hd, k)))

case class Internal[V](hd: V, k: Callable[List[V], List[V]]) extends Callable[List[V], List[V]]:
  def apply(res: List[V]): Call[List[V]] =
    Call1(k, hd :: hd :: res)

def execute[V](c: Call[V]): V =
  var call = c
  while true do
    call match
      case Call0(v) => return v
      case Call1(f, arg) => call = f(arg)
  null.asInstanceOf[V]

// def unsafeApply[U, V](callable: Callable[U, List[V]], argument: U): Call[List[V]] =
//   callable match
//     case Identity() => Call0(argument.asInstanceOf[List[V]])
//     case Double() =>
//       val (l, k) = argument.asInstanceOf[(List[V], Callable[List[V], List[V]])]
//       if l.isEmpty then
//         Call1[List[V], List[V]](k, Nil)
//       else
//         val (hd, tl) = (l.head, l.tail)
//         Call1(Double(), (tl, Internal(hd, k)))
//     case Internal(hd, k) =>
//       Call1[List[V], List[V]](k, hd :: hd :: argument.asInstanceOf[List[V]])

def double[A](l: List[A]): List[A] =
  execute((new Double).apply((l, (new Identity))))

def time[A](f: () => A): Long =
    val start = System.currentTimeMillis
    f()
    val end = System.currentTimeMillis
    end - start

def run(): Long = double(List.fill(10_000_000)(1)).length

def main =
    val times = (1 to 25).map(_ => time(run)).drop(5)
    println(s"Average time: ${times.sum / times.length} ms")

main
