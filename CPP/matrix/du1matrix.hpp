// DU1matrix.hpp
// Jaroslav Jindrak NPRG051 2014/2015
#ifndef du1matrix_hpp_
#define du1matrix_hpp_

#include "du1debug.hpp"
#include <memory>
#include <iostream>

/**************************************************************************************************/

/**
 * STATUS:
 *  - cols, rows, single_col, single_row, mat_it iterators ... Complete.
 *  - [] access (matrix, cols, rows)                       ... Complete.
 *  - Const version of iterators                           ... TODO
 *  - Safe debug mode                                      ... TODO
 */

template<typename T>
class matrix; // Forward declaration.

/**
 * Iterator that iterates over elements in one column.
 */
template<typename T>
class single_col
{
    public:
        /* Types: */
        typedef T                          value_type;
        typedef T*                         pointer;
        typedef const T*                   const_pointer;
        typedef T&                         reference;
        typedef const T&                   const_reference;
        typedef std::size_t                size_type;
        typedef std::ptrdiff_t             difference_type;
        typedef std::forward_iterator_tag  iterator_category;

        /**
         * Brief: Default constructor.
         */
        single_col()
            : matrix_{nullptr},
              size_{0},
              curr_{0}
        { /* Dummy body. */ }

        /**
         * Brief: Constructor to be used as the default one.
         * Param: Pointer to the matrix matrix.
         * Param: Index of the column this object is supposed to iterate over.
         * Param: Starting index of the iteration (defaults to zero).
         * Note: size_row_ in the matrix refers to the number of rows and
         *       therefore is the size of a column.
         */
        single_col(matrix<T>* mat, size_type col_index = 0,
                size_type start = 0)
            : matrix_{new T*[mat->size_row()]},
              size_{mat->size_row()},
              curr_{start}
        {
            // Initalize the elements:
            for(size_type i = 0; i < size_; ++i)
                matrix_[i] = &(mat->matrix_)[i * mat->size_col() + col_index];
            // When gettin column members, i need number of columns as offset!
        }

        /**
         * Brief: Copy constructor used for returning during incrementation.
         * Param: Const reference to the other object.
         * Note: Have to copy the pointers here, because if the other
         *       iterator dies and the pointer array is shared, this
         *       one is invalidated.
         */
        single_col(const single_col<T>& other)
            : matrix_{new T*[other.size_]},
              size_{other.size_},
              curr_{other.curr_}
        {
            for(size_type i = 0; i < size_; ++i)
                matrix_[i] = other.matrix_[i]; 
        }

        /**
         * Brief: Assignment operator overload.
         * Param: Const reference to the other instance of this class.
         */
        single_col<T>& operator=(const single_col<T>& other)
        {
            if(this == &other) // Check for self assignment!
                return *this;
            size_ = other.size_;
            delete[] matrix_; // Do not delete the element pointers!
            matrix_ = new T*[size_];

            for(size_type i = 0; i < size_; ++i)
                matrix_[i] = other.matrix_[i]; // Copy the elements.

            return *this;
        }

        /**
         * Brief: Returns reference to the pointed-to member of the column
         *        by dereferencing.
         */
        reference operator*()
        {
            return *matrix_[curr_];
        }

        /**
         * Brief: Arrow operator overload.
         * Note: Returns pointer to self for use with methods like
         *       rowit->end() in the test file.
         */
        single_col<T>* operator->()
        {
            return this;
        }

        /**
         * Brief: Increments the iterator by increasing the current index
         *        in the column and returns a reference to this object.
         *        (Prefix increment.)
         */
        single_col<T>& operator++()
        {
            ++curr_;
            return *this;
        }

        /**
         * Brief: Increments the iterator by increasing the current index
         *        in the column and returns it's pre-increment version
         *        by value by value.
         *        (Suffix increment.)
         * Param: A dummy variable indicating to the compiler that this is
         *        a suffix version of the increment.
         */
        single_col<T> operator++(int never_gonna_be_used)
        {
            single_col<T> tmp{*this}; // Suffix increment returns preinc copy.
            ++curr_;
            return tmp;
        }

