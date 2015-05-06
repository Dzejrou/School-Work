program prog1v100;
{Program that calculates most effective way to
 eliminate enemies in a competition out of 100
 over input number of rounds.}

{Declaration START.}
const ENEMIES = 100; {Number of enemies in the start.}

type tArray = array of integer;  {Dynamic array type.}
     tSolution = record  {Solution record type.}
                  val : longint;
                  elim : tArray;
                 end;

var input : integer;  {Number of rounds.}
    eliminated : tArray;  {Array keeping the number of enemies
                           eliminated each round.}
    bestSolution : tSolution;  {Record that keeps highest win value and
                                the process of elimination need for it.}
    i : integer;  {Loop variables.}
    temp : integer;  {Temporary variables.}

{Declaration END.}

{Function that calculates the prize value
 based on input number of enemies eliminated
 and input number of remaining enemies.}
function earnings( x, rem : integer) : longint;
begin
 earnings:= Trunc(100000*x div rem);
end;
{Prize function END.}

{Function that calculates the amount won in
 the competition based on array containing
 number of enemies eliminated each turn.}
function amountWon( x : tArray) : longint;
var i,rem : integer;
begin
 amountWon:= 0;
 rem:= ENEMIES;
 
 for i:= 0 to length(x)-1 do
  begin
   amountWon:= amountWon + earnings(x[i], rem);
   rem:= rem - x[i];
  end;
end;
{AmountWon function END.}

{Function that calculates the sum of array's elements.}
function sumOfArray( x : tArray) : longint;
var i : integer;
begin
 sumOfArray:= 0;

 for i:= 0 to length(x)-1 do
  begin
   sumOfArray:= sumOfArray + x[i];
  end;
end;
{SumOfArray function END.}

{Procedure that sets input array as bestSolution.elim members
 amount won by using that array as bestSolution.val.}
procedure addSolution( x : tArray);
var i : integer;
begin
 setLength(bestSolution, length(x));
 with bestSolution do
  begin
   val:= amountWon(x);
   for i:= 0 to length(x)-1 do elim[i]:= x[i];
  end;
end;
{AddSolution procedure END.}

function findSet( x : tArray, remPlrs, round : integer) : integer;
var i : integer;
begin
 for i:= round to length(x)-1 do
  begin
   {$B-}
   if (i = length(x) - 1) AND sumOfArray(x) > sumOfArray(bestSolution.elim) then addSolution(x)
   else begin





        end;

    
  end;
end;

{Program START.}
begin
 readln(input);
 setLength(eliminated, input);

 for i:= 1 to length(eliminated)-1 do eliminated[i]:= 1;
 eliminated[0]:= ENEMIES - i;
 
 findSet(input, eliminated, ENEMIES);
 
 writeln(bestSolution.val);
 for i:= 0 to length(bestSolution.elim)-1 do write(bestSolution.elim[i],' ');
end.
{Program END.}