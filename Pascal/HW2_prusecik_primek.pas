program prusecikPrimek;
{Vstupem programu je osm celých čísel Ax, Ay, Bx, By, Cx, Cy, Dx, Dy
 představujících souřadnice čtyř bodů A, B, C, D v Euklidovské rovině.
 Program vytiskne odpověď TRUE, pokud ÚSEČKY AB a CD mají nějaký společný bod,
 nebo FALSE, pokud úsečky AB a CD nemají žádný společný bod.
 Například pro vstupní hodnoty: 0 0 1 1 0 1 1 0 bude odpověď TRUE.}
type
 tVekt = record
         x : integer;
         y : integer;
end;

var
 A,B,C,D : tVekt;  {Variables containint coordinates of input points.}
 fVekt,sVekt,tempVekt : tVekt; {Vector variables.}
 par1,par2 : real;  {Parameters defining the segmented lines.}

{Procedure that gives the output 'TRUE' and halts the program.}
procedure isTrue;
begin
 write('TRUE');
 halt;
end;

{Procedure that gives the output 'FALSE' and halts the program.}
procedure isFalse;
begin
 write('FALSE');
 halt;
end;

{Cross product function.}
function cProd(fVekt, sVekt : tVekt) : integer;
begin
 cProd:= fVekt.x*sVekt.y - fVekt.y*sVekt.x;
end;

begin
 read(A.x,A.y,B.x,B.y,C.x,C.y,D.x,D.y);  {Initial reading of input.}
 
{Are any of those points the same?}
 if ((A.x = C.x) AND (A.y = C.y)) OR ((A.x = D.x) AND (A.y = D.y)) OR
    ((B.x = C.x) AND (B.y = C.y)) OR ((B.x = D.x) AND (B.y = D.y))
 then isTrue;  {If any couple of points (where one point is from first
                segmented line and the other from the second) are equal
                to each other, they are the common point, this program
                outputs TRUE.}

{Are those actual segmented lines?}
 if ((A.x = B.x) AND (A.y = B.y)) OR ((C.x = D.x) AND (C.y = D.y))
 then isFalse; {If the condition is true, there is none or just one
                segmented line, thus program outputs FALSE.}
 
{Finding the directional vectors for input segmented lines.}
 fVekt.x:= B.x - A.x;
 fVekt.y:= B.y - A.y;
 
 sVekt.x:= D.x - C.x;
 sVekt.y:= D.y - C.y;

{Segmented lines are defined as [POINT] + [PARAMETER]*[VECTOR], if
 they intersect, taking those two definitions and making an equation
 out of them, then it yields those parameters from the interval (0,1).}
 tempVekt.x:= C.x - A.x;
 tempVekt.y:= C.y - A.y;

{If cross product of fVekt and sVekt is equal to zero, they are either parallel
 or colinear, if they are colinear then the cross product of tempVekt and fVekt
 is also equal to zero.}
 if cProd(fVekt,sVekt) = 0
 then if cProd(tempVekt,fVekt) = 0 then isTrue
      else isFalse;

 par1:= cProd(tempVekt,sVekt)/cProd(fVekt,sVekt);    {If cProd(f,s)=0 the program}
 par2:= cProd(tempVekt,fVekt)/cProd(fVekt,sVekt);    {would halt at line 71 or 72.}

 if (((par1 <= 1) AND (par1 >= 0)) AND ((par2 <= 1) AND (par2 >= 0))) then isTrue
 else isFalse;

 writeln('FALSE');  {Just in case :)}
end.