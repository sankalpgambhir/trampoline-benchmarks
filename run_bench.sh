#!/usr/bin/env sh

# this just runs the individual files properly. The timing is left to the files themselves.

line() {
    echo "-----------------------------------------"
}

HASKELL_FILE="haskell-list.hs"
HASKELL_OUT="haskell-list"

TAIL_CALL_FILE="scala-tail-call.sc"

LAZY_LIST_FILE="scala-lazy-list.sc"

line
line
echo "Haskell (stack script):"
line
stack script --resolver nightly $HASKELL_FILE 2>/dev/null

line
line
echo "Haskell (ghc):"
line
stack ghc --resolver nightly -- $HASKELL_FILE -o "$HASKELL_OUT.no-opt.o" >/dev/null 2>&1

line
line
echo "Haskell (ghc -O):"
line
stack ghc --resolver nightly -- $HASKELL_FILE -O -o "$HASKELL_OUT.opt.o" >/dev/null 2>&1

line
line
echo "Scala Lazy List:"
line
scala-cli $LAZY_LIST_FILE

line
line
echo "Scala Tail Call:"
line
scala-cli $TAIL_CALL_FILE
