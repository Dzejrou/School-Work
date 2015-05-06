#ifndef du1debug_hpp_
#define du1debug_hpp_

#include <exception>
#include <iostream>
#include <ostream>

// if DU_NDEBUG macro is defined, switch off debugging support

#ifndef DU_NDEBUG

#define du_assert(e) ((void)((!!(e))||(du_abort((#e),__FILE__,__LINE__),0)))

#else

#define du_assert(ignore) ((void)0)

#endif

class du_abort_exception : public std::exception
{
private:
    virtual const char * what() const throw()
    {
	return "du_abort_exception";
    }
};

inline void du_abort( const char * s, const char * f, int l)
{
    std::cout << f << "(" << l << "): " << s << std::endl;
    throw du_abort_exception(); 
}

#endif
