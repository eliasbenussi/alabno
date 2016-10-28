module Quadratic where

import Data.Char (ord, chr)

quad :: Int -> Int -> Int -> Int -> Int
-- Returns evaluated quadratic expression.
quad a b c x
	= a * x^2 + b * x + c

quadIsZero :: Int -> Int -> Int -> Int -> Bool
-- Returns True if a quadratic expression evaluates to zero;
-- False otherwise
quadIsZero a b c x
	= quad a b c x == 0

quadraticSolver :: Float -> Float -> Float -> (Float,Float)
-- Returns the two roots of a quadratic equation with
-- coefficients a, b, c
quadraticSolver a b c 
	= let root = sqrt (b^2 - 4 * a * c)
	in  (((-b + root)/ (2*a)), ((-b - root)/ (2*a)))

realRoots :: Float -> Float -> Float -> Bool
-- Returns True if the quadratic equation has real roots;
-- False otherwise
realRoots  a b c
	=b^2 - 4 * a * c >=0.0
