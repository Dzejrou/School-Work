program Orientation;
{Robot stojí v počátku souřadné soustavy (souřadnice [0,0,0]) a
 je otočen tak, že doprava vede osa x, nahory osa y a dozadu a ve směru pohledu osa z.
 Příkazy robota jsou zadávány písmeny:
 posun:
 F...vpřed, posune se o krok délky 1 ve směru své orientace
 otočení (otáčí se na místě, vždy o 90 stupňů):
 L...doleva
 R...doprava
 U...nahoru
 D...dolů
 <...na levý bok
 >...na pravý bok
 Program čte ze vstupu znaky representující příkazy a po každém z nich
 vytiskne na zvláštní řádek trojici celých čísel, oddělených mezerou, udávající aktuální polohu robota.
 Vstup je ukončen tečkou ('.').}

{Declaration section START.}
type
 tCoor = record               {Record of coordinates.}
          x : integer;
          y : integer;
          z : integer;
         end;
 tDir = (negX, posX, negY, posY, negZ, posZ);
 tState = record                {Record for robot's state.}
           fwd : tDir;
           lft : tDir;
           up : tDir;
           pos : tCoor;
          end;
 
var
 input : string;  {Input variable.}
 segment : char; {Variable for string segmentation.}
 robot : tState;   {Robot's state variable.}
 progress : array of tCoor;    {Array keeping track of robot's progress.}
 i,j : integer;    {Loop variables.}
{Declaration section END.}

{Procedure and function section START.}
function neg(x : tDir) : tDir;      {Function that returns the negative value of a coordinate.}
begin
 case(x) of
  negX : neg:= posX;
  posX : neg:= negX;
  negY : neg:= posY;
  posY : neg:= negY;
  negZ : neg:= posZ;
  posZ : neg:= negZ;
 end;
end;

procedure turn(var robot : tState;x : char);    {Procedure that turns the robot.}
var temp : tDir;
begin
 case(x) of
  'L' : begin
         temp:= robot.fwd;
         robot.fwd:= robot.lft;
         robot.lft:= neg(temp);
        end;
  'R' : begin
         temp:= robot.fwd;
         robot.fwd:= neg(robot.lft);
         robot.lft:= temp;
        end;
  'U' : begin
         temp:= robot.up;
         robot.up:= neg(robot.fwd);
         robot.fwd:= temp;
        end;
  'D' : begin
         temp:= robot.up;
         robot.up:= robot.fwd;
         robot.fwd:= neg(robot.up);
        end;
  '<' : begin
         temp:= robot.up;
         robot.up:= robot.lft;
         robot.lft:= neg(temp);
        end;
  '>' : begin
         temp:= robot.up;
         robot.up:= neg(robot.lft);
         robot.lft:= temp;
        end;
 end;
end;

procedure moveFwd(var robot : tState);      {Procedure that moves the robot forward.}
begin
 case(robot.fwd) of
  negX : robot.pos.x:= robot.pos.x - 1;
  posX : robot.pos.x:= robot.pos.x + 1;
  negY : robot.pos.y:= robot.pos.y - 1;
  posY : robot.pos.y:= robot.pos.y + 1;
  negZ : robot.pos.z:= robot.pos.z - 1;
  posZ : robot.pos.z:= robot.pos.z + 1;
 end;
end;

procedure moveDec(var robot : tState;x : char);    {Function that decides the movement based on input.}
begin
 if (x = 'F') then moveFwd(robot)
 else if (x = '.') then {Do nothing.}
      else turn(robot,x);
end;

function isDot(x : string) : boolean;
var k : integer;
begin
 for k:=1 to length(x) do if ord(x[k]) = 46 then isDot:=TRUE;
 if (k = length(x)) AND (ord(x[k]) <> 46) then isDot:= FALSE;
end;
{Procedure and function section END.}

{Program section START.}
begin
{Setting the position to 0,0,0.}
 with robot.pos do
  begin
   x:= 0;
   y:= 0;
   z:= 0;
  end;
 
{Setting the directions.}
 with robot do
  begin
   fwd:= posZ;
   lft:= negX;
   up:= posY;
  end;

{First input.}
 readln(input);

 while not (isDot(input)) do    {Input does not contain dot.}
 begin
  SetLength(progress, length(input)+1);
  for i:=1 to length(input) do
   begin
    segment:= input[i];   {First character from input copied.}
    moveDec(robot,segment);
    with progress[i] do
     begin
      x:= robot.pos.x;
      y:= robot.pos.y;
      z:= robot.pos.z;
     end;
   end;
  for j:=1 to length(input) do writeln(progress[j].x,' ',progress[j].y,' ',progress[j].z);
  readln(input);
 end;

 SetLength(progress,length(input));
 for i:=1 to length(input)-1 do        {Input does contain dot.}
  begin
   segment:= input[i];
   moveDec(robot,segment);
   with progress[i] do
    begin
     x:= robot.pos.x;
     y:= robot.pos.y;
     z:= robot.pos.z;
    end;
  end;
 for j:=1 to length(input)-1 do writeln(progress[j].x,' ',progress[j].y,' ',progress[j].z);
end.
{Program section END.}