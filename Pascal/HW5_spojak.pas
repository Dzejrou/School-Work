unit spojak;

interface

type
   PItem = ^TItem;

	TItem = record
		val: LongInt;	{ hodnota prvku }
		next: PItem;	{ ukazatel na dalsi prvek mnoziny }
	end;
	
implementation

end.