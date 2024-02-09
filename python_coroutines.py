import sys
import time

# exp is either int or ('Add', exp, exp)
def gen_exp_r(n):
    e = 1
    for i in range(n):
        e = ('Add', i, e)
    return e

def gen_exp_l(n):
    e = 1
    for i in range(n):
        e = ('Add', e, i)
    return e


def evaluate(exp, cont):
    if isinstance(exp, int):
        yield cont.send(exp)
    elif isinstance(exp, tuple) and len(exp) == 3 and exp[0] == 'Add':
        al = after_left(exp[2], cont)
        next(al)
        yield from evaluate(exp[1], al)
    else:
        raise AssertionError

def after_left(right_expr, top_cont):
    ar = after_right((yield), top_cont)
    next(ar)
    yield from evaluate(right_expr, ar)

def after_right(left_res, top_cont):
    yield from evaluate(left_res + (yield), top_cont)

def returner():
    yield (yield)

def doit1():
    r = returner()
    next(r)
    return next(evaluate(gen_exp_l(10), r))
    ### also works:
    # return next(evaluate(gen_exp_r(10), r))
    ### RecursionError:
    # return next(evaluate(gen_exp_l(1000), r))
    # return next(evaluate(gen_exp_r(1000), r))

def returner_loop():
    yield (yield), None

def after_right_loop(left_res, top_cont):
    yield left_res + (yield), top_cont

def after_left_loop(right_expr, top_cont):
    ar = after_right_loop((yield), top_cont)
    next(ar)
    yield right_expr, ar

def evaluate_loop(exp):
    cont = returner_loop()
    next(cont)

    while True:
        if isinstance(exp, int):
            exp, cont = cont.send(exp)
            if cont is None:
                return exp
        elif isinstance(exp, tuple) and len(exp) == 3 and exp[0] == 'Add':
            al = after_left_loop(exp[2], cont)
            next(al)
            exp, cont = exp[1], al
        else:
            raise AssertionError

def doit2():
    return evaluate_loop(gen_exp_l(1000))
    ### also works:
    # return evaluate_loop(gen_exp_r(1000))


def double(l):
    if l:
        return [l[0], l[0], *double(l[1:])]
    else:
        return []


def inner_cont(head, cont):
    yield cont.send([head, head, *(yield)])

def double_coro(l, cont):
    if l:
        k = inner_cont(l[0], cont)
        next(k)
        yield from double_coro(l[1:], k)
    else:
        yield cont.send([])

def list_returner_loop():
    yield (yield), False, None

def inner_cont_loop(head, cont):
    yield [head, head, *(yield)], False, cont

def double_coro_loop(l):
    cont = list_returner_loop()
    next(cont)
    down = True

    while True:
        if down and l:
            k = inner_cont_loop(l[0], cont)
            next(k)
            l, down, cont = l[1:], True, k
        else:
            l, down, cont = cont.send(l)
            if cont is None:
                return l

runSize = int(1e4)

def time_double_coro():
    start = time.time_ns()
    double_coro_loop(list(range(runSize)))
    end = time.time_ns()
    return int(end - start) // 1e6

if __name__ == '__main__':
    if len(sys.argv) > 0:
        runSize = int(sys.argv[1])
    times = [time_double_coro() for _ in range(10)]
    print(f"Average time: ({runSize})", sum(times) / len(times), " ms")