        /**
         * Brief: Equality operator overload.
         * Param: Const reference to the other instance of this class.
         * Note: Since both iterators have to point to the same column,
         *       checking only the pointer to the first member of the
         *       column is satisying, since it means they both point
         *       to the same column.
         */
        bool operator==(const single_col<T>& other)
        {
            if(other.matrix_ == nullptr)
                return false;
            else
                return curr_ == other.curr_ && size_ == other.size_ 
                    && matrix_[0] == other.matrix_[0];
        }

        /**
         * Brief: Inequality operator overload.
         * Param: Const reference to the other instance of this class.
         */
        bool operator!=(const single_col<T>& other)
        {
            return !(*this == other);
        }

        /**
         * Brief: Returns by value a version of this iterator poiinting
         *        to the first column.
         */
        single_col<T> begin()
        {
            // TODO: Create constructor for T** instead of matrix*,
            //       so the curr_ backup & change is not necessary.
            size_type tmp_curr = curr_;
            curr_ = 0;
            single_col<T> tmp{*this};
            curr_ = tmp_curr;
            return tmp;
        }

        /**
         * Brief: Returns by value a version of this iterator pointing
         *        to the one past-last column.
         */
        single_col<T> end()
        {
            size_type tmp_curr = curr_;
            curr_ = size_;
            single_col<T> tmp{*this};
            curr_ = tmp_curr;
            return tmp;
        }

        /**
         * Brief: Indexed access operator overload, used for expressions
         *        like a.cols()[1][2].
         * Param: The index of the element in this column.
         */
        reference operator[](size_type index)
        {
            return *matrix_[index];
        }

        /**
         * Brief: Returns the size of the column this object iterates over.
         */
        size_type size() { return size_; }
    private:
        T** matrix_; // Pointers to the members of the column.
        size_type size_, curr_; // Size of the column and current index.
 };

/**
 * Iterator that iterates over elements in one single row.
 */
