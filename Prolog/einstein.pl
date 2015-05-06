% Einstein's question.
reload :- consult('einstein.pl').

% street(house, house, house, house, house)
% house(color, faculty, language, drink, animal)
street(_,
       _,
       house(_,_,_,coffee,_),
       house(blue,_,_,_,_),
       house(_,mu,_,_,_)).

str(A,B,C,D,E) :- h1(A), h2(B), h3(C), h4(D), h5(E).
                  

h1(X) :- next_to(X,Y), h2(Y).
h2(X) :- next_to(X,Y), h3(Y).
h2(X) :- next_to(X,Y), h1(Y).
%h3(X) :- next_to(X,Y), h2(Y).
%h3(X) :- next_to(X,Y), h4(Y).
h3(house(_,_,_,coffee,_)).
%h4(X) :- next_to(X,Y), h3(Y).
%h4(X) :- next_to(X,Y), h5(Y).
h4(house(blue,_,_,_,_)).
%h5(X) :- next_to(X,Y), h4(Y).
h5(house(_,mu,_,_,_)).

house(red,czu,_,_,_).
house(yellow,_,haskell,_,_).
house(_,mff,prolog,_,_).
house(_,_,scala,beer,_).
house(_,up,_,tea,_).
house(green,_,_,biomilk,_).
house(_,cvut,_,_,dog).
house(_,_,lisp,_,parrot).
   
right_of(house(green,_,_,_,_),house(orange,_,_,_,_)).
left_of(X,Y) :- right_of(Y,X).
next_to(house(blue,_,_,_,_),house(_,mu,_,_,_)). %?
next_to(house(_,_,_,coffee,_),house(blue,_,_,_,_)). %?
next_to(house(_,_,lisp,_,_),dum(_,_,_,_,cat)).
next_to(house(_,_,_,_,horse),dum(_,_,haskell,_,_)).
next_to(house(_,_,fsharp,_,_),dum(_,_,_,wine,_)).
next_to(X,Y) :- right_of(X,Y); left_of(X,Y).
