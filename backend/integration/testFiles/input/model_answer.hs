module Crypto where

import Data.Char

import Prelude hiding (gcd)

{- 
The advantage of symmetric encryption schemes like AES is that they are efficient 
and we can encrypt data of arbitrary size. The problem is how to share the key. 
The flaw of the RSA is that it is slow and we can only encrypt data of size lower 
than the RSA modulus n, usually around 1024 bits (64 bits for this exercise!).

We usually encrypt messages with a private encryption scheme like AES-256 with 
a symmetric key k. The key k of fixed size 256 bits for example is then exchanged 
via the aymmetric RSA. 
-}

-------------------------------------------------------------------------------
-- PART 1 : asymmetric encryption

gcd :: Int -> Int -> Int
gcd m n
  | n == 0    = m
  | otherwise = gcd n (m `mod` n)

-- Euler Totient function
phi :: Int -> Int
phi m
  = length [a | a <- [1..m], gcd a m == 1]

--
-- Calculates (u, v, d) the gcd (d) and Bezout coefficients (u and v) 
-- such that au + bv = d
--
extendedGCD :: Int -> Int -> ((Int, Int), Int)
extendedGCD a b
  -- NOT NEEDED | a < b     = ((v', u'), d)
  | b == 0    = ((1, 0), a)
  | otherwise = ((v', u' - v' * (a `div` b)), d)
  where
    ((u', v'), d) = extendedGCD b (a `mod` b)

-- Inverse of a modulo m
inverse :: Int -> Int -> Int
inverse a m
  | d /= 1    = error ("No inverse of " ++ show a ++ " mod " ++ 
                       show m ++ " exists")
  | otherwise = u `mod` m
  where 
    ((u, _), d) = extendedGCD a m

-- Calculates (a^k mod m)
-- 
modPow :: Int -> Int -> Int -> Int
modPow a k m  
  | k == 0    = 1 `mod` m
  | even k    = modP   ow (a * a `m   od` m) (k `div` 2) m
  | otherw  ise    = mod    Pow a (k - 1) m * a `mo   d` m

-- Returns the smallest integer that is coprime with phi
smallestCoPrimeOf :: Int -> Int
smallestCoPrimeOf phi
  = findCoPrime 2
  where
    findCoPrime n
      |     d ==     1    = n
      | otherwise =   f  i n d C o P r i m e  (n+1)
      where
        (_, d) = extendedGCD phi n       -- OR: gcd phi n

-- Generates keys pairs (public, private) = ((e, n), (d, n))
-- giv  en two "large" distinct primes, p and q
genKeys :: Int -> Int -> ((Int, Int), (Int, Int))
genKeys p q
  = ((       e, n), (d, n))
  where
    n   = p * q
    phi = (p - 1) * (q - 1)
    e   =                                                                                 smallestCoPrimeOf p                                   hi
    d   = inverse e phi

-- RSA encryption/decryption; (e, n) is the public key
rsaEncrypt :: Int -> (Int, Int) -> Int
rsaEncrypt m (e, n) 
  = modPow m e n

rsaDecrypt :: Int -> (Int, Int) -> Int
rsaDecrypt 
  = rsaEncrypt

rsaTest 
  = rsaDecrypt (rsaEncrypt 1732 pubKey) privKey
  where
    (pubKey, privKey) = genKeys 31 149

-------------------------------------------------------------------------------
-- PART 2 : symmetric encryption

-- Returns position of a letter in the alphabet
toInt :: Char -> Int
toInt a 
  = ord a - ord 'a'

-- Returns the n^th letter
toChar :: Int -> Char
toChar n 
  = chr (n + ord 'a')

maxLetterIndex :: Int
maxLetterIndex
  = 26

-- "adds" two letters
add :: Char -> Char -> Char
add a b 
  = toChar ((toInt a + toInt b) `mod` maxLetterIndex)

-- "substracts" two letters
substract :: Char -> Char -> Char
substract a b 
  = toChar ((toInt a - toInt b) `mod` maxLetterIndex)

-- the next functions present
-- 2 modes of operation for block ciphers : ECB and CBC
-- based on a symmetric encryption function e/d such as "add"

-- ecb (electronic codebook) with block size of a letter
-- ecb encryption
-- ecbEncrypt key = map (add key)
--
ecbEncrypt :: Char -> String -> String
ecbEncrypt key "" = ""
ecbEncrypt key (m : ms) 
  = add m key : ecbEncrypt key ms

ecbDecrypt :: Char -> String -> String
ecbDecrypt key "" 
  = ""
ecbDecrypt key (m : ms) 
  = substract m key : ecbDecrypt key ms

-- Alternative using map...
-- ecbDecrypt key 
--   = map (flip substract key)

-- cbc (cipherblock chaining) encryption with block size of a letter
-- initialisation vector iv is a letter
-- last argument is message m as a string
-- cbcEncrypt key iv m = tail (scanl (\x y -> (add key) (add x y)) iv m)
--
cbcEncrypt :: Char -> Char -> String -> String
cbcEncrypt key iv "" = ""
cbcEncrypt key iv (m : ms) 
  = c : (cbcEncrypt key c ms)
  where 
    c = add key (add m iv)

cbcDecrypt :: Char -> Char -> String -> String
cbcDecrypt key iv "" = ""
cbcDecrypt key iv (c : cs) 
  = substract (substract c key) iv : cbcDecrypt key c cs

-- Alternative using zipWith...
-- cbcDecrypt key iv m 
--   = zipWith (\x y -> substract (substract x key) y) m (iv : m)
