#ifndef du1_h_
#define du1_h_

#include <string>
#include <vector>
#include <map>

class Seznam
{
public:
	struct Zaznam
	{
		typedef std::vector< std::string> telefony_t;

		std::string jmeno;
		std::string prijmeni;
		std::string pracoviste;
		telefony_t telefony;
	};

	typedef std::string Handle;
	typedef std::vector< Handle> Balik;

	enum Podminka_Pracoviste
	{
		Jakekoliv_Pracoviste,
		Shoda_Pracoviste,
		Substring_Pracoviste
	};

	enum Podminka_Prijmeni
	{
		Jakekoliv_Prijmeni,
		Prefix_Prijmeni
	};

	Seznam()
		: last_handle_( 0)
	{
	}

	Balik find( 
		Podminka_Pracoviste p_pracoviste, 
		const std::string & s_pracoviste,
		Podminka_Prijmeni p_prijmeni, 
		const std::string & s_prijmeni);

	const std::string & id( const Handle & h) const;

	const Zaznam & zaznam( const Handle & h) const;

	Handle pridej( const Zaznam & z);

	void pridej_telefon( const Handle & h, const std::string & telefon);

	void odeber_telefon( const Handle & h, const std::string & telefon);

	void zmen_prijmeni( const Handle & h, const std::string & prijmeni);

	void zmen_pracoviste( const Handle & h, const std::string & pracoviste);

	void smaz( const Handle & h);

private:

	typedef std::map< std::string, Zaznam> tabulka_t_;

	tabulka_t_ tabulka_;

	long long last_handle_;

	Zaznam & zaznam_( const Handle & h);

	static bool splneno_( const std::string & s, Podminka_Pracoviste p_pracoviste, const std::string & s_pracoviste);
	static bool splneno_( const std::string & s, Podminka_Prijmeni p_prijmeni, const std::string & s_prijmeni);
};

#endif

/**/