template<typename T>
class single_row
  {
    public:
        /* Types: */
        typedef T                          value_type;
        typedef T*                         pointer;
        typedef const T*                   const_pointer;
        typedef T&                         reference;
        typedef const T&                   const_reference;
        typedef std::size_t                size_type;
        typedef std::ptrdiff_t             difference_type;
        typedef std::forward_iterator_tag  iterator_category;

        /**
         * Brief: Default constructor.
         */
        single_row()
            : size_{0},
              matrix_{nullptr},
              curr_{0}
        { /* Dummy body. */ }

        /**
         * Brief: Constructor to be used as the default one.
         * Param: Pointer to the matrix matrix.
         * Param: Index of the row this object is supposed to iterate over.
         * Param: Starting index of the iteration (defaults to zero).
         * Note: size_row_ in the matrix refers to the number of columns
         *       and therefore is the size of a row.
         */
        single_row(matrix<T>* mat, size_type row_index = 0,
                size_type start = 0)
            : matrix_{new T*[mat->size_col()]},
              size_{mat->size_col()},
              curr_{start}
        {
            // Initalize the elements:
            for(size_type i = 0; i < size_; ++i)
                matrix_[i] = &mat->matrix_[i + row_index * size_];
        }

        /**
         * Brief: Copy constructor used for returning during incrementation.
         * Param: Const reference to the other object.
         * Note: Have to copy the pointers here, because if the other
         *       iterator dies and the pointer array is shared, this
         *       one is invalidated.
         */
        single_row(const single_row<T>& other)
            : matrix_{new T*[other.size_]},
              size_{other.size_},
              curr_{other.curr_}
        {
            for(size_type i = 0; i < size_; ++i)
                matrix_[i] = other.matrix_[i]; 
        }

        /**
         * Brief: Assignment operator overload.
         * Param: Const reference to the other instance of this class.
         */
        single_row<T>& operator=(const single_row<T>& other)
        {
            if(this == &other) // Check for self assignment!
                return *this;
            size_ = other.size_;
            delete[] matrix_; // Do not delete the element pointers!
            matrix_ = new T*[size_];

            for(size_type i = 0; i < size_; ++i)
                matrix_[i] = other.matrix_[i]; // Copy the elements.

            return *this;
        }

        /**
         * Brief: Returns reference to the pointed-to member of the row
         *        by dereferencing.
         */
        reference operator*()
        {
            return *matrix_[curr_];
        }

        /**
         * Brief: Arrow operator overload.
         * Note: Returns pointer to self for use with methods like
         *       rowit->end() in the test file.
         */
        single_row<T>* operator->()
        {
            return this;
        }

        /**
         * Brief: Increments the iterator by increasing the current index
         *        in the row and returns a copy of this object.
         *        (Prefix increment.)
         */
        single_row<T>& operator++()
        {
            ++curr_;
            return *this;
        }

        /**
         * Brief: Increments the iterator by increasing the current index
         *        in the row and returns it's pre-increment version
         *        by value by value.
         *        (Suffix increment.)
         * Param: A dummy variable indicating to the compiler that this is
         *        a suffix version of the increment.
         */
        single_row operator++(int never_gonna_be_used)
        {
            single_row<T> tmp{*this}; // Suffix increment returns pre-inc copy.
            ++curr_;
            return tmp;
        }

        /**
         * Brief: Equality operator overload.
         * Param: Const reference to the other instance of this class.
         * Note: Since both iterators have to point to the same row,
         *       checking only the pointer to the first member of the
         *       row is satisying, since it means they both point
         *       to the same row.
         */
        bool operator==(const single_row<T>& other)
        {
            if(other.matrix_ == nullptr)
                return false;
            else
                return curr_ == other.curr_ && size_ == other.size_
                    && matrix_[0] == other.matrix_[0];
        }

        /**
         * Brief: Inequality operator overload.
         * Param: Const reference to the other instance of this class.
         * Note: Due to simplicity of this operation there is no need to return
         *       !(*this == other).
         */
        bool operator!=(const single_row<T>& other)
        {
            return !(*this == other);
        }

        /**
         * Brief: Returns by value a version of this iterator pointing to 
         *        the first row.
         */
        single_row<T> begin()
        {
            // TODO: Create constructor for T** instead of matrix*,
            //       so the curr_ backup & change is not necessary.
            size_type tmp_curr = curr_;
            curr_ = 0;
            single_row<T> tmp{*this};
            curr_ = tmp_curr;
            return tmp;
        }

        /**
         * Brief: Returns by value a version of this iterator pointing to
         *        the past-last row.
         */
        single_row<T> end()
        {
            size_type tmp_curr = curr_;
            curr_ = size_;
            single_row<T> tmp{*this};
            curr_ = tmp_curr;
            return tmp;
        }

        /**
         * Brief: Index access operator overload used for 2 dimensional
         *        index access.
         */
        reference operator[](size_type index)
        {
            return *matrix_[index];
        }

        /**
         * Brief: Returns the size of the row this object iterates over.
         */
        size_type size() { return size_; }
    private:
        T** matrix_; // Pointers to the members of the row.
        size_type size_, curr_; // Size of the row and current index.
};

/**
 * Iterator that iterates over columns in the matrix.
 * Individual columns are then represented by the single_col<T>
 * quasi structure.
 */
template<typename T>
class cols
{
    public:
        typedef single_col<T>                            value_type;
        typedef single_col<T>*                           pointer;
        typedef const single_col<T>*                     const_pointer;
        typedef single_col<T> /* See note below. */      reference;
        typedef const single_col<T>&                     const_reference;
        typedef typename single_col<T>::size_type        size_type;
        typedef typename single_col<T>::difference_type  difference_type;
        typedef std::forward_iterator_tag                iterator_category;

        typedef single_col<T>                            iterator;

        /**
         * Note: Setting reference to single_col<T> because of dereferencing.
         */

