// DU2multiset.hpp
// Jaroslav Jindrak NPRG051 2014/2015

#include <iostream>
#include <iomanip>
#include <tuple>
#include <set>
#include <exception>
#include <utility>
#include <string>

/**
 * Exception that is thrown when multiple results were found in the find
 * method of multi_set.
 */
class multiple_result_exception : public std::exception
{
    public: 
    virtual const char* what() const throw()
    {
        return "Multiple results corresponding to the criteria found.";
    }
};

/**
 * Exception that is thrown when no results were found in the find method
 * of multi_set.
 */
class no_result_found_exception : public std::exception
{
    public:
    virtual const char* what() const throw()
    {
        return "No results corresponding to the criteria found.";
    }
};

/**
 * Testing enums, typedefs and functions.
 */
enum color { red, blue, orange, cyan, magenta, black, white, teal,
             green, purple, pink };
using color_t = std::tuple<color, std::string, int, int, int>;
void print(const color_t&);

/**
 * Multiset class storing variables of heterogenous types using variadic
 * templates.
 * TODO list:
 *      insert     --- DONE!
 *      find       --- DONE!
 *      erase      --- DONE!
 *      efficiency --- TODO!
 *
 * Complexity: (n = # of lines in the db, m = # of parameters,
 *              p = # of columns)
 *      find:   O(n * m)
 *      insert: O(log(n) + p)
 *      erase:  O(n * m)
 */
template<typename ... TList>
class multi_set
{
    using line  = std::tuple<TList ...>;
    using dbase = std::multiset<line>;

    public:
        /**
         * Brief: Method that checks the multi_set for a line matching
         *        given arguments in specific columns (given as template
         *        arguments).
         * Param: Variadic list of arguments to be checked against.
         * Throw: multiple_result_exception when 2 or more matches were found.
         * Throw: no_result_found_exception when no match was found.
         */
        template<int ... ColList, typename ... Args>
        const line& find(Args&& ... args) const
        {
            const line* tmp_ptr{nullptr};
            for(const line& l : base_)
            {
                if(match<ColList ...>(l, std::forward<Args>(args)...))
                {
                    if(tmp_ptr == nullptr)
                        tmp_ptr = &l; // Multiple result check;
                    else
                        throw multiple_result_exception{};
                }
            }
            if(tmp_ptr)
                return *tmp_ptr;
            else
                throw no_result_found_exception{};
        }



        /**
         * Brief: Inserts a record into the multiset.
         * Param: The record as variadic list matching the multi_set types.
         */
        void insert(TList ... args)
        {
            //base_.insert(create_line(args ...)); // DEPRECATED
            base_.insert(std::make_tuple(std::forward<TList>(args)...));
        }



        /**
         * Brief: Erases all lines that match given arguments in columns
         *        specified as template arguments.
         * Param: Variadic list of the values to be matched.
         */
        template<int ... ColList, typename ... Args>
        void erase(Args&& ... args)
        {
            auto it = base_.begin();
            while(it != base_.end())
            { // Linear processing of the multiset, checking each line.
                if(match<ColList ...>(*it, std::forward<Args>(args)...))
                    it = base_.erase(it); // Erase all matches!
                else
                    ++it;
            }
        }

    private:
        /**
         * Brief: Recursively creates a tuple (one line of this database)
         *        out of a list of arguments via variadic templates.
         * Param: Head of the type list.
         * Param: Tail of the type list, given as variadic type.
         * NOTE: DEPRECATED, somehow I thought it's impossible to create
         *       a tuple from a variadic list of templates using
         *       std::make_tuple -.-
         */
        template<typename T, typename ... Args>
        std::tuple<T, Args ...> create_line(T t, Args ... args)
        {
            /*
             * std::tuple_cat concatenates the one-element tuple
             * created in this function and the tuple created in the
             * recursive call.
             */
            return std::tuple_cat(std::tuple<T>(t),
                                  create_line(args ...));
        }



        /**
         * Brief: Bottom case of the create_line recursion.
         * Param: The value of the returned tuple.
         * NOTE: DEPRECATED.
         */
        template<typename T>
        std::tuple<T> create_line(T t)
        {
            return std::make_tuple(t);
        }

        /**
         * Brief: Returns true it the given tuple matches given criteria
         *        in the specified "columns" (given as template arguments).
         * Param: Const reference to the tuple being checked.
         * Param: Head of the argument list.
         * Param: Tail of the argument list.
         */
        template<int FCol, int ... ColList, typename Arg, typename ... Args>
        bool match(const line& l, Arg&& arg, Args&& ... args) const
        {
            const typename std::tuple_element<FCol, line>::type& tmp =
                std::get<FCol>(l);
            if(tmp == arg)
                return match<ColList ...>(l, std::forward<Args>(args)...);
            else
                return false;
        }

        /**
         * Brief: Bottom case of the match recursion ("column" specified
         *        as template argument).
         * Param: Const reference to the tuple being checked.
         * Param: Value that it's being checked against.
         */
        template<int N, typename T>
        bool match(const line& l, T&& arg) const
        {
            const typename std::tuple_element<N, line>::type& tmp =
                std::get<N>(l);
            return tmp == arg;        
        }

        /* Class members: */
        dbase base_;
};

/**
 * Brief: Used for testing to print the results.
 * Param: A tuple representing one line in the multi_set.
 */
void print(const color_t& t)
{
    std::cout << "Code:"
              << std::setw(2) << std::get<0>(t)
              << " Color:"
              << std::setw(8) << std::get<1>(t)
              << std::setw(5) << " RGB: (" 
              << std::setw(3) << std::get<2>(t) << ", "
              << std::setw(3) << std::get<3>(t) << ", "
              << std::setw(3) << std::get<4>(t) << ")." << std::endl;
}
