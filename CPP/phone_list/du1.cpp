#include "du1.h"

#include <algorithm>
#include <iostream>

Seznam::Seznam()
	:table_{}, work_{}, debug_{true}
{
}


Seznam::Balik Seznam::find( 
	Podminka_Pracoviste p_pracoviste, 
	const std::string & s_pracoviste,
	Podminka_Prijmeni p_prijmeni, 
	const std::string & s_prijmeni)
{
	Balik b;
	/* For clang, switch is faster than an elsif chain. (jmp table implementation)
	 * TODO: Possible that MSVC will be slower, check it...
	 * */
	switch(p_pracoviste)
	{
		case Jakekoliv_Pracoviste:
			switch(p_prijmeni)
			{
				case Jakekoliv_Prijmeni:
					for(Handle h = table_.begin(); h != table_.end(); ++h)
						b.push_back(h);
					break;
				case Prefix_Prijmeni:
					// Narrow it down.
					Zaznam tmp{};
					tmp.prijmeni = s_prijmeni;
					Handle lower = table_.lower_bound(tmp);
					while (lower->prijmeni.find(s_prijmeni) == 0 && lower->prijmeni.size() >= s_prijmeni.size())
						b.push_back(lower++);
					break;
			}
			break;
		case Substring_Pracoviste:
			switch(p_prijmeni)
			{
				case Jakekoliv_Prijmeni:
					for (auto it = work_.begin(); it != work_.end(); ++it)
						if (it->first.find(s_pracoviste) != std::string::npos)
							b.insert(b.end(),it->second.begin(),it->second.end());
					break;
				case Prefix_Prijmeni:
					Zaznam tmp{};
					tmp.prijmeni = s_prijmeni;
					Handle lower = table_.lower_bound(tmp);
					while (lower->prijmeni.find(s_prijmeni) == 0 && lower->prijmeni.size() >= s_prijmeni.size())
					{
						if (lower->pracoviste.find(s_pracoviste) != std::string::npos)
							b.push_back(lower);
						++lower;
					}
					break;
			}
			break;
		case Shoda_Pracoviste:
			switch(p_prijmeni)
			{
				case Jakekoliv_Prijmeni:
				{
					auto it = work_.find(s_pracoviste);
					b.insert(b.end(), it->second.begin(), it->second.end());
				}
					break;
				case Prefix_Prijmeni:
				{
					auto itt = work_.find(s_pracoviste);
					for (auto i = itt->second.begin(); i != itt->second.end(); i++)
						if ((*i)->prijmeni.find(s_prijmeni) == 0 && (*i)->prijmeni.size() >= s_prijmeni.size())
							b.push_back(*i);
				}
					break;
			}
			break;
	}
	return b;
}

const std::string Seznam::id( const Handle & h) const
{// TODO: Is this the right way to make id?
	return h->prijmeni;
}

const Seznam::Zaznam & Seznam::zaznam( const Handle & h) const
{
	return *h; 
}

Seznam::Handle Seznam::pridej( const Zaznam & z)
{
	// Insert record and keep the iterator.
	auto it = table_.insert(z);

	// Keep the handle in workstations.
	work_[z.pracoviste].push_back(it);

	return it;
}

void Seznam::pridej_telefon( Handle & h, const std::string & telefon)
{
	Zaznam tmp = *h;
	tmp.telefony.push_back(telefon);
	smaz(h);
	pridej(tmp);
}

void Seznam::odeber_telefon( Handle & h, const std::string & telefon)
{
	Zaznam tmp = *h;

	// Find the iterator that corresponds to the number.
	auto it = std::find(begin(tmp.telefony),end(tmp.telefony),telefon);

	if(it != end(tmp.telefony)) // Was it in the list of numbers?
		tmp.telefony.erase(it);

	smaz(h);
	pridej(tmp);
}

void Seznam::zmen_prijmeni( Handle & h, const std::string & prijmeni)
{
	Zaznam tmp_rec = *h;
	tmp_rec.prijmeni = prijmeni;
	auto new_handle = table_.insert(tmp_rec); // Insert the new copy.

	// Find the old handle in the workstations multimap:
	remove_work(h, new_handle);
	table_.erase(h); // Get rid of the old record.
}

void Seznam::zmen_pracoviste( Handle & h, const std::string & pracoviste)
{
	Zaznam tmp = *h;
	tmp.pracoviste = pracoviste;
	smaz(h);
	pridej(tmp);
}

void Seznam::smaz( Handle & h)
{
	// Remove work_ entry first:
	remove_work(h,h);

	// Remove the table entry itself.
	table_.erase(h);
}

void Seznam::remove_work(const Handle& h, const Handle& new_handle)
{ // O(logn)
	auto target = work_.find(h->pracoviste);
	auto it = target->second.begin();
	auto end = target->second.end();
	for (; it != end; ++it)
	{
		if (*it == h) // Found it!
		{
			if (h == new_handle) // Just remove.
				target->second.erase(it);
			else
				*it = new_handle; // Change the workstation.
		}
	}
}

void Seznam::debug(std::string msg)
{
	if(debug_)
		std::cout << msg << std::endl;
}

bool Seznam::match(const std::string& str, Podminka_Pracoviste p_cond, const std::string& p_str)
{ // Obsolete.
	switch(p_cond)
	{
		case Jakekoliv_Pracoviste:
			return true;
		case Shoda_Pracoviste:
			return str == p_str;
		case Substring_Pracoviste:
			return str.find(p_str) != std::string::npos;
	}
	return false;
}

bool Seznam::match(const std::string& str, Podminka_Prijmeni p_cond, const std::string& p_str)
{ // Obsolete.
	switch(p_cond)
	{
		case Jakekoliv_Prijmeni:
			return true;
		case Prefix_Prijmeni:
			return str.size() >= p_str.size() && str.find(p_str) == 0;
				//&& std::equal(p_str.begin(),p_str.end(),str.begin());
	}
}
