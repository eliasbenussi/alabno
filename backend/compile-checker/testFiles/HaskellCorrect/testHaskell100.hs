module Recursion where

-- Precondition on all integers: they're all non-negative.

isPrime :: Int -> Bool
isPrime a
  | a < 2 = False
  | a == 2 = True
  | a `mod` 2 == 0 = False
  | otherwise = testPrime 3
     where
       root = floor(sqrt(fromIntegral a))
       testPrime i
         | i>root = True
         | a `mod` i == 0 = False
         | otherwise = testPrime (i + 2)

nextPrime :: Int -> Int
nextPrime a
  | isPrime (a + 1) = (a + 1)
  | otherwise = nextPrime (a + 1)

modPow :: Int -> Int -> Int -> Int
-- Pre: 1 <= m <= sqrt(maxint)
modPow x y m
  | m == 1 = 0
  | otherwise = myPower x y 1
    where
      myPower :: Int -> Int -> Int -> Int
      myPower x y result
        | y == 0 = result `mod` m
        | y `mod` 2 == 0 = myPower (md * md) (y `div` 2) result
        | otherwise = myPower (md * md) (y `div` 2) ((result * md) `mod` m)
          where md = x `mod` m

isCarmichael :: Int -> Bool
isCarmichael n
  | isPrime n == True = False
  | otherwise = testCarm 2
    where
      testCarm :: Int -> Bool
      testCarm a
        | a < n && modPow a n n == a = testCarm (a + 1)
        | a == n = True
        | otherwise = (modPow a n n == a)

primeFactors :: Int -> [ Int ]
-- Pre: x >= 1
primeFactors n
  = primeList n 2
    where
      primeList :: Int -> Int -> [Int]
      primeList b a
        | b == 1 = []
        | isPrime b = [b]
        | b `mod` a == 0 = a : primeList (b `div` a) a
        | otherwise = primeList b (a + (a `mod` 2) + 1)

sumDigits :: Int -> Int
sumDigits n
  | n<10 = n
  | otherwise = sumdig n 0
    where
      sumdig :: Int->Int->Int
      sumdig n sum
        | n < 10 = sum + n
        | otherwise = sumdig (n `div` 10) (sum + (n `mod` 10))

sumAllDigits :: [ Int ] -> Int
sumAllDigits []       = 0
sumAllDigits (x : xs) = sumDigits x + (sumAllDigits xs)

nextSmithNumber :: Int -> Int
nextSmithNumber a
        | sumDigits (a + 1) == sumAllDigits (primeFactors (a + 1)) &&
          (isPrime (a + 1) == False) = a + 1
        | otherwise = nextSmithNumber (a + 1)

