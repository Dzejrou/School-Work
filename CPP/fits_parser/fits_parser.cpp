#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <cmath>

void parse(const std::string&);
bool is_end(const std::string&);
bool is_blank(const std::string&);
std::string get_name(const std::string&);
std::string data_type(const std::string&);
std::string value(const std::string&, int);
int closest_end(int);
bool is_num(const std::string&);

int main(int argc, char** argv)
{
    std::string filename;
    if(argc >= 2)
    {
        filename = argv[1];
    }
    else
    {
        std::cout << "No filename passed as argument" << std::endl
            << "Aborting." << std::endl;
        return 1;
    }

    parse(filename);
    return 0;
}

void parse(const std::string& fname)
{
    std::string name;
    std::string type;
    std::string val;

    std::size_t header_size = 0;
    std::string state;
    int number_size = 0;
    int data_size = 0;
    char tmp[81];
    std::string kword;
    std::fstream input{fname, std::ios::in | std::ios::binary};

    while(!input.eof())
    {
        input.get(tmp,81); // Get the keyword.
        kword = std::string{tmp};
        header_size += 80;

        if(is_end(kword))
        {
            // Ignore to the end of the current block.
            std::cout << "Data: " << data_size << "B" << std::endl;
            int tmp3 = closest_end(header_size);
            input.ignore(closest_end(header_size));
            input.ignore(data_size);
            if(data_size != 0)
            {
                int tmp2 = header_size + data_size + tmp3;
                input.ignore(closest_end(tmp2));
            }
            std::cin.ignore();
            data_size = 0;
            header_size = 0;
            continue;
        }
        else if(is_blank(kword))
            continue; // Skip it.

        std::string t = kword.substr(0,7);
        if(t == "SIMPLE  ")
        {
            state = "Primary";
            std::cout << "Primary" << std::endl;
        }
        else if(t == "XTENSION")
        {
            if(kword.substr(10,14) == "IMAGE")
            {
                state = "Image";
                std::cout << "Image" << std::endl;
            }
            else if(kword.substr(10,17) == "BINTABLE")
            {
                state = "BinTable";
                std::cout << "BinTable" << std::endl;
            }
        }

        name = get_name(kword);
        type = data_type(kword);
        if(name == "BITPIX")
        {
            val = value(kword,1);
            int tmp_val;
            if(val == "32" || val == "-32")
            {
                number_size = 4;
            }
            else if(val == "64" || val == "-64")
            {
                number_size = 8;
            }            
        }
        else if(name == "NAXIS")
        {
            int total = 1;
            int num = std::stoi(value(kword,1));
            int pos = input.tellg();
            char tmp[81];
            for(int j = 0; j < num; ++j)
            {
                input.get(tmp,81);
                total *= std::stoi(value(tmp,1));
            }
            total *= number_size;
            number_size = 1;
            data_size += total;
            input.get(tmp,81);
            if(state == "BinTable")
            {
                total += std::stoi(value(tmp,1)); // PCOUNT!
            }
            input.seekg(pos);
        }

        if(type == "int64")
            val = value(kword, 1);
        else if(type == "double")
            val = value(kword, 2);
        else
            val = value(kword, 3);

        std::cout << name << "[" << type
            << "]=" << val << std::endl;
    }
    input.close();
}

bool is_end(const std::string& kword)
{
    return kword.find("END") == 0 ||
        kword.find("end") == 0;
}

bool is_blank(const std::string& kword)
{
    if(kword.find("COMMENT") != std::string::npos ||
            kword.find("HISTORY") != std::string::npos)
        return true;
    for(std::size_t i = 0; i < kword.length(); ++i)
        if(kword[i] != ' ')
            return false;
    return true;
}

std::string get_name(const std::string& kword)
{
    int i = 0;
    std::string tmp = "";
    while(kword[i] != '=' && i < 8)
        if(kword[i] != ' ')
            tmp.append(std::string{kword[i++]});
        else
            i++;
    return tmp;
}

std::string data_type(const std::string& kword)
{
    // Value starts at char 10.
    int i = 10;
    while(kword[i] == ' ' || kword[i] == '=')
        ++i; // In case of trailing spaces.

    if(kword[i] == '\'')
        return "string";
    else if(kword[i] == '-' || (kword[i] <= '9' && kword[i] >= 0))
    {
        if(kword.find("E",i) != std::string::npos ||
                (kword.find(".",i) != std::string::npos &&
                 kword.find(":",i) == std::string::npos))
            return "double";
        else
            return "int64";
    }
    else if(kword[i] == 'T' || kword[i] == 'F')
        return "logical";
    else
        return "UNKNOWN - " + kword;
}

std::string value(const std::string& kword, int type)
    /*
     * Types:
     * 1 = integer
     * 2 = double
     * 3 = rest
     */
{
    std::string tmpstr{};
    int i = 10;
    while(kword[i] == ' ' || kword[i] == '=')
        ++i;

    bool instr = false;
    for(; i < kword.length(); ++i)
    {
        if(kword[i] == '\'')
            instr = !instr;
        if(kword[i] != '"' && kword[i] != ' ' && kword[i] != '/')
            tmpstr.append(std::string{kword[i]});
        if((kword[i] == ' ' || kword[i] == '/') && instr)
            tmpstr.append(std::string{kword[i]});
        else if(kword[i] == '/')
            break;
    }

    if(tmpstr == "")
        return tmpstr;
    switch(type)
    {
        case 1:
            return tmpstr;
        case 2:
            return std::to_string(std::stod(tmpstr)); // To get rid of E.
        case 3:
            if(tmpstr == "T")
                return "TRUE";
            else if(tmpstr == "F")
                return "FALSE";
            return tmpstr;
        default:
            return "BAD TYPE!";
    }
}

int closest_end(int val)
{
    int tmp = 2880;
    while(tmp <= val)
        tmp += 2880;
    return tmp - val;
}

bool is_num(const std::string& num)
{
    for(int i = 0; i < num.length(); ++i)
    {
        if(num[i] == '/')
            return false;
        if((num[i] <= 9 && num[i] >= 0) || // Numeric.
                num[i] == '.' || num[i] == 'E' || num[i] == 'e') 
        {
            // NOTHING!
        }
        else
            return false;
    }
    return true;
}
