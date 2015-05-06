#include <iostream>
#include <future>
#include <ctime>
#include <map>
#include <vector>
#include <algorithm>
#include "Stopwatch.h"
#include "du3sch.hpp"
//#define VOID_TEST 1
static std::size_t Count = 1000;

static std::size_t MaxNum = 10000000;

static std::size_t CoreCount = 8;

typedef std::map<std::size_t, bool> PrimeMap;

bool IsPrime(std::size_t num)
{
	for (std::size_t i = 2; i < num; i++)
	{
		if (num % i == 0)
			return false;
	}
	return true;
}

void Test(std::size_t num)
{
    //bool is_prime = true;
	for (std::size_t i = 2; i < num; i++)
	{
		if (num % i == 0)
            return;
            //is_prime = false;
	}
    //if(is_prime)
	//	std::cerr << num << " is prime!" << std::endl;
}

template<typename SCHEDULER>
void TestScheduler(const char* msg, const PrimeMap& value, time_sec serialTime)
{
	ticks_t start = now();
	SCHEDULER scheduler(CoreCount);
	std::map<std::size_t, std::size_t> tasks;
	for (std::size_t i = 0; i < Count; i++)
	{
#ifdef VOID_TEST
		tasks[i] = scheduler.add_task([i]() -> void {Test(i); });
		tasks[MaxNum - i] = scheduler.add_task([i]() -> void {Test(MaxNum - i); });
#else
		tasks[i] = scheduler.add_task([i]() -> bool { return IsPrime(i); });
		tasks[MaxNum - i] = scheduler.add_task([i]() -> bool { return IsPrime(MaxNum - i); });
#endif
	}

	for (auto i = tasks.begin(); i != tasks.end(); ++i)
	{
#ifdef VOID_TEST
        scheduler.get_task_result(i->second);
#else
		if (value.at(i->first) != scheduler.get_task_result(i->second))
			throw 0;
#endif
	}
	time_sec parallelTime = ticks_to_time(now() - start);
	std::cout << msg << " time = " << parallelTime << " [s]  -  Serial time portion = " << parallelTime / (serialTime / 100.0) << " [%]" << std::endl;
	std::cout << msg << " calculation correct" << std::endl;
}

int main()
{
#ifdef VOID_TEST
    std::cerr << "Void test." << std::endl;
#else
    std::cerr << "Int test." << std::endl;
#endif
	ticks_t start = now();
	PrimeMap value;
	for (std::size_t i = 0; i < Count; i++)
	{
#ifdef VOID_TEST
		Test(i);
		Test(MaxNum - i);
#else
		value[i] = IsPrime(i);
		value[MaxNum - i] = IsPrime(MaxNum - i);
#endif
	}
	time_sec serialTime = ticks_to_time(now() - start);
	std::cout << "Serial time = " << serialTime << std::endl;

#ifdef VOID_TEST
	TestScheduler<Scheduler<void, std::function<void(void)>>>("Scheduler", value, serialTime);
#else
	TestScheduler<Scheduler<bool, std::function<bool(void)> > >("Scheduler", value, serialTime);
#endif

	system("pause");
	return 0;
}
