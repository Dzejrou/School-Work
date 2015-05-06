// DU2multiset.cpp
// Jaroslav Jindrak NPRG051 2014/2015

#include "du2multiset.hpp"

template<int>
struct A
{ // Hint test, thanks:)
    static std::size_t value()
    {
        static std::size_t n = 0;
        return ++n;
    }
};

int main()
{
    multi_set<color, std::string, int, int, int> mset;

    /* Insert: */
    mset.insert(color::red, "red", 255, 0, 0);
    mset.insert(color::blue, "blue", 0, 0, 255);
    mset.insert(color::orange, "orange", 255, 165, 0);
    mset.insert(color::cyan, "cyan", 0, 255, 255);
    mset.insert(color::magenta, "magenta", 255, 0, 255);
    mset.insert(color::black, "black", 0, 0, 0);
    mset.insert(color::white, "white", 255, 255, 255);
    mset.insert(color::teal, "teal", 0, 128, 128);
    mset.insert(color::green, "green", 0, 255, 0);
    mset.insert(color::purple, "purple", 128, 0, 128);
    mset.insert(color::pink, "pink", 255, 192, 203);
    
    std::cout << "Multi_set:" << std::endl;
    for(int i = color::red; i != color::pink; ++i)
    {
        auto res = mset.find<0>(i);
        print(res);
    }

    /* Find: */
    std::cout << std::endl << "Starting tests." << std::endl;
    std::cout << "Test #1." << std::endl;
    auto& res1 = mset.find<1>("red"); 
    std::cout << "Test #2." << std::endl;
    auto& res2 = mset.find<0,1>(color::blue, "blue");
    std::cout << "Test #3." << std::endl;
    auto& res3 = mset.find<2,3,4>(128, 0, 128);
    std::cout << "Test #4." << std::endl;
    auto& res4 = mset.find<0,1,2,3,4>(color::orange, "orange", 255, 165, 0);

    std::cout << std::endl << "Result batch #1:" << std::endl;
    print(res1);
    print(res2);
    print(res3);
    print(res4);
    std::cout << std::endl;

    /* Exceptions: */
    try
    {
        std::cout << "Test #5." << std::endl;
        mset.find<2>(255);
    }
    catch(multiple_result_exception& ex)
    {
        std::cout << ex.what() << std::endl << std::endl;;
    }
    catch(...)
    {
        std::cout << "Wait, what?" << std::endl;
    }

    try
    {
        std::cout << "Test #6." << std::endl;
        mset.find<1>("Chimichanga!"); 
    }
    catch(no_result_found_exception& ex)
    {
        std::cout << ex.what() << std::endl << std::endl;
    }
    catch(...)
    {
        std::cout << "Wait, what?" << std::endl;
    }

    /* Erase: */
    try
    {
        std::cout << "Test #7." << std::endl;

        auto& res5 = mset.find<1,2>("red", 255); // Just to be sure.
        std::cout << "Before erase:" << std::endl << "\t";
        print(res5);

        mset.erase<1,2>("red", 255);
        std::cout << "After erase:" << std::endl << "\t";
        mset.find<1,2>("red", 255);
    }
    catch(no_result_found_exception& ex)
    {
        std::cout << ex.what() << std::endl << std::endl;
    }
    catch(...)
    {
        std::cout << "Wait, what?" << std::endl;
    }


    /* Erase all 255s: */
    std::cout << "Test #8:" << std::endl; 
    try
    {
        mset.erase<2>(255);
        mset.find<2>(255);
    }
    catch(no_result_found_exception& ex)
    { // I want no matching searches.
        std::cout << "PASSED: " << ex.what() << std::endl; 
    }
    catch(multiple_result_exception& ex)
    { // Multiple matching searches indicate that the erase failed.
        std::cout << "FAILED: " << ex.what() << std::endl;
    }
}
