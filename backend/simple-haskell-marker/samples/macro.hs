module MP where

import System.Environment

type FileContents = String

type Keyword      = String
type KeywordValue = String
type KeywordDefs  = [(Keyword, KeywordValue)]

separators :: String
separators
  = " \n\t.,:;!\"\'()<>/\\"


lookUp :: String -> [(String, a)] -> [a]
lookUp = error "TODO: implement lookUp"

split :: [Char] -> String -> (String, [String])
split chs []
    = ("", [""])
split chs (c : cs)
        | elem c chs = (c : terms, "" : words)
        | otherwise = (terms, (c:w) : ws)
        where
                (terms, words) = split chs cs
                (w : ws) = words

combine :: String -> [String] -> [String]
combine = error "TODO: implement combine"

getKeywordDefs :: [String] -> KeywordDefs
getKeywordDefs = error "TODO: implement getKeywordDefs"

expand :: FileContents -> FileContents -> FileContents
expand = error "TODO: implement expand"

-- You may wish to uncomment and implement this helper function
-- when implementing expand
-- replaceWord :: String -> KeywordDefs -> String



main :: IO ()
-- The provided main program which uses your functions to merge a
-- template and source file.
main = do
  args <- getArgs
  main' args

  where
    main' :: [String] -> IO ()
    main' [template, source, output] = do
      t <- readFile template
      i <- readFile source
      writeFile output (expand t i)
    main' _ = putStrLn ("Usage: runghc MP <template> <info> <output>")

