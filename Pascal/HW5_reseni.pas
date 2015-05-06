unit reseni;

interface

uses HW5_spojak;

{ nedestruktivni sjednoceni mnozin S1 a S2 }
function union(S1, S2: PItem): PItem;

{ nedestruktivni prunik mnozin S1 a S2 }
function intersection(S1, S2: PItem): PItem;

{ destruktivni sjednoceni mnozin S1 a S2, vstupni seznamy jsou zniceny a
	promenne S1, S2 jsou nastaveny na nil }
function unionDestruct(var S1, S2: PItem): PItem;

{ destruktivni prunik mnozin S1 a S2, vstupni seznamy jsou zniceny a
	promenne S1, S2 jsou nastaveny na nil }
function intersectionDestruct(var S1, S2: PItem): PItem;




implementation

{Adds a given value as a member to given list.}
procedure addVal(var gList : PItem; gValue : longint);
var temp : PItem;
begin
 new(temp);
 temp^.val:= gValue;
 temp^.next:= gList^.next;
 gList^.next:= temp;
 gList:= gList^.next;
end;

{Adds a given list member to target given list.}
procedure addListMem(var gTarList : PItem; gList : PItem);
begin
 gList^.next:= gTarList^.next;
 gTarList^.next:= gList;
 gTarList:= gTarList^.next;
end;


{Creates a list consisting of members of two given lists.}
function union(S1, S2: PItem): PItem;
var temp1, temp2 : PItem;
begin
 new(temp1);
 temp1^.next:= temp1;
 
 while (S1 <> NIL) AND (S2 <> NIL) do
  begin
   if (S1^.val < S2^.val) then
    begin
     addVal(temp1, S1^.val);
     S1:= S1^.next;
    end
   else
    begin
     if (S1^.val = S2^.val) then S1:= S1^.next;
     addVal(temp1,S2^.val);
     S2:= S2^.next;
    end;
  end;

 temp2:= temp1^.next;
 if (S1 <> NIL) then temp1^.next:= S1
 else temp1^.next:= S2;

 union:= temp2^.next;
 dispose(temp2);
end;
{Union END.}

{Creates a list consisting of common members of two given lists.}
function intersection(S1, S2: PItem): PItem;
var temp1, temp2 : PItem;
begin
 new(temp1);
 temp1^.next:= temp1;
 
 while (S1 <> NIL) AND (S2 <> NIL) do
  begin
   if (S1^.val < S2^.val) then S1:= S1^.next
   else
    begin
     if (S1^.val = S2^.val) then
      begin
       addVal(temp1,S1^.val);
       S1:= S1^.next;
      end;
     S2:= S2^.next;
    end;
  end;

 temp2:= temp1^.next;
 temp1^.next:= NIL;

 intersection:= temp2^.next;
 dispose(temp2);
end;
{Intersection END.}


{Creates a unified set whilst destroying originals.}
function unionDestruct(var S1, S2: PItem): PItem;
var temp1, temp2 : PItem;
begin
 new(temp1);
 temp1^.next:= temp1;
 
 while (S1 <> NIL) AND (S2 <> NIL) do
  begin
   if (S1^.val < S2^.val) then
    begin
     temp2:= S1;
     S1:= S1^.next;
     addListMem(temp1, temp2);
    end
   else
    begin
     if (S1^.val = S2^.val) then
      begin
       {Temp2 points where S1 points, S1 points to the second member in list,}
       {temp2 (pointing to first) gets disposed.)}
       temp2:= S1;
       S1:= S1^.next;
       dispose(temp2);
      end;
     temp2:= S2;
     S2:= S2^.next;
     addListMem(temp1, temp2);
    end;
  end;

 temp2:= temp1^.next;

 if (S1 <> NIL) then
  begin
   temp1^.next:= S1;
   S1:= NIL;
  end
 else
  begin
   temp1^.next:= S2;
   S2:= NIL;
  end;

 unionDestruct:= temp2^.next;
 dispose(temp2);
end;
{Union Destruct END.}


function intersectionDestruct(var S1, S2: PItem): PItem;
var temp1, temp2 : PItem;
begin
 new(temp1);
 temp1^.next:= temp1;

 while (S1 <> NIL) AND (S2 <> NIL) do
  begin
   if (S1^.val < S2^.val) then
    begin
     temp2:= S1;
     S1:= S1^.next;
     dispose(temp2)
    end
   else
    begin
     {Equal = common member.}
     if (S1^.val = S2^.val) then
      begin
       temp2:= S1;
       S1:= S1^.next;
       addListMem(temp1, temp2);
      end;
     temp2:= S2;
     S2:= S2^.next;
     dispose(temp2);
    end;
  end;

 temp2:= temp1^.next;
 temp1^.next:= NIL;
 
 intersectionDestruct:= temp2^.next;
 dispose(temp2);

 {Taking out the trash.}
 while (S1 <> NIL) do
  begin
   temp2:= S1;
   S1:= S1^.next;
   dispose(temp2);
  end;
  
 while (S2 <> NIL) do
  begin
   temp2:= S2;
   S2:= S2^.next;
   dispose(temp2);
  end;
end;


end.
{Unit END.}
