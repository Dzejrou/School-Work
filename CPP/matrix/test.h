#pragma once

template<typename T>
class test_class
{
    public:
        typedef T value_type;
        test_class(value_type);
        ~test_class();
    private:
        value_type* val;
};
