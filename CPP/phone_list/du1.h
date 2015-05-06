#ifndef du1_h_
#define du1_h_

#include <string>
#include <vector>
#include <map>
#include <set>

class Seznam
{
public:
	struct Zaznam
	{
		using telefony_t = std::vector<std::string>;
		std::string jmeno;
		std::string prijmeni;
		std::string pracoviste;
		telefony_t telefony;
	};

	struct Cmp
	{
		bool operator()(const Zaznam& a, const Zaznam& b)
		{
			return a.prijmeni < b.prijmeni;
		}
	};

	using table_t = std::multiset<Zaznam, Cmp>;
	using Handle = table_t::iterator;
	using Balik =  std::vector<Handle>;
	using work_t = std::map<std::string, Balik>;

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

	Seznam();
	Balik find( 
		Podminka_Pracoviste p_pracoviste, 
		const std::string & s_pracoviste,
		Podminka_Prijmeni p_prijmeni, 
		const std::string & s_prijmeni);

	const std::string id(const Handle& h) const;
	const Zaznam & zaznam(const Handle& h) const;
	Handle pridej(const Zaznam& z);
	void pridej_telefon(Handle& h, const std::string & telefon);
	void odeber_telefon(Handle& h, const std::string & telefon);
	void zmen_prijmeni(Handle& h, const std::string & prijmeni);
	void zmen_pracoviste(Handle& h, const std::string & pracoviste);
	void smaz(Handle& h);

private:
	bool debug_;
	table_t table_;
	work_t work_;
	void remove_work(const Handle&, const Handle&);
	void debug(std::string);
	bool match(const std::string&,Podminka_Pracoviste,const std::string&);
	bool match(const std::string&,Podminka_Prijmeni,const std::string&);
};

#endif

/**/
