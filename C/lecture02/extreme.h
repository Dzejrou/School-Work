#ifndef min_h
#define min_h

#include <stdarg.h>

typedef int (*comparator)(int, int);

typedef struct CMP_
{
	int extreme;
	comparator cmp_func;
} cmp_t;

typedef int (*ext_func)(cmp_t, int, ...);

enum OP
{
	MIN = 0, MAX
};

/**
 * Loaded using dload().
 * int ext(cmp_t, int, ...);
*/

#endif
