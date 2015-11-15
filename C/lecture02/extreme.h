#ifndef min_h
#define min_h

#include <stdarg.h>

/**
 * Type representing a pointer to a function comparing two numbers.
 */
typedef int (*comparator)(int, int);

/**
 * Struct representing an extreme selecting operation, holds
 * a starting value of the extreme and a pointer to the comparing
 * function.
 */
typedef struct cmp_t_
{
    int extreme;
    comparator cmp_func;
} cmp_t;

/**
 * Type representing a pointer to the extreme finding function,
 * used for simplicity when loading this 
 */
typedef int (*ext_func)(cmp_t, int, ...);

/**
 * Enum representing possible extremes.
 */
enum op
{
    MIN = 0, MAX
};

/**
 * Brief: Returns an extreme from a given set of integers.
 * Param: Struct containing the comparator function and the starting extreme value.
 * Param: Number of the numbers being processed.
 * Param: Variable argument list of numbers.
 */
//int ext(cmp_t, int, ...);

#endif
