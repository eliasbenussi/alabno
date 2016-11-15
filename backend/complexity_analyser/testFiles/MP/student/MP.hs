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
lookUp x [] = []
lookUp x ((key , value) : xs)
  | x == key = value : (lookUp x xs)
  | otherwise = lookUp x xs


split :: [Char] -> String -> (String, [String])
split sep [] = ([], [""])
split sep (x : xs)
  | elem x sep = (x : seplist, "" : word : rest)
  | otherwise = (seplist, (x : word) : rest)
    where (seplist, word : rest) = split sep xs


combine :: String -> [String] -> [String]
combine sep [] = [sep]
combine [] xs = xs
combine (y : sep) (x : xs)
  = x:[y]:rest
    where rest = combine sep xs
-- if x == "", it will be concatenated, but it will not

getKeywordDefs :: [String] -> KeywordDefs
getKeywordDefs [] = []
getKeywordDefs (x : xs)
  = (head r, concat(combine (drop 1 sep) (tail r))) : getKeywordDefs xs
  where
    (sep, r) = split " " x


expand :: FileContents -> FileContents -> FileContents
expand [] _ = []
expand input keys
  = concat (combine sep [replaceWord x (getKeywordDefs keys') | x <- words])
  where
    (sep, words) = split separators input
    (sep2, keys') = split "\n" keys


replaceWord :: String -> KeywordDefs -> String
replaceWord xs [] = xs
replaceWord xs ((key , val) : def)
  | xs == key = val
  | otherwise = replaceWord xs def

-- expandext is the function implemented so that the algorithm
-- expands the input file once for each set of definitions.

-- I have used the map function so that I could concatenate the
-- string of dashes "-----", the endline "\n" and the output from
-- the list comprehension

expandext :: FileContents -> FileContents -> FileContents
expandext [] _ = []
expandext input keys
  = concat ( map (++"----- \n") [(expand input str1) | str1 <- v])
  where
    (sep3, v) = split "#" keys


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
-- I have changed the output so that it calls function expandext instead
      writeFile output (expandext t i)
    main' _ = putStrLn ("Usage: runghc MP <template> <info> <output>")
