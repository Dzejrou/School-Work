% Homework #3:
reload :- consult('HW03.pl'). % Reloads this file.


%%%
% Times:
%   Test #1:     0ms
%   Test #2:     6ms
%   Test #3:    65ms
%   Test #4:   316ms
%   Test #5:    ??ms  -- can't solve:(
%   Test #6:  4048ms
%   Test #7: 32062ms
%%%

% Homework solution:
% solve(+Curr, +Target, -Steps) :- Solves the Lloyds 8 puzzle with the current
%                                  position given in Curr and the target
%                                  position given in Target, saves all the
%                                  steps made in Steps.
solve(Curr, Target, Steps) :- solve_counting(Curr, Target, Steps, 0, 31).

% solve_counting(+Curr, +Target, -Steps, +MinMax, +MaxMax) :- 
%               Starts trying to solve the Lloyds puzzle with a set
%               maximum of steps, given in MinMax, and if it fails,
%               increments MinMax and as long as it is not bigger
%               than MaxMax (where it just gives up) it calls itself
%               with the new MinMax.
% Note: Curr, Target & Steps have the same meaning as in solve/3 above,
%       this simply serves to incrementally check the lowest possible
%       way to solve the puzzle.
solve_counting(Curr, Target, Steps, MinMax, MaxMax) :-
                MinMax =< MaxMax,
                solve(Curr, Target, [], Steps, 0, MinMax);
                N is MinMax + 1,
                solve_counting(Curr, Target, Steps, N, MaxMax).

% solve(+Curr, +Target, +Hist, -Steps, +N, +Max) :-
%               If the given N (indicating the number of this step) is
%               lower or equal to Max, this predicate tries to solve
%               the puzzle by generating a new state New, checking if
%               it is in the history of states stored in Hist and if
%               isn't, calls itself with incremented number of step.
solve(Target, Target, _, _, _, _). % Solved!.
solve(Curr, Target, Hist, [Dir|Steps], N, Max) :- 
            N =< Max, N1 is N + 1,
            action(Curr, New, Dir),
            \+ member(New,Hist), % DRY
            solve(New, Target, [New|Hist], Steps, N1, Max).

% solve_std(+Start, -Steps) :- Uses solve with standard target.
solve_std(Start, Steps) :- solve(Start, [1,2,3,4,5,6,7,8,0], Steps).

% action(+X, -Y, -DIR) :- Performs the action of moving in the DIR direction in
%                         the list X and saves the new list in Y.
action(X,Y,n) :- move_up(X,Y).     % Up.
action(X,Y,p) :- move_right(X,Y).  % Right.
action(X,Y,l) :- move_left(X,Y).   % Left.
action(X,Y,d) :- move_down(X,Y).   % Down.

% Name change for the course:
vyres(X,Y,Z) :- solve(X,Y,Z).
vyresStd(X,Y) :- solve_std(X,Y).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Moves:
%   move_####(+X, -Y) :- Y is the list that is created from X when the hole
%                        moves in the direction ####.
% Note:
%   Y1-Y7 represent values that do not concern us in that particular move,
%   X is the value that is on the target location and 0 represents the hole.

%%%%%
%% UP:
% Moving from the middle row to the top row:
move_up([X,Y1,Y2, 0,Y3,Y4, Y5,Y6,Y7], [0,Y1,Y2, X,Y3,Y4, Y5,Y6,Y7]).
move_up([Y1,X,Y2, Y3,0,Y4, Y5,Y6,Y7], [Y1,0,Y2, Y3,X,Y4, Y5,Y6,Y7]).
move_up([Y1,Y2,X, Y3,Y4,0, Y5,Y6,Y7], [Y1,Y2,0, Y3,Y4,X, Y5,Y6,Y7]).
% Moving from the bottom row to the middle row:
move_up([Y1,Y2,Y3, X,Y4,Y5, 0,Y6,Y7], [Y1,Y2,Y3, 0,Y4,Y5, X,Y6,Y7]).
move_up([Y1,Y2,Y3, Y4,X,Y5, Y6,0,Y7], [Y1,Y2,Y3, Y4,0,Y5, Y6,X,Y7]).
move_up([Y1,Y2,Y3, Y4,Y5,X, Y6,Y7,0], [Y1,Y2,Y3, Y4,Y5,0, Y6,Y7,X]).

%%%%%%%
%% DOWN:
% move_down is just inverted move_up:
move_down(X,Y) :- move_up(Y,X).

%%%%%%%
%% LEFT:
% Moving from the middle column to the left column:
move_left([X,0,Y1, Y2,Y3,Y4, Y5,Y6,Y7], [0,X,Y1, Y2,Y3,Y4, Y5,Y6,Y7]).
move_left([Y1,Y2,Y3, X,0,Y4, Y5,Y6,Y7], [Y1,Y2,Y3, 0,X,Y4, Y5,Y6,Y7]).
move_left([Y1,Y2,Y3, Y4,Y5,Y6, X,0,Y7], [Y1,Y2,Y3, Y4,Y5,Y6, 0,X,Y7]).
% Moving from the right column to the middle column:
move_left([Y1,X,0, Y2,Y3,Y4, Y5,Y6,Y7], [Y1,0,X, Y2,Y3,Y4, Y5,Y6,Y7]).
move_left([Y1,Y2,Y3, Y2,X,0, Y5,Y6,Y7], [Y1,Y2,Y3, Y2,0,X, Y5,Y6,Y7]).
move_left([Y1,Y2,Y3, Y4,Y5,Y6, Y7,X,0], [Y1,Y2,Y3, Y4,Y5,Y6, Y7,0,X]).

%%%%%%%%
%% RIGHT:
% move_right is just inverted move_left:
move_right(X,Y) :- move_left(Y,X).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Tests:
test1 :- solve_std([1,2,3,4,5,6,7,8,0],X), length(X,N),
         write(N), write(' '), write(X).
test2 :- solve_std([5,8,2,1,0,3,4,7,6],X), length(X,N),
         write(N), write(' '), write(X).
test3 :- solve_std([8,0,2,5,7,3,1,4,6],X), length(X,N),
         write(N), write(' '), write(X).
test4 :- solve_std([8,7,0,5,4,2,1,6,3],X), length(X,N),
         write(N), write(' '), write(X).
test5 :- solve_std([2,4,3,5,1,7,0,6,8],X), length(X,N),
         write(N), write(' '), write(X).
test6 :- solve_std([8,4,7,0,6,2,5,1,3],X), length(X,N),
         write(N), write(' '), write(X).
test7 :- solve_std([8,6,7,2,5,4,3,0,1],X), length(X,N),
         write(N), write(' '), write(X).
