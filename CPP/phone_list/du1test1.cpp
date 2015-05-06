#include "du1.h"

#include <iostream>

/*
Seznam::Jakekoliv_Pracoviste
Seznam::Shoda_Pracoviste
Seznam::Substring_Pracoviste

Seznam::Jakekoliv_Prijmeni
Seznam::Prefix_Prijmeni
*/

void dump( Seznam & s, const Seznam::Balik & b)
{
	for ( Seznam::Balik::const_iterator it = b.begin(); it != b.end(); ++ it)
	{
		Seznam::Handle h = * it;
		std::string id = s.id( h);
		const Seznam::Zaznam & a = s.zaznam( h);
		std::cout << id << " " << a.jmeno << " " << a.prijmeni << " " << a.pracoviste;
		for ( Seznam::Zaznam::telefony_t::const_iterator it2 = a.telefony.begin();
			it2 != a.telefony.end();
			++ it2)
		{
			std::cout << " " << * it2;
		}
		std::cout << std::endl;
	}
}


int main( int argc, char * * argv)
{
	Seznam s;

	{
		Seznam::Zaznam z;

		z.jmeno = "David";
		z.prijmeni = "Bednarek";
		z.pracoviste = "KSI";
		z.telefony.push_back( "4271");
		z.telefony.push_back( "4323");

		Seznam::Handle h2 = s.pridej( z);
		std::string id2 = s.id( h2);
	}

	Seznam::Balik b = s.find( 
		Seznam::Shoda_Pracoviste, "KSI", 
		Seznam::Prefix_Prijmeni, "Bed");

	dump( s, b);

	if ( b.size() )
	{
		Seznam::Handle h = b.front();
		std::string id = s.id( h);
		s.pridej_telefon( h, "4111");
		s.odeber_telefon( h, "4323");
		s.zmen_prijmeni( h, "Bednarkova");
	}

	Seznam::Balik b2 = s.find( 
		Seznam::Shoda_Pracoviste, "KSI", 
		Seznam::Jakekoliv_Prijmeni, "");

	dump( s, b2);

	if ( b2.size() )
	{
		Seznam::Handle h = b2.front();
		s.smaz( h);
	}

	// z.unlock( b);

}