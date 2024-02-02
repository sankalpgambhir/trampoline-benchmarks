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
