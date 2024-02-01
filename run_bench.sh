#!/usr/bin/env sh

# this just runs the individual files properly. The timing is left to the files themselves.

HASKELL_FILE="haskell-list.hs"
HASKELL_OUT="haskell-list"

TAIL_CALL_FILE="scala-tail-call.sc"

LAZY_LIST_FILE="scala-lazy-list.sc"

echo "Haskell (stack script):\n"
stack script --resolver nightly $HASKELL_FILE 2>/dev/null

echo "Haskell (ghc):\n"
stack ghc --resolver nightly -- $HASKELL_FILE -o "$HASKELL_OUT.no-opt.o" >/dev/null 2>&1


echo "Haskell (ghc -O):\n"
stack ghc --resolver nightly -- $HASKELL_FILE -O -o "$HASKELL_OUT.opt.o" >/dev/null 2>&1

echo "Scala Lazy List:\n"
scala-cli $LAZY_LIST_FILE

echo "Scala Tail Call:\n"
scala-cli $TAIL_CALL_FILE
