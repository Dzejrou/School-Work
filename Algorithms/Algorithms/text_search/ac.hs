import Prelude hiding (fail)
import qualified Data.Map as Map
import Data.Maybe
import qualified Data.List as List
import System.Environment -- For getArgs



--------------------------------------------------------------------------------
-- AHO-CORASICK ALGORITHM -----------------------------------------------------|
--------------------------------------------------------------------------------
--  Info:
--      The Aho-Corasick string matching algorithm searches a text looking for
--      a set of patterns (referred to as "searched words" in this source)
--      and matches them simultaneously. (Requiring only one pass through
--      the text.)
--      The algorithm itself has two parts, firstly, it has to construct it's
--      three main structures: the forward automaton, the fail function and
--      the output function (their implementation is discussed below), for
--      which the forward, fail and output functions are used.
--      In the second part, implemented in the function aho_corasick, the
--      algorithm traverses the text, using the aforementioned structures
--      to match the required patterns.
--
--  Usage:
--      There are two possible ways to use this AC implementation, you can
--      compile it with ghc and the run it with two arguments, first being
--      the name of the file containing the text to be searched and second
--      being the list of searched words passed as a string with the words
--      delimited by spaces.
--      The other way is through the interactive interpreter ghci, supplied
--      with the basic installation of Haskell, in that, type :load *ac.hs
--      (the asterisk is needed to avoid loading only the main function)
--      and then use the find function, which takes the text to be searched
--      in the form of a string and the list of searched words, which is in
--      this case an actual list of strings.
--
--  Data structures:
--      See below.
--
--  Complexity:
--      Complexity of the algorithm alone is O(m + n + p), where m is the length
--      of the text to be searched through and n is the length of the patterns
--      searched and p is the amount of found occurences of the patterns.
--      The total complexity is enlarged by the map lookups, inserts and
--      member test, bringing the overall complexity to O(m*log(m) + n + p).
--
--  Additional info:
--      Each function has it's documentation comments above it's implementation
--      with some additional comments inside the implementation where it is
--      necessary.
--
--  Author:
--      Jaroslav Jindrak, @Dzejrou
--      Student at MFF UK in Prague, Czech Republic
--------------------------------------------------------------------------------



--------------------------------------------------------------------------------
-- Data structures ------------------------------------------------------------|
--------------------------------------------------------------------------------
-- Note: Using structures to be used with pattern matching.
-- (= Dictionary like graph representation.)
--
-- Forward:
--      The forward structure acts as the forward function in automatons,
--      forward(Q, X) returns a new state that is achieved from the state
--      Q with input X in a *deterministic* automaton.
type Forward = Map.Map (Int, Char) Int
--
-- Out:
--      The out structure binds together a state with its output string.
--      (Or potentially a list of strings in the cases of searched words
--      like ["cor, ahocor, hocor"], which all end at the same position).
type Out = Map.Map Int [String]
--
-- Fail:
--      The fail structure acts as the fail function in automatons,
--      fail(Q, P) returns a new state that is achieved when the current
--      prefix stops mathing the word on state P.
type Fail = Map.Map Int Int
--------------------------------------------------------------------------------



--------------------------------------------------------------------------------
-- FORWARD --------------------------------------------------------------------|
--------------------------------------------------------------------------------

-- Builds the forward automaton, or adds a word to an existing one.
-- Param #1: The automaton (start with Map.empty).
-- Param #2: Word to be searched by the automaton.
-- Return: Pair of forward automaton and the parameter string.
-- Note: The second return value (string) is because of the use of mapAccumL.
forward::Forward -> String -> (Forward, String)
forward for str = (append 0 for str, str)

