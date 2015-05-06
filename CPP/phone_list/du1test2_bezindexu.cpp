#include "du1_bezindexu.h"

#include <iostream>
#include <ctime>


std::size_t randnum( std::size_t domain_size)
{
	int r1 = std::rand();
	int r2 = std::rand();

	// RAND_MAX has different value on Fedora64 with Clang++
	long long r = (long long)r1 * (32767 + 1) + (long long)r2;

	return (std::size_t)(r % domain_size);
}

std::string randstring( std::size_t size, std::size_t domain_size)
{
	long long rr = randnum( domain_size);

	std::string s;

	for (; size > 0; -- size)
	{
		char ch = 'a' + (char)(rr % 10);

		rr = (rr * 729 + 21) % 1021;

		s.push_back( ch);
	}

	return s;
}

void filldomain( std::vector< std::string> & v, std::size_t size, std::size_t domain_size)
{
	for ( std::size_t c = domain_size; c > 0; -- c)
	{
		v.push_back( randstring( size, domain_size));
	}
}

void fill( 
		  Seznam & s, 
		  std::size_t c,
		  std::size_t jmeno_s, 
		  std::size_t jmeno_ds, 
		  std::size_t prijmeni_s, 
		  std::size_t prijmeni_ds, 
		  std::size_t pracoviste_s, 
		  std::size_t pracoviste_ds, 
		  std::size_t telefon_c,
		  std::size_t telefon_s, 
		  std::size_t telefon_ds)
{
	std::vector< std::string> v_jmeno;
	std::vector< std::string> v_prijmeni;
	std::vector< std::string> v_pracoviste;
	std::vector< std::string> v_telefon;

	filldomain( v_jmeno, jmeno_s, jmeno_ds);
	filldomain( v_prijmeni, prijmeni_s, prijmeni_ds);
	filldomain( v_pracoviste, pracoviste_s, pracoviste_ds);
	filldomain( v_telefon, telefon_s, telefon_ds);

	for (; c > 0; -- c)
	{
		Seznam::Zaznam z;

		z.jmeno = v_jmeno[ randnum( jmeno_ds)];
		z.prijmeni = v_prijmeni[ randnum( prijmeni_ds)];
		z.pracoviste = v_pracoviste[ randnum( pracoviste_ds)];

		for ( std::size_t cc = telefon_c; cc > 0; -- cc)
		{
			z.telefony.push_back( v_telefon[ randnum( telefon_ds)]);
		}

		s.pridej( z);
	}
}

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

std::size_t scan( Seznam & s, const Seznam::Balik & b)
{
	for ( Seznam::Balik::const_iterator it = b.begin(); it != b.end(); ++ it)
	{
		Seznam::Handle h = * it;
		std::string id = s.id( h);
		const Seznam::Zaznam & a = s.zaznam( h);
		for ( Seznam::Zaznam::telefony_t::const_iterator it2 = a.telefony.begin();
			it2 != a.telefony.end();
			++ it2)
		{
		}
	}
	return b.size();
}


int main( int argc, char * * argv)
{
	Seznam s;

	std::clock_t c0 = std::clock();

	fill( s, 10000, 5, 10, 8, 1000, 30, 300, 3, 4, 10000);

	std::clock_t c1 = std::clock();

#ifdef _DEBUG
	std::size_t n = 100;
	std::size_t n2 = 100;
	std::size_t n3 = 100;
#else
	std::size_t n = 10000;
	std::size_t n2 = 10000;
	std::size_t n3 = 10000;
#endif
	std::size_t ss1;

	for ( std::size_t c = n; c > 0; -- c)
	{ // "aaah"
		Seznam::Balik b = s.find( 
			Seznam::Substring_Pracoviste, "aaah", 
			Seznam::Jakekoliv_Prijmeni, "");

		ss1 = scan( s, b);
	}

	std::clock_t c3 = std::clock();

	std::size_t ss2;

	for ( std::size_t c = n2; c > 0; -- c)
	{
		Seznam::Balik b2 = s.find( 
			Seznam::Jakekoliv_Pracoviste, "", 
			Seznam::Prefix_Prijmeni, "ahh");

		ss2 = scan( s, b2);
	}

	std::clock_t c5 = std::clock();

	std::size_t ss3;

	for ( std::size_t c = n3; c > 0; -- c)
	{
		Seznam::Balik b3 = s.find( 
			Seznam::Substring_Pracoviste, "aaah", 
			Seznam::Prefix_Prijmeni, "ahh");

		ss3 = scan( s, b3);
	}

	std::clock_t c7 = std::clock();

	std::cout << "fill: " << ((double)1000 * ( c1 - c0) / CLOCKS_PER_SEC) << std::endl;
	std::cout << "substring_pracoviste: " << ss1 << " " << ((double)1000 * ( c3 - c1) / n / CLOCKS_PER_SEC) << std::endl;
	std::cout << "prefix_prijmeni: " << ss2 << " " << ((double)1000 * ( c5 - c3) / n2 / CLOCKS_PER_SEC) << std::endl;
	std::cout << "sp+pp: " << ss3 << " " << ((double)1000 * ( c7 - c5) / n3 / CLOCKS_PER_SEC) << std::endl;

	return 0;
}