        /**
         * Brief: Constructor to be used as the default one.
         * Param: Pointer to the matrix this class will be iterating over.
         * Param: Index of the current column.
         */
        cols(matrix<T>* mat, size_type curr = 0)
            : matrix_{mat},
              size_col_{mat->size_col()},
              curr_col_{curr}
        { /* Dummy body. */ }

        /**
         * Brief: Copy construcor.
         * Param: Const reference to the other instance of this class.
         */
        cols(const cols<T>& other)
            : matrix_{other.matrix_},
              size_col_{other.size_col_},
              curr_col_{other.curr_col_}
        { /* Dummy body. */ }

        /**
         * Brief: Copy assignment.
         * Param: Const reference to the other instance of this class.
         */
        cols<T>& operator=(const cols<T>& other)
        {
            if(this == &other) // Check for self-assignment.
                return *this;

            matrix_ = other.matrix_;
            size_col_ = other.size_col_;
            curr_col_ = other.curr_col_;
        }

        /**
         * Brief: Dereference operator overload, returns proxy on the current
         *        column.
         */
        value_type operator*()
        {
            return single_col<T>{matrix_, curr_col_, 0};
        }

        /**
         * Brief: Arrow operator overload, returns proxu on the current column.
         */
        value_type operator->()
        {
            return single_col<T>{matrix_, curr_col_, 0};
        }

        /**
         * Brief: Prefix increment overload, returns reference to this object
         *        in post-increment state.
         */
        cols<T>& operator++()
        {
            ++curr_col_;
            return *this;
        }

        /**
         * Brief: Suffix increment overload, returns a copy of this object
         *        in pre-increment state by value.
         */
        cols<T> operator++(int never_gonna_be_used)
        {
            cols<T> tmp{*this};
            ++curr_col_;
            return tmp;
        }

        /**
         * Brief: Equality operator overload.
         * Param: Const reference to the other instance of this class.
         */
        bool operator==(const cols<T>& other)
        {
            if(other.curr_col_ == other.size_col_ || other.matrix_ == nullptr)
                // One past-last element.
                return matrix_ == other.matrix_ && curr_col_ == other.curr_col_
                    && size_col_ == other.size_col_;
            else
                return matrix_ == other.matrix_ && curr_col_ == other.curr_col_;
        }

        /**
         * Brief: Inequality operator overload.
         * Param: Const reference to the other instance of this class.
         */
        bool operator!=(const cols<T>& other)
        {
            return !(*this == other);
        }

        /**
         * Brief: Operator for index access, returns proxu on the column.
         * Param: Column index.
         */
        reference operator[](size_type i)
        {
            return single_col<T>{matrix_, i, 0};
        }

        /**
         * Brief: Method that returns by value a version of this iterator with
         *        its current column set to 0.
         */
        cols<T> begin()
        {
            return cols<T>{matrix_, 0};
        }

        /**
         * Brief: Method that returns by value a version of this iterator with
         *        its current column set to the one past-last column.
         */
        cols<T> end()
        {
            return cols<T>{matrix_, size_col_};
        }

        /**
         * Brief: Returns the number of columns in the matrix.
         */
        size_type size() { return size_col_; }
    private:
        matrix<T>* matrix_;
        size_type size_col_, curr_col_;
};

/**
 * Iterates over rows in the matrix, individial rows are then represented
 * by the single_row<T> quasi structure.
 */
template<typename T>
class rows
{
    public:
        typedef single_row<T>                            value_type;
        typedef single_row<T>*                           pointer;
        typedef const single_row<T>*                     const_pointer;
        typedef single_row<T> /* See note below. */      reference;
        typedef const single_row<T>&                     const_reference;
        typedef typename single_row<T>::size_type        size_type;
        typedef typename single_row<T>::difference_type  difference_type;
        typedef std::forward_iterator_tag                iterator_category;

        typedef single_row<T>                            iterator;

        /**
         * Note: Setting reference to single_row<T> because of dereferencing.
         */

