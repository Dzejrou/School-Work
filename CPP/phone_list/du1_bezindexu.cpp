#include "du1_bezindexu.h"

#include <sstream>
#include <algorithm>
#include <iomanip>

Seznam::Balik Seznam::find( 
	Podminka_Pracoviste p_pracoviste, 
	const std::string & s_pracoviste,
	Podminka_Prijmeni p_prijmeni, 
	const std::string & s_prijmeni)
{
	Balik b;

	for ( tabulka_t_::iterator it = tabulka_.begin();
		it != tabulka_.end();
		++ it)
	{
		if ( splneno_( it->second.pracoviste, p_pracoviste, s_pracoviste)
			&& splneno_( it->second.prijmeni, p_prijmeni, s_prijmeni) )
		{
			b.push_back( it->first);
		}
	}

	return b;
}

const std::string & Seznam::id( const Handle & h) const
{
	return h;
}

Seznam::Zaznam & Seznam::zaznam_( const Handle & h)
{
	return tabulka_.find( h)->second;
}

const Seznam::Zaznam & Seznam::zaznam( const Handle & h) const
{
	return tabulka_.find( h)->second;
}

Seznam::Handle Seznam::pridej( const Zaznam & z)
{
	std::ostringstream oss;
	oss << std::setw( 8) << std::setfill( '0') << ++ last_handle_;
	Handle h = oss.str();
	tabulka_.insert( tabulka_t_::value_type( h, z));
	return h;
}

void Seznam::pridej_telefon( const Handle & h, const std::string & telefon)
{
	Zaznam & z = zaznam_( h);
	z.telefony.push_back( telefon);	
}

void Seznam::odeber_telefon( const Handle & h, const std::string & telefon)
{
	Zaznam & z = zaznam_( h);
	Zaznam::telefony_t::iterator it = std::stable_partition( 
		z.telefony.begin(), 
		z.telefony.end(), 
		std::not1( std::bind2nd( std::equal_to< std::string>(), telefon)));
	z.telefony.erase( it, z.telefony.end());
}

void Seznam::zmen_prijmeni( const Handle & h, const std::string & prijmeni)
{
	Zaznam & z = zaznam_( h);
	z.prijmeni = prijmeni;
}

void Seznam::zmen_pracoviste( const Handle & h, const std::string & pracoviste)
{
	Zaznam & z = zaznam_( h);
	z.pracoviste = pracoviste;
}

void Seznam::smaz( const Handle & h)
{
	tabulka_.erase( h);
}

bool Seznam::splneno_( const std::string & s, Podminka_Pracoviste p_pracoviste, const std::string & s_pracoviste)
{
	switch ( p_pracoviste )
	{
		case Jakekoliv_Pracoviste:
			return true;
		case Shoda_Pracoviste:
			return s == s_pracoviste;
		case Substring_Pracoviste:
			return s.find( s_pracoviste) != std::string::npos;
	}
	return false;
}

bool Seznam::splneno_( const std::string & s, Podminka_Prijmeni p_prijmeni, const std::string & s_prijmeni)
{
	switch ( p_prijmeni )
	{
		case Jakekoliv_Prijmeni:
			return true;
		case Prefix_Prijmeni:
			return s.size() >= s_prijmeni.size()
				&& 0 == s.compare( 0, s_prijmeni.size(), s_prijmeni);
				// && s.find( s_prijmeni) == 0;
				// && std::equal( s_prijmeni.begin(), s_prijmeni.end(), s.begin());
	}
	return false;
}

/**/