-- Auxilliary function that appends a word to a forward automaton.
-- Param #1: State where the word is supposed to start.
-- Param #2: The automaton.
-- Param #3: Word that is to be appended.
-- Return: Forward automaton with the new word appended.
append::Int -> Forward -> String -> Forward
append _ for [] = for -- Bottom case of the recursion.
append q for (x:xs)
    | Map.member y for = append (lowest_state y for) for xs
    | otherwise        = append highest (Map.insert y highest for) xs
    where
        y             = (q, x) -- Just make a state character pair.
        highest = (Map.size for) + 1 -- This is the highest state
                                     -- in the automaton.

-- Finds the lowest state where the given pair can be appended.
-- Param #1: Pair of a state and a character to be appended.
-- Param #2: The forward automaton.
-- Return: Number of the lowest state where the given pair can be appended.
lowest_state::(Int, Char) -> Forward -> Int
lowest_state (q, x) for = result
    where
        -- The starting state is default append place for all transitions.
        result = Map.findWithDefault 0 (q, x) for
--------------------------------------------------------------------------------



--------------------------------------------------------------------------------
-- OUT ------------------------------------------------------------------------|
--------------------------------------------------------------------------------

-- Builds the output structure.
-- Param #1: List of searched words.
-- Param #2: List of searched words. (This won't be changed.)
-- Param #3: The forward automaton for which the output structure is created.
-- Return: The output structure (function) corresponding to the given automaton.
output::[String] -> [String] -> Forward -> Out
output [] _ _            = Map.empty -- Bottom of the recursion.
-- (x:xs) is list of lists of char, so the tails function returns all suffixes
-- of the string x (starting from the longest).
output (x:xs) ws for = Map.insert (end_state 0 x for) (filter (\n -> elem n ws)
                        (List.tails x)) (output xs ws for)

-- Finds the state in which a given string ends (i.e. is accepted).
-- Param #1: Current state.
-- Param #2: The string for which an end state is searched.
-- Param #3: The forward automaton.
-- Return: Number of the states where the given string is accepted.
end_state::Int -> String -> Forward -> Int
end_state q [] _       = q -- Bottom of the recursion.
-- Read first character, go to the next state over the arc labeled by that
-- character.
end_state q (x:xs) for = end_state next_state xs for
    where
        next_state = (Map.findWithDefault 0 (q, x) for)
--------------------------------------------------------------------------------



--------------------------------------------------------------------------------
-- FAIL -----------------------------------------------------------------------|
--------------------------------------------------------------------------------

-- Returns layers in an automaton as if searched by the bfs algorithm.
-- Param #1: The forward automaton.
-- Param #2: List of searched words.
-- Return: Array of layers represented by arrays of state numbers.
bfs_lists::Forward -> [String] -> [[Int]]
bfs_lists for (w:ws) = map (\x -> filter (>0) (map (get x) branches)) range
    where
        -- This is why we need to remove all of those outside the list
        -- indexes, since we go to the length of the longest string,
        -- for all other strings (with different lengths) there will be
        -- out of bounds gets.
        range = [0..(maximum (map length branches))-1]
        -- List of branches (lists of state numbers that form one word).
        branches = map (get_branch 0 for) (w:ws) 

-- Returns a value in the given list at the given index, or -1 if the index is
-- out of bounds.
-- Param #1: Index of the value.
-- Param #2: List in which the value is.
-- Return: Value at the given index or -1 if the index is out of bounds.
-- Note: This is basically an index access with default error value.
get::Int -> [Int] -> Int
get x ys
    | x < length ys = ys !! x -- Index is in the list, return the value.
    | otherwise     = -1      -- Out of bounds, return fail value.

-- Returns a list of state numbers representing one given word in the given
-- forward automata.
-- Param #1: Starting state.
-- Param #2: The forward automaton.
-- Param #3: Word that the branch represents.
-- Return: List of state numbers representing a branch.
get_branch::Int -> Forward -> String -> [Int]
-- Bottom case, find the outputting state, [c] is a one letter string.
get_branch q for [c]    = [end_state q [c] for]
get_branch q for (c:cs) = x:(get_branch x for cs)
    where
        x = end_state q [c] for