        /**
         * Brief: Constructor to be used as the default one.
         * Param: Pointer to the matrix this class will be iterating over.
         * Param: Index of the current row.
         */
        rows(matrix<T>* mat, size_type curr = 0)
            : matrix_{mat},
              size_row_{mat->size_row()},
              curr_row_{curr}
        { /* Dummy body. */ }

        /**
         * Brief: Copy construcor.
         * Param: Const reference to the other instance of this class.
         */
        rows(const rows<T>& other)
            : matrix_{other.matrix_},
              size_row_{other.size_row_},
              curr_row_{other.curr_row_}
        { /* Dummy body. */ }

        /**
         * Brief: Copy assignment.
         * Param: Const reference to the other instance of this class.
         */
        rows<T>& operator=(const rows<T>& other)
        {
            if(this == &other) // Check for self-assignment.
                return *this;

            matrix_ = other.matrix_;
            size_row_ = other.size_row_;
            curr_row_ = other.curr_row_;
        }

        /**
         * Brief: Dereference operator overload, returns proxy on the current
         *        row.
         */
        value_type operator*()
        {
            return single_row<T>{matrix_, curr_row_, 0};
        }

        /**
         * Brief: Arrow operator overload, returns proxu on the current row.
         */
        value_type operator->()
        {
            return single_row<T>{matrix_, curr_row_, 0};
        }

        /**
         * Brief: Prefix increment overload, returns reference to this object
         *        in post-increment state.
         */
        rows<T>& operator++()
        {
            ++curr_row_;
            return *this;
        }

        /**
         * Brief: Suffix increment overload, returns a copy of this object
         *        in pre-increment state by value.
         */
        rows<T> operator++(int never_gonna_be_used)
        {
            rows<T> tmp{*this};
            ++curr_row_;
            return tmp;
        }

        /**
         * Brief: Equality operator overload.
         * Param: Const reference to the other instance of this class.
         */
        bool operator==(const rows<T>& other)
        {
            if(other.curr_row_ == other.size_row_ || other.matrix_ == nullptr)
                // One past-last element.
                return matrix_ == other.matrix_ && curr_row_ == other.curr_row_
                    && size_row_ == other.size_row_;
            else
                return matrix_ == other.matrix_ && curr_row_ == other.curr_row_;
        }

        /**
         * Brief: Inequality operator overload.
         * Param: Const reference to the other instance of this class.
         */
        bool operator!=(const rows<T>& other)
        {
            return !(*this == other);
        }

        /**
         * Brief: Operator for index access, returns proxy on a single row.
         * Param: Index of the row.
         */
        value_type operator[](size_type i)
        { 
            return single_row<T>{matrix_, i, 0};
        }

        /**
         * Brief: Method that returns by value a version of this iterator with
         *        its current row set to 0.
         */
        rows<T> begin()
        {
            return rows<T>{matrix_, 0};
        }

        /**
         * Brief: Method that returns by value a version of this iterator with
         *        its current row set to the one past-last row.
         */
        rows<T> end()
        {
            return rows<T>{matrix_, size_row_};
        }

        /**
         * Brief: Returns the number of rows in the matrix.
         */
        size_type size() { return size_row_; }
    private:
        matrix<T>* matrix_;
        size_type size_row_, curr_row_;
};

/**
 * Iterator that iterates over the matrix in traditional way,
 * a11, a12, .., a21, a22, .., a31, a32, ..
 */
template<typename T>
class mat_it
{
    public:
        /* Types: */
        typedef T                          value_type;
        typedef T*                         pointer;
        typedef const T*                   const_pointer;
        typedef T&                         reference;
        typedef const T&                   const_reference;
        typedef std::size_t                size_type;
        typedef std::ptrdiff_t             difference_type;
        typedef std::forward_iterator_tag  iterator_category;

        /**
         * Brief: Default constructor.
         */
        mat_it()
            : matrix_{nullptr},
              curr_{0},
              size_{0}
        { /* Dummy body. */ }

