-- HW01: Create a function that splits a list into two sublists
--       depending on whether they did or did no pass a test given
--       in the form of an argument function.

-- split_with F X: F is the function that tests the elements and
--                 X is the list of elements that are to be tested.
-- Note: In the resulting pair, first are the elements that passed
--       the test, second are the elements that failed.
split_with::(a -> Bool) -> [a] -> ([a], [a])
split_with _ [] = ([], []) -- Just in case someone passes an empty list.
split_with f [x] | f x       = ([x], []) -- Bottom case, test the element
                 | otherwise = ([], [x]) -- and return it in the right list.
-- Test the current head element and add it as head into the respective
-- result list, tails are computed in the recursive call.
split_with f (x:xs) | f x = (x:ys, zs)
                    | otherwise = (ys, x:zs)
                    where (ys, zs) = split_with f xs

-- To match the homework function name:
rozdelPodle = split_with
