#include <iostream>

template<typename T>
class test
{
    public:
        test<T>* const new_t()
        {
            return new test<T>();
        }
        T a;
};

int main()
{
    test<int> t{};
    t.a = 1;
    test<int>* p = t.new_t();
    p->a = 2;

    p = t.new_t();
    p->a = 3;

    std::cout << p->a << " -- " << t.a;
    return 0;
}
