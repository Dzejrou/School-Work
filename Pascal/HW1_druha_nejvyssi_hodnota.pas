program druhaNejvyssiHodnota;
{V seznamu celych cisel ukoncenem -1, ktere nepatri do seznamu,
 naleznete druhou nejvetsi hodnotu. Seznam ma alespon dve hodnoty
 a cisla se mohou opakovat.}
 
var input : integer;  {Input variable.}
    max,second : integer;  {Biggest and second biggest value.}

begin
 read(input);                            {First input.}
 max:= input;

 read(input);                            {Second input.}
 if input > max then
                  begin
                   second:= max;
                   max:= input;
                  end
 else if input < max then second:= input;
 
 read(input);                            {Rest of inputs is directed by the condition input<>-1.}
 while input <> -1 do
  begin
   if (input > max) then
                      begin
                       second:= max;
                       max:= input;
                      end
   else if (input > second) AND (input <> max) then second:= input;
   read(input);
  end;
  
 write(second);                          {Output.}
end.