-- Program that parses a formula and can either convert it to an infix tree
-- or evaluate it.
-- NOTE: In the case of some wrong formula formats (see test6),
--       it will print the infix tree up until where the formula breaks
--       and throws an error with the rest as a list.

data Tree
    = Add Tree Tree
    | Sub Tree Tree
    | Mul Tree Tree
    | Div Tree Tree
    | Num Int
    deriving Show

-- Transforms an expression in prefix into a tree.
string_to_tree::String -> Tree
string_to_tree s = to_infix_tree (words s)

-- Called by string_to_tree after parsing the input,
-- deals with individual tokens.
to_infix_tree::[String] -> Tree
to_infix_tree [] = error "Wrong format." -- Not enough operands/operators.
to_infix_tree [x] = Num (read x ::Int)
to_infix_tree (x:xs)
    | x == "+" = Add t1 t2
    | x == "-" = Sub t1 t2
    | x == "*" = Mul t1 t2
    | x == "/" = Div t1 t2
    where
        t1 = to_infix_tree (take (expr_len xs) xs)
        t2 = to_infix_tree (drop (expr_len xs) xs)
-- Wrong order of expression members.
to_infix_tree x = error ("Wrong prefix formula format: " ++ show x)

-- Evaluates a formula given as a string of operators and operands
-- separated by spaces in prefix form.
eval_formula::String -> Int
eval_formula s = eval_tree (string_to_tree s)

-- Evaluates an infix tree of a formula.
eval_tree::Tree -> Int
eval_tree (Num x) = x
eval_tree (Add t1 t2) = (eval_tree t1) + (eval_tree t2)
eval_tree (Sub t1 t2) = (eval_tree t1) - (eval_tree t2)
eval_tree (Mul t1 t2) = (eval_tree t1) * (eval_tree t2)
eval_tree (Div t1 t2) = (eval_tree t1) `div` (eval_tree t2)

-- Returns number of elements that are in the parsed tree to the right
-- of the operator x in the expression x:str.
-- The 1 passed to expr_len_ stands for the operator x.
expr_len::[String] -> Int
expr_len s = expr_len_ s 1 0

-- Accumulator version used to calculate the expr_len function.
expr_len_::[String] -> Int -> Int -> Int
expr_len_ _ 0 m = m
expr_len_ [] n m
    | n == 0 = m
    | otherwise = error("Wrong format.")
expr_len_ (x:xs) n m
    | is_operator x = expr_len_ xs (n + 1) (m + 1)
    | otherwise     = expr_len_ xs (n - 1) (m + 1)

-- Returns true if x is an operator.
is_operator::String -> Bool
is_operator x
    | x == "+" = True
    | x == "-" = True
    | x == "*" = True
    | x == "/" = True
    | otherwise = False

-- Name changes to match the assignment:
prefixNaStrom = string_to_tree
vyhodnotPrefix = eval_formula

-- Tests:
test1 = string_to_tree "+ 1 1"
test2 = eval_formula "+ 1 1"
test3 = string_to_tree "- * / 15 - 7 + 1 1 3 + 2 + 1 1"
test4 = eval_formula "- * / 15 - 7 + 1 1 3 + 2 + 1 1"
test5 = eval_formula "+ 1"
test6 = string_to_tree "+ 1 1 4"
