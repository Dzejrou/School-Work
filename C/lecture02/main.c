#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <dlfcn.h>
#include <limits.h>
#include "extreme.h"

int min_cmp(int, int);
int max_cmp(int, int);
void safe_exit(int);

void* lib_handle = NULL;

/**
 * Brief: Prints the extreme of a set of (hardcoded atm^^) numbers, using
 *        a dynamic library.
 * Param: -o specifies operation deciding the extreme.
 * Param: -e specifies environment variable holding the extreme.
 * Note: Possible values for extreme are min and max.
 */
int main(int argc, char** argv)
{
    lib_handle = dlopen("./libext.so", RTLD_NOW);
    if(!lib_handle)
    {
        printf("Cannot locate libmin.so in the current directory.\n");
        safe_exit(EXIT_FAILURE);
    }
    ext_func ext = (ext_func)dlsym(lib_handle, "ext");

    char c;
    int op;
    while((c = getopt(argc, argv, "o:e:h")) != -1)
    {
        switch(c)
        {
            case 'o':
                op = (strncmp("max", optarg, 3) == 0) ? MAX : MIN;
                break;
            case 'e':
            {
                char* tmp = getenv(optarg);
                if(tmp == NULL)
                {
                    printf("Invalid environment variable: %s\n", optarg);
                    safe_exit(EXIT_FAILURE);
                }
                op = (strncmp("max", tmp, 3) == 0) ? MAX : MIN;
                break;
            }
            case 'h':
                printf("Set operation type by -o {min|max} or -e ENV_VAR.\n");
                printf("Default operation: min\n");
                break;
        }
    }

    cmp_t cmp;

    if(op == MAX)
    {
        cmp.extreme = INT_MIN;;
        cmp.cmp_func = &max_cmp;
    }
    else
    {
        cmp.extreme = INT_MAX;;
        cmp.cmp_func = &min_cmp;
    }

    printf("%d\n", ext(cmp, 4, 1, 2, 3, 4));
    safe_exit(EXIT_SUCCESS);
}

/**
 * Brief: Comparator used to get minimum from two numbers.
 * Param: First number.
 * Param: Second number.
 */
int min_cmp(int fnum, int snum)
{
    return fnum < snum;
}

/**
 * Brief: Comparator used to get maximum from two numbers.
 * Param: First number.
 * Param: Second number.
 */
int max_cmp(int fnum, int snum)
{
    return fnum > snum;
}

/**
 * Brief: Exits the program and ensures all resources are relased.
 * Param: Return code.
 */
void safe_exit(int ret)
{
    if(lib_handle)
        dlclose(lib_handle);
    exit(ret);
}
