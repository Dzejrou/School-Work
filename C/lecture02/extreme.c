#include "extreme.h"

/**
 * Brief: Returns an extreme from a given set of integers.
 * Param: Struct containing the comparator function and the starting extreme value.
 * Param: Number of the numbers being processed.
 * Param: Variable argument list of numbers.
 */
int ext(cmp_t cmp, int cnt, ...)
{
	int res = cmp.extreme;
	va_list args;
	va_start(args, cnt);

	int tmp;
	for(int i = 0; i < cnt; ++i)
	{
		tmp = va_arg(args, int);
		if(cmp.cmp_func(tmp, res))
			res = tmp;
	}

	va_end(args);

	return res;
}
