program decomposition;
{Na standardním vstupu je zadáno číslo N (1 ≤ N ≤ 40).
 Vypište na standardní výstup všechny možnosti,
 jak toto číslo rozložit na součet celých kladných čísel.
 Každý rozklad musí být uveden na samostatném řádku,
 sčítanci vyjmenováni od nejmenšího k největšímu a odděleny
 znaménkem "+". Na pořadí řádků nezáleží.}
 
var
 input : integer;
 members : array[1..40] of integer;

{Decomposition procedure that recursively decomposes a number.}
procedure decomposition(remSum, index, lb : integer);     {RemSum = sum of remaining members, index = max array index, lb = lower bound for members.}
var i,j : integer;
begin
{DEBUG START writeln('Decomposing with parameters (',remSum,',',index,',',lb,').'); DEBUG END.}
 for i:= lb to remSum do
  begin
   members[index]:= i;
   if i = remSum then
    begin
     write(members[1]);
     for j:= 2 to index do write('+', members[j]);
     writeln;
    end else decomposition(remSum-i,index+1,i);
  end;
end;
{Decomposition procedure END.}

{Program START.}
begin
 readln(input);
 
 decomposition(input,1,1);
end.
{Program END.}