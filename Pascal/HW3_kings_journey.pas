program kingsJourney;
{Program bude hledat nejkratší cestu šachovým králem na šachovnici 8x8,
 kde na některá políčka nelze vstoupit.
 Vstup programu obsahuje popořadě:
  počet překážek
  souřadnice jednotlivých překážek (dvojice čísel v rozsahu 1..8)
  souřadnice výchozího políčka
  souřadnice cílového políčka
 Počet překážek je zapsán na samostatném řádku, souřadnice jsou vždy zapsány
 jako dvojice čísel na jednom řádku oddělená mezerou.
 Výstup je buď -1, pokud král na cílové políčko nemůže dojít NEBO počet kroků,
 které musí vykonat.}
const
 chessBSize = 8;            {Number of fields in one row or column.}
 fieldCount = sqr(chessBSize);        {Total number of fields.}
 moveOptions = 8;    {Number of fields, where can king theoretically move.}
 
type
 tRange = 1..chessBSize;             {Range for array.}
 tCoor = record                      {Coordinates type.}
          x : integer;
          y : integer;
         end;
 tDynArray = array of tCoor;         {Dynamic array of tCoor type.}
 tVekt = array[1..moveOptions] of tCoor;    {Vector of coordinates.}
 
var
 chessB : array[tRange,tRange] of integer;   {Chess board array.}
 obs : tDynArray;   {Array with obstacle coordinates.}
 obsQuant : integer;   {Quantity of obstacles.}
 start, fin : tCoor;   {Coordinates of starting and ending point.}
 i,j : integer;   {Loop control variable.}
 queue : array[1..fieldCount] of tCoor;      {The queue variable.}
 cField : tCoor;    {Current field variable.}
 sQue,eQue : integer;   {Start and end of the queue.}
 step : integer;   {Variables counting steps.}
 maxQue : integer;   {Variable telling us how many fields can go to queue.}
 
{Outputs number of steps and halts the program.}
procedure isTRUE(x : integer);
begin
 write(x);
 halt;
end;

{Outputs -1 and halts the program.}
procedure isFALSE;
begin
 write('-1');
 halt;
end;

{Returns TRUE if field (x,y) is an obstacle, returns FALSE otherwise.}
function isObs(x,y : integer) : boolean;
var k : integer;
begin
 for k:=1 to obsQuant-1 do
  if (obs[k].x = x) AND (obs[k].y = y) then
   begin
    isObs:= TRUE;
    break;
   end
  else isObs:= FALSE;
end;

{Returns 8 fields that the king can access.}
function move(x,y : integer) : tVekt;
{Clockwise movement.}
begin
 move[1].x:= x;     {UP}
 move[1].y:= y+1;

 move[2].x:= x+1;   {UP RIGHT}
 move[2].y:= y+1;
 
 move[3].x:= x+1;   {RIGHT}
 move[3].y:= y;
 
 move[4].x:= x+1;   {DOWN RIGHT}
 move[4].y:= y-1;
 
 move[5].x:= x;     {DOWN}
 move[5].y:= y-1;
 
 move[6].x:= x-1;   {DOWN LEFT}
 move[6].y:= y-1;
 
 move[7].x:= x-1;   {LEFT}
 move[7].y:= y;
 
 move[8].x:= x-1;   {UP LEFT}
 move[8].y:= y+1;
end;

{Program START}
begin
 {Obstacle input START}
 readln(obsQuant);
 obsQuant:= obsQuant + 1;         {Pascal sets dynamic array to 0..UB-1.}
 SetLength(obs,obsQuant);
 {Leaving obs[0] not initialized.}
 for i:=1 to obsQuant-1 do
  begin
   read(obs[i].x);
   readln(obs[i].y);
  end;
 {Obstacle input END}
 
 {Start and fin input START}
 read(start.x);
 readln(start.y);

 read(fin.x);
 read(fin.y);
 {Start and fin input END}
 
 {Chess board initialization START}
 for i:=1 to chessBSize do     {Horizontal - x coordinate.}
  for j:=1 to chessBSize do    {Vertical - y coordinate.}
  begin
   if (start.x = i) AND (start.y = j) then chessB[i,j]:= 0  {0 is 'start' value.}
        else if isObs(i,j) then chessB[i,j]:= -1 {-1 is 'obstacle' value.}
             else chessB[i,j]:= -2;     {-2 are unvisited fields.}
  end;
 {Chess board initialization END}

 {Wave algorithm START}
 sQue:= 1;
 eQue:= 1;                  {Setting start and end of the queue to the first element of the queue.}

 queue[sQue].x:= start.x;     {Initializing first field in queue to the start field.}
 queue[sQue].y:= start.y;

 while chessB[fin.x,fin.y] = -2 do
  begin
   cField.x:= queue[sQue].x;
   cField.y:= queue[sQue].y;
   sQue:= sQue + 1;
   step:= chessB[cField.x,cField.y] + 1;
   for i:=1 to moveOptions do
    if (move(cField.x,cField.y)[i].x >= 1) AND (move(cField.x,cField.y)[i].x <= chessBSize)
    AND (move(cField.x,cField.y)[i].y >= 1) AND (move(cField.x,cField.y)[i].y <= chessBSize)
    then begin
          if (chessB[move(cField.x,cField.y)[i].x,move(cField.x,cField.y)[i].y] = -2) then
           begin
            chessB[move(cField.x,cField.y)[i].x,move(cField.x,cField.y)[i].y]:= step;
            eQue:= eQue + 1;
            queue[eQue].x:= move(cField.x,cField.y)[i].x;
            queue[eQue].y:= move(cField.x,cField.y)[i].y;
  {debug    writeln('Loop ended. eQue = ',eQue,', i =  ',i,' Coordinates = ', cField.x,',',cField.y,' Step ',step,'.','Adding to queue = ',move(cField.x,cField.y)[i].x,',',move(cField.x,cField.y)[i].y,'.'); readln; readln;  debug}
           end;
         end;
    if (eQue = fieldCount - obsQuant) then break;
  end;
 {Wave algorithm END}
 if chessB[fin.x,fin.y] <> -2 then isTrue(step)
 else isFalse;
end.
{Program END}