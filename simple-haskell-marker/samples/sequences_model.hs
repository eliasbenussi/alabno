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

isADigit :: Char -> Bool
-- Returns True if the character represents a digit '0'..'9';
-- False otherwise
isADigit c
  = c >= '0' && c <= '9'

-- False otherwise
isAlpha :: Char -> Bool
-- Returns True if the character represents an alphabetic
-- character either in the range 'a'..'z' or in the range 'A'..'Z';
isAlpha c
  = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')

digitToInt :: Char -> Int
-- Pre: the character is one of '0'..'9'
-- Returns the integer [0..9] corresponding to the given character.
-- Note: this is a simpler version of digitToInt in module Data.Char,
-- which does not assume the precondition.
digitToInt c
  = ord c - ord '0'

toUpper :: Char -> Char
-- Returns the upper case character corresponding to the input.
-- Uses guards by way of variety.
toUpper c
  | c >= 'A' && c <= 'Z' = c
  | c >= 'a' && c <= 'z' = chr (ord 'A' + ord c - ord 'a')

--
-- Sequences and series
--

-- Arithmetic sequence
arithmeticSeq :: Double -> Double -> Int -> Double
arithmeticSeq a d n
  = a + fromIntegral n * d

-- Geometric sequence
geometricSeq :: Double -> Double -> Int -> Double
geometricSeq a r n
  = a * r ^ n

-- Arithmetic series
arithmeticSeries :: Double -> Double -> Int -> Double
arithmeticSeries a d n
  = (n' + 1) * (a + d * n' / 2)
  where
    n' = fromIntegral n

-- Geometric series
geometricSeries :: Double -> Double -> Int -> Double
geometricSeries a r n
  | r == 1    = (fromIntegral n + 1) * a
  | otherwise = a * (1 - r ^ (n + 1)) / (1 - r)
