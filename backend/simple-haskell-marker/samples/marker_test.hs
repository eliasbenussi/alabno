module Sequences where

import Data.Char (ord, chr)

maxOf2 :: Int -> Int -> Int
-- Returns first argument if it is larger than the second,
-- the second argument otherwise
maxOf2 x y
  | x > y     = x
  | otherwise = y

maxOf3 :: Int -> Int -> Int -> Int
-- Returns the largest of three Ints
maxOf3 x y z
  = maxOf2 x (maxOf2 y z)

-- Alternative version of maxOf3 using >= and &&
maxof3' x y z
  = if x >= y && x >= z
    then x
    else if y >= x && y >= z
         then y
         else z

-- Another version of maxOf3 using just >=
maxof3'' x y z
  = if x >= y
    then if x >= z
         then x
         else z
    else if y >= z
         then y
         else z

