module Bench where
import IC.TestSuite

import Control.Exception
import Control.Monad
import Data.List

import Tests hiding (main)

import Criterion.Main


goTest' (TestCase name f cases) = do
  counts <- (forM cases (handle majorExceptionHandler . goTestOne' name f))
  return True
  where
    majorExceptionHandler :: SomeException -> IO Bool
    majorExceptionHandler e = putStr "" >> return False

goTestOne' name f (input, expected) = handle exceptionHandler $ do
      r <- evaluate (f input)
      if r == expected
        then return True
        else return False
      where
        failedStanza :: Show x => Bool -> x -> IO Bool
        failedStanza b x = do
          return False

        exceptionHandler :: SomeException -> IO Bool
        exceptionHandler = failedStanza True


runTests' = map $ \t@(TestCase s func arg) -> bench s $ whnfIO (goTest' t)
main = defaultMain [bgroup "tests" (runTests' allTestCases)]