-- Constructs the fail structure (representing the fail function in the
-- algorithm).
-- Param #1: Layers of states in the forward automaton (as from bfs_lists).
-- Param #2: Current layer.
-- Param #3: The forward automaton.
-- Return: The fail structure corresponding to the given forward automaton.
fail::[[Int]] -> Int -> Forward -> Fail
-- At the starting layer all states fail back to the starting state (i.e. 0).
fail x 0 for = fst (List.mapAccumL (\y q -> (Map.insert q 0 y, q))
                        Map.empty (x!!0))
-- Using x instead of (x:xs) because I will need index access.
fail x y for      = fst (List.mapAccumL (\z q ->
                    (Map.insert q (find_fail q for prev) z, q)) prev (x !! y))
    where
        prev = fail x (y-1) for -- Previous fail state.

-- Finds the state a given state will fail to in the given forward automaton
-- using the given fail structure.
-- Param #1: State we are finding the fail destination for.
-- Param #2: The forward automaton.
-- Param #3: The fail structure.
-- Return: State that is to be reached when failing from the given state.
find_fail::Int -> Forward -> Fail -> Int
find_fail q for fai = Map.findWithDefault 0 (x, y) for
    -- toList here forms pairs from the key and value in the Forward map, that
    -- is ((Int, Char), Int), map_get_key will get the key part (Int, Char) of
    -- this tuple when supplied with the Forward automaton and the value for
    -- the key.
    where
        (z, y) = map_get_key q (Map.toList for)
        x      = Map.findWithDefault 0 z fai

