% Homework #1:
% Write a procedure to replace all occurrences of an atom in a list.
reload :- consult('HW01.pl'). % Reloads this file.

% Homework solution: sub/4
% sub(+X, +A, +B, -Y) - Substitutes all occurrences of A in the input
%                       list X with B and stores the new list in the
%                       output list Y.
% (Although sub(?X, ?A, ?B, ?Y) is possible. See tests.)
sub([],_,_,[]). % Bottom case, nothing to sub.
sub([A|T1],A,B,[B|T2]) :- sub(T1,A,B,T2). % Match found, sub and continue.
sub([X|T1],A,B,[X|T2]) :- sub(T1,A,B,T2), % No match, just continue.
                          X \= A. % Without this, it would allow identity
                                  % for searched atoms.

% Tests: (1-5 Martin's, 6 onwards mine)
correct(X) :- write('Correct: '), write(X). % Write correct output before test.
test1(X) :- correct([1,2,5,4]), sub([1,2,3,4],3,5,X).
test2(X) :- correct('X = 5'), sub([1,2,3,4],3,X,[1,2,5,4]).
test3(X,Y) :- correct('X = 3, Y = 5'), sub([1,2,3,4],X,Y,[1,2,5,4]).
test4(X,Y,Z) :- sub([1,2,3,4],X,Y,Z). % Correct/1 too verbose.
test5 :- correct('no'), sub([1,2,3,4],3,4,[1,2,5,4]).
% Note: sub(-X, +A, +B, +Y) will always return Y, which is fine.
test6(X) :- correct('[1,2,3,4]; [1,7,3,4]'), sub(X,2,7,[1,7,3,4]).
test7(X) :- correct('X = 3'), sub([1,1,1,1],1,X,[3,3,3,3]).
test8(X) :- correct('no'), sub([1,1,1,1],1,X,[3,3,2,3]).
