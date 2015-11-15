#include "extreme.h"

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