-- Finds the state from which we can get to the given state using the "forward
-- rules" in a forward automaton.
-- Param #1: Destination state.
-- Param #2: List of "forward rules" in a forward automaton.
-- Return: A tuple consisting of source state and the character we need to
--         read in order to get to the destination state.
map_get_key::Int -> [((Int, Char), Int)] -> (Int, Char)
map_ket_key _ [] = error "Trying to get map key from an empty list of pairs."
map_get_key q ((x, y):xy) -- Just find the rule ((Source, Char), Destination
    | q == y    = x       -- that matches the required destination and return
                          -- the source.
    | otherwise =  map_get_key q xy
--------------------------------------------------------------------------------



--------------------------------------------------------------------------------
-- AHO-CORASICK ALGORITHM -----------------------------------------------------|
--------------------------------------------------------------------------------

-- Returns the next state that can be reached from a given state via arc labeled
-- by a given input character.
-- Param #1: A tuple of the source state and the read input character.
-- Param #2: The forward automaton.
-- Param #3: The fail structure.
-- Return: Destination state reached via provided rule.
get_next_state::(Int, Char) -> Forward -> Fail -> Int
get_next_state (q, x) for fai
    -- Such forward arc exists, so return the next state.
    | Map.member (q, x) for = Map.findWithDefault 0 (q, x) for
    -- If there is no arc labeled x from 0, just loop back to 0.
    | q == 0    = 0
    -- No arc forward, look into the fail arcs.
    | otherwise = get_next_state (Map.findWithDefault 0 q fai, x) for fai

-- Returns the output that is supposed to be printed/returned at a given state.
-- Param #1: Current state.
-- Param #2: The output structure.
-- Param #3: The fail structure.
-- Return: List of words found at the state to be printed/returned.
get_output:: Int -> Out -> Fail -> [String]
get_output 0 _ _ = [] -- Starting state has no output!
get_output q out fai
    -- The state is accepting, return it's output.
    | result /= [] = result
    -- The state is not accepting, look in it's fail path for accepting
    -- strings. -> This will find substrings of the searched words that
    -- are also searched words.
    | otherwise = get_output (Map.findWithDefault 0 q fai) out fai -- Shortcuts.
    where
        result = Map.findWithDefault [] q out

-- Interface for the algorithm, calls the underscored version to search
-- through the given text looking for the output strings contained in the
-- given output structure.
-- Param #1: Text to search through.
-- Param #2: The forward automaton.
-- Param #3: The fail structure.
-- Param #4: The output structure.
-- Return: List of tuples consisting of index in the text and a list of all
--         searched words that end at that index.
aho_corasick::String -> Forward -> Fail -> Out -> [(Int, [String])]
-- The two zeros mean starting state and the start of the text.
aho_corasick text for fai out = aho_corasick_ text for fai out 0 0

-- Searches through the given text looking for the output strings contained
-- in the given output structure.
-- Param #1: Text to search through.
-- Param #2: The forward automaton.
-- Param #3: The fail structure.
-- Param #4: The output structure.
-- Param #5: Current state in the forward automaton.
-- Param #6: Current position in the text.
-- Return: List of tuples consisting of index in the text and a list of all
--         searched words that end at that index.
aho_corasick_::String -> Forward -> Fail -> Out -> Int -> Int -> [(Int, [String])]
-- The same check as for the non-empty list version is needed, otherwise the
-- end of text would be treated as an empty match.
aho_corasick_ [] _ fai out q position
    | output_ /= [] = [(position, output_)]
    | otherwise     = []
    where
        output_ = get_output q out fai -- Checks the current state for words
                                       -- ending in it or it's fail states.
aho_corasick_ (w:ws) for fai out q position
    -- This state accepts, add the output to the final list.
    | output_ /= []  = (position, output_):
                                    (aho_corasick_ ws for fai out next_state (position+1))
    -- If it doesn't, simply skip it.
    | otherwise      = aho_corasick_ ws for fai out next_state (position+1)
    where
        next_state = get_next_state (q, w) for fai
        output_    = get_output q out fai

-- Function that constructs all needed structures (that is, the forward
-- automaton, the fail structure and the output structure) and calls the
-- aho-corasick algorithm on the given text with these newly created
-- structures.
-- Param #1: The text to be searched.
-- Param #2: List of the searched words.
-- Return: List of tuples containing a position and a list of searched words
--         ending at that position.
find::String -> [String] -> [(Int, [String])]
find txt searched_words = aho_corasick txt for fai out
    where
        -- Forward automaton.
        for = fst (List.mapAccumL forward Map.empty searched_words)
        -- Fail function.
        fai = fail lists ((length lists) - 1) for
        -- Output function.
        out = output searched_words searched_words for
        lists = bfs_lists for searched_words
--------------------------------------------------------------------------------



--------------------------------------------------------------------------------
-- MAIN -----------------------------------------------------------------------|
--------------------------------------------------------------------------------

-- Main function.
-- Param #1: Name of the file containing the text to be searched.
-- Param #2: String containing searched words delimited by spaces.
main:: IO ()
main = do
        [fname, wordlist] <- getArgs
        text <- readFile fname
        pretty_print (find text (words wordlist))

-- Auxiliary function to print every found occurence on it's own line.
-- Param #1: List of pairs of positions and word lists.
pretty_print::[(Int, [String])] -> IO ()
pretty_print []     = return ()
pretty_print (x:xs) = do
                        putStrLn (show x)
                        pretty_print xs
--------------------------------------------------------------------------------



--------------------------------------------------------------------------------
-- TEST AREA ------------------------------------------------------------------|
--------------------------------------------------------------------------------
-- These tests prove that the algorithm is capable of searching for all possible
-- variants of the searched words (prefix, suffix, substring) at once.

-- The algorithm will find a word and it's suffix in the same occurence.
test1 = find "shore is deep" ["shore", "hore", "ore", "re"]

-- The algorithm will find a word and it's prefix in the same occurence.
test2 = find "shore is deep" ["shore", "shor", "sho", "sh"]

-- The algorithm will find a word and it's substring in the same occurence.
test3 = find "shore is deep" ["shore", "hor"]

-- Test 4 showcases all of the above in one run.
test4 = find "shore is deep" ["shore", "sho", "deep", "dee", "shore is deep"]
--------------------------------------------------------------------------------
