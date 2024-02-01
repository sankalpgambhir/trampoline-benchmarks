import Criterion.Main

double :: [a] -> [a]
double [] = []
double (hd : tl) = hd : hd : double tl

run :: Int -> Int
run n =
    ln
    where
        ln = length doubled
        doubled = double [1..n]

main :: IO ()
main = defaultMainWith defaultConfig [ bench "double" $ nf run 10000000 ]
