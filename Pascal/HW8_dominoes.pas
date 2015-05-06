program dominoes;

{Declaration START.}
type tArray = array[1..38,1..38] of shortint;   {2D array type.}

var input1, input2, maxLength, dCount : shortint;
    a : tArray;   {Array representing dominoes.}
    i,j : shortint;     {Loop variables.}
{Declaration END.}

{Subprograms START.}
{Procedure that recursively searches the 2D array for potential usable dominoes
 incrementing the domino row length (domLength) and setting the maxLength variable
 to domLength if needed.}
procedure recSearch( prevDomino, domLength : shortint);
var i,j : shortint;
begin
 for i:= 1 to 38 do
  if a[prevDomino, i] > 0 then
   begin
{DEBUG writeln('Decrementing: [',prevDomino,',',i,'] [',i,',',prevDomino,'].');}
    Dec(a[prevDomino,i]); Dec(a[i,prevDomino]);
    
{DEBUG writeln('Processing recSearch with parameters: [',i,',',domLength+1,'].');}
    recSearch(i,domLength+1);
    
{DEBUG writeln('Incrementing: [',prevDomino,',',i,'] [',i,',',prevDomino,'].');}
    Inc(a[i,prevDomino]); Inc(a[prevDomino,i]);
   end;

 if domLength > maxLength then maxLength:= domLength;
end;
{Subprograms END.}

{Program START.}
begin
{Input of the number of dominoes.}
 read(dCount);

{Initialization of the array to zeros.}
 for i:= 1 to 38 do
  for j:= 1 to 38 do a[i,j]:= 0;

{Dominoes input.}
 for i:= 1 to dCount do
  begin
   read(input1,input2);
   {Incrementing represents increasing the stack of this kind of domino
    by one.}
   Inc(a[input1,input2]);
   Inc(a[input2,input1]);
  end;

{Searching for maxLength.}
 maxLength:= 0;
 for i:= 1 to 38 do
  begin
{DEBUG writeln('Processing recSearch with parameters: [',i,',',0,'].');}
   recSearch(i,0);
   if maxLength = dCount then break;
  end;

{Output of maxLength variable.}
 writeln(maxLength);
end.
{Program END.}