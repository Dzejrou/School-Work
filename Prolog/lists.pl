/**
 * First part of 99 problems in prolog solutions.
 * By Dzejrou 2015.
 */
 reload :- consult('lists.pl'). % Reload this file.



/* 1: Find the last element of a list: */
last_ele(X, [X]).
last_ele(X, [_|Y]) :- last_ele(X,Y).

test1(X) :- last_ele(X, [a, b, c, d, e, f, g]).
/* ------- */



/* 2: Find the last but one element of a list: */
last_but1_(X, X, [_]).
last_but1_(X, _, [H|T]) :- last_but1_(X, H, T).
last_but1(X, [H|T]) :- last_but1_(X, H, T).

test2(X) :- last_but1(X, [a, b, c, d, e, f, g]).
/* ------- */



/* 3: Find the k-th element of a list: */
element_at_(X, [X|_], Pos, Curr) :- Pos >= Curr, Curr >= Pos. % Forgot ==.
element_at_(X, [_|T], Pos, Curr) :- element_at_(X, T, Pos, Curr + 1).
element_at(X, List, Pos) :- element_at_(X, List, Pos, 1).

test3(X) :- element_at(X, [a,b,c,d,e,f], 4).
/* ------- */

/* 4: Find the number of elements in a list: */
len_([], Len, Len). % TODO: Return a number, not Pean thingy.
len_([_|T], Len, Curr) :- len_(T, Len, s(Curr)).
len(X, Y) :- len_(X, Y, 0).

test4(X) :- len([a,b,c,d,e,f,g], X).
/* -------- */



/* 5: Reverse a list: */
% TODO:
append_([], X, X).
append_([H|T], X, [H|Y]) :- append_(T, X, Y).
recerse_list([], X, X).
reverse_list([H|T], Y, X) :- append_(H, Y, Z), reverse_list(T, Z, X).

test5(X) :- reverse_list([a, b, c, d, e, f, g], [], X).
/* ------- */






/* !EOF! */