        /**
         * Brief: Constructor that constructs this iterator from a matrix.
         * Param: Pointer to a matrix.
         * Param: Starting index.
         */
        mat_it(matrix<T>* mat, size_type start)
            : matrix_{mat->matrix_},
              curr_{start},
              size_{mat->size_col() * mat->size_row()}
        { /* Dummy body. */ }

        /**
         * Brief: Copy constructor.
         * Param: Const reference to the other instance of this class.
         */
        mat_it(const mat_it<T>& other)
            : matrix_{other.matrix_},
              curr_{other.curr_},
              size_{other.size_}
        { /* Dummy body. */ }

        /**
         * Brief: Copy assignment operator overload.
         * Param: Const reference to the other instance of this class.
         */
        mat_it<T>& operator=(const mat_it<T>& other)
        {
            matrix_ = other.matrix_;
            curr_ = other.curr_;
            size_ = other.size_;
        }

        /**
         * Brief: Dereference operator overload, returns a direct
         *        reference to the member of the matrix that is currently
         *        being pointed at.
         */
        reference operator*()
        {
            return matrix_[curr_];
        }

        /**
         * Brief: Arrow operator overload, returns a pointer to the member
         *        of the matrix that is currently being pointed at.
         */
        pointer operator->()
        {
            return &matrix_[curr_];
        }

        /**
         * Brief: Prefix increment operator overload, returns reference
         *        to the post-increment version of this iterator.
         */
        mat_it<T>& operator++()
        {
            ++curr_;
            return *this;
        }

        /**
         * Brief: Suffix increment operator overload, returns by value
         *        a copy of this iterator in its pre-increment state.
         * Param: A dummy variable indicating that this is a suffix operator.
         */
        mat_it<T> operator++(int never_gonna_be_used)
        {
            mat_it<T> tmp{*this};
            ++curr_;
            return tmp;
        }

        /**
         * Brief: Equality operator overload, returns true if the other
         *        iterator points at the same member of the same matrix.
         * Param: Const reference to the other instance of this class.
         */
        bool operator==(const mat_it<T> other)
        {
            return matrix_ == other.matrix_ && curr_ == other.curr_;
        }

        /**
         * Brief: Inequality operator overload, returns true if the other
         *        iterator points either at a differenc member of the same
         *        matrix or at a member of completely different matrix.
         */
        bool operator!=(const mat_it<T> other)
        {
            return !(*this == other);
        }

        /**
         * Brief: Returns the total number of elements in the matrix.
         */
        size_type size() { return size_; }
    private:
        T* matrix_;
        size_type curr_, size_;
};

/**
 * The matrix class the iterators iterate over.
 */
template<typename T>
class matrix
{
    public:
        // Added iterators as friends so they can access the private members.
        friend class single_col<T>;
        friend class single_row<T>;
        friend class cols<T>;
        friend class rows<T>;
        friend class mat_it<T>;

        /* Types: */
        typedef T                          value_type;
        typedef T*                         pointer;
        typedef const T*                   const_pointer;
        typedef T&                         reference;
        typedef const T&                   const_reference;
        typedef std::size_t                size_type;
        typedef std::ptrdiff_t             difference_type;
        typedef std::forward_iterator_tag  iterator_category;

        // Note: the global scope :: operator is used because
        // gcc cannot handle redefinition of class cols/rows
        // in the methos matrix<T>::cols()/matrix<T>::rows().
        // Long live clang!:)
        typedef ::rows<T>                  rows_t;
        typedef ::cols<T>                  cols_t;
        typedef mat_it<T>                  iterator;

        void debug_print()
        {
            std::cout << "Rows: " << size_row_ << " | Cols: " << size_col_
                << std::endl;
            for(std::size_t i = 0; i < size_col_*size_row_; ++i)
            {
                if(i % size_col_ == 0 && i != 0)
                    std::cout << std::endl;
                std::cout << matrix_[i] << " ";
            }
            std::cout << std::endl;
        }

