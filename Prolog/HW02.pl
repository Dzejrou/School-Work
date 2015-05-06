% Homework #2: 
reload :- consult('HW02.pl'). % Reloads this file.

% Homework solution:
% hist(+X, -Y):- Creates a histogram for the given (sorted) list of numbers
%                X and stores it in the output list Y. (Soting done in the
%                predicate histogram/2)
hist(X, Y) :- nest(X, Z), count(Z, Y). % Team numbers into sublists and count
                                       % them.

% Renaming to histogram(+X, -Y) and sorting.
histogram(X, Y) :- qsort(X, X1), hist(X1, Y).

%% Helper functions:
% nest(+X, -Y) :- Transforms consecutive occurrences of an atom in the given
%                 list X into nested lists within the output list Y.
nest([],[]).
nest([H1|T1], [H2|T2]) :- move(H1, T1, T3, H2), nest(T3, T2).

% move(+U, +V, -W, +Z) :- Moves all occurences of U from the front* of the
%                         list V into the list Z and stores the new version
%                         of V in the list W.
%                         * this is why we need the list sorted before using
%                           hist/2.
move(X, [], [], [X]). % Case of a list filled with a single number.
move(X, [H|T], [H|T], [X]) :- X \= H. % Stops when it finds a different number
                                      % for the first time.
move(X, [X|T1], Y, [X|T2]) :- move(X, T1, Y, T2).

% count(+X, -Y) :- Changes each sublist of X that contains the atom x into
%                  [x, length(sublist)].
count([], []).
count([[H1|T1]|T2], [[H1, C]|T3]) :- length([H1|T1], C), % Substitutes the tail
                                          count(T2, T3). % for the length of the
                                                         % sublist.

% split(+X, +P, -Smaller, -Bigger):- Splits the given list X into two
%                                    lists, Smaller containing the elements
%                                    of X that are smaller than P and Bigger
%                                    containing the elements of X that are
%                                    bigger than P.
split([], _, [], []).
split([H|T], P, [H|Smaller], Bigger) :- H =< P, split(T, P, Smaller, Bigger).
split([H|T], P, Smaller, [H|Bigger]) :- H > P, split(T, P, Smaller, Bigger).

% unify(+X, +Y, -Z) :- Unifies the given lists X and Y and stores the result
%                      in the list Z.
unify([], X, X).
unify([H|T1], X, [H|T2]) :- unify(T1, X, T2).

% qsort(+X, -Y):- Sorts the given list X and stores the sorted
%                 version in the output list Y.
qsort([], []).
qsort([X], [X]).
qsort([H|T], X) :- T \= [], split(T, H, Smaller, Bigger),
                   qsort(Smaller, X1), qsort(Bigger, X2),
                   unify(X1, [H|X2], X).

% Tests:
correct(X) :- write('Correct: '), write(X).
test1(X) :- correct('X = [[1,3],[2,4],[3,2],[4,1]]'),
            histogram([1,2,3,4,2,1,2,3,2,1], X).
test2(X) :- correct('X = []'), histogram([], X).
test3(X) :- correct('X = [[97,16],[98,3],[99,1]]'),
            histogram("baaabaaabaaaaacaaaaa", X).
test4 :- correct('[[32,3],[83,1],[97,2],[101,3],[107,2],[108,1],[111,1]'),
         write(',[112,2],[114,1],[115,3],[118,1]]\n---------'),
         histogram("Skakal pes pres oves", X), write(X).
test5(X) :- correct('X = [[1,1]]'), histogram([1], X).
test6(X) :- correct('X = [[[1],1]]'), histogram([[1]],X).
test7(X) :- correct('X = [[1.4,1],[2.2,2],[10,1],[4,1]'),
            histogram([1.4, 2.2, 10, 2.2, 4], X).
