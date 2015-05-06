#include "du1matrix.hpp"
#include "du1debug.hpp"

#include <iostream>
#include <algorithm>
#include <string>

typedef matrix< char> my_matrix;

int cnt = 69;

void f1( my_matrix::cols_t::value_type::reference x)
{
    x = ++cnt;
}

void f2( my_matrix::cols_t::reference r)
{
    std::for_each( r.begin(), r.end(), f1);
}

void f3( my_matrix::rows_t::value_type::reference x)
{
    std::cout << x << " ";
}

void f4( my_matrix::rows_t::reference r)
{
    std::for_each( r.begin(), r.end(), f3);
    std::cout << std::endl;
}

void f6( my_matrix::cols_t::value_type::reference x)
{
    std::cout << x << " ";
}

void f5( my_matrix::cols_t::reference r)
{
    std::for_each(r.begin(), r.end(), f6);
    std::cout << std::endl;
}


int main( int, char * *)
{

    try {

        // WORKS!
	my_matrix a( 3, 4, 'l');  // matice 3 radky * 4 sloupce inicializovana nulami

	my_matrix c = a; // WORKS!

	my_matrix b( a); // WORKS!

    // TODO: Initializer list constructor - was it in the slides?
    // my_matrix d = { { 1, 2, 3, 4 },
    //                 { 5, 6, 7, 8 },
    //                 { 9, 10, 11, 12 } };

	std::for_each( b.cols().begin(), b.cols().end(), f2); // WORKS!

	c = b;
	c[0][2] = b[1][1]; // Subscript access: DONE!

    std::cout << "Printing by rows: " << std::endl; // WORKS!
	std::for_each( c.rows().begin(), c.rows().end(), f4);

    std::cout << std::endl << "Printing by columns: " << std::endl; // WORKS!
    std::for_each( c.cols().begin(), c.cols().end(), f5);

    std::cout << std::endl << "Printing by indexes: " << std::endl;
    for(my_matrix::rows_t::size_type i = 0; i < c.size_row(); ++i)
    {
        for(my_matrix::cols_t::size_type j = 0; j < c.size_col(); ++j)
        {
            std::cout << c[i][j] << " ";
        }
        std::cout << std::endl;
    }

    std::cout << std::endl;
    std::cout << "Printing with mat_it: " << std::endl;
    my_matrix::iterator it = c.begin();
    for(my_matrix::size_type i = 1; it != c.end(); ++it, ++i)
    {
        std::cout << *it << " ";
        if(i % c.size_col() == 0)
            std::cout << std::endl;
    }

    // WORKS!
	my_matrix::cols_t::iterator rowit;

	rowit->end(); // TODO: Debug solution bonus!

    std::cout << std::endl;
    std::cout << "108 == " << c.cols()[2][1] << std::endl;
    std::cout << "106 == " << c.rows()[2][1] << std::endl;

    }
    catch ( const std::exception & e)
    {
	std::cout << e.what() << std::endl;
    }

    return 0;
}