        /**
         * Brief: This constructor is used as a default one:
         * Param: Number of rows.
         * Param: Number of columns.
         * Param: Default value for the elements.
         */
        matrix(size_type r, size_type c, T def = T())
            : matrix_{new T[r*c]},
              size_row_{r}, size_col_{c}
        {
            for(size_type i = 0; i < size_row_*size_col_; ++i)
                matrix_[i] = def;
        }

        /**
         * Brief: Copy constructor.
         * Param: Const reference to the other matrix.
         */
        matrix(const matrix<T>& other)
            : matrix_{new T[other.size_row_*other.size_col_]},
              size_row_{other.size_row()},
              size_col_{other.size_col()}
        {
            // Copy the elements:
            for(size_type i = 0; i < size_row_*size_col_; ++i)
                matrix_[i] = other.matrix_[i];
        }

        /**
         * Brief: Move constructor.
         * Param: Rvalue reference to the other instance of this class.
         */
        matrix(matrix<T>&& other)
            : matrix_{other.matrix_},
              size_row_{other.size_row_},
              size_col_{other.size_col_}
        {
            // Invalidate the other matrix.
            other.matrix_ = nullptr;
            other.size_row_ = 0;
            other.size_col_ = 0;
        }

        /**
         * Brief: Copy assignment operator.
         * Param: Const reference to the other instance of this class.
         */
        matrix<T>& operator=(const matrix<T>& other)
        {
            if(this == &other) // Check for self assignment.
                return *this;

            if(matrix_ != nullptr)
                delete[] matrix_;

            size_col_ = other.size_col_;
            size_row_ = other.size_row_;

            matrix_ = new T[size_col_ * size_row_];
            for(size_type i = 0; i < size_row_*size_col_; ++i)
                matrix_[i] = other.matrix_[i];

            return *this;
        }

        /**
         * Brief: Move assignment operator overload.
         * Param: Rvalue reference to the other instance of this class.
         */
        matrix<T>& operator=(matrix<T>&& other)
        {
            if(this == &other) // Check for self-assignment.
                return *this;

            if(matrix_ != nullptr) // Already have a matrix.
                delete[] matrix_;
            matrix_ = other.matrix_;
            size_col_ = other.size_col_;
            size_row_ = other.size_row_;

            // Invalidate:
            other.matrix_ = nullptr;
            other.size_col_ = 0;
            other.size_row_ = 0;
        }

        /**
         * Brief: Destructor that handles the deletion of the matrix array.
         */
        ~matrix()
        {
            if(matrix_ != nullptr) // In case we moved the matrix before.
                delete[] matrix_;
        }


        /**
         * Brief: Index access operator overload.
         * Param: Row index.
         * Note: Returns proxy on the row, which will then be used for the
         *       column index call.
         */
        single_row<T> operator[](size_type row)
        {
            return single_row<T>{this, row, 0};
        }

        /**
         * Brief: Returns an iterator that iterates over the columns.
         */
        cols_t cols()
        {
            return cols_t{this, 0};
        }

        /**
         * Brief: Returns an iterator that iterates over the rows.
         */
        rows_t rows()
        {
            return rows_t{this, 0};
        }

        /**
         * Brief: Returns a mat_it iterator pointing at the beggining of
         *        the matrix - 0th element.
         */
        iterator begin()
        {
            return iterator{this, 0};
        }

        /**
         * Brief: Returns a mat_it iterator pointing at the end of
         *        the matrix - the one past-last element.
         */
        iterator end()
        {
            return iterator{this, size_col_ * size_row_};
        }

        /**
         * Brief: Returns the number of rows.
         */
        size_type size_row() const { return size_row_; }

        /**
         * Brief: Returns the number of rows.
         */
        size_type size_col() const { return size_col_; }
    private:
        T* matrix_; // The actual matrix array representation.
        size_type size_row_, size_col_; // Number of rows/cols.
}; 

/**************************************************************************************************/

#endif

/**/

