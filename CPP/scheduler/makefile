CC = clang++
CFLAGS = -pthread -std=c++14 -g

all: du3sch.o Source.o Stopwatch.o
	$(CC) $(CFLAGS) du3sch.o Source.o Stopwatch.o -o sched

%.o : %.cpp
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm *.o sched

# Targets:
du3sch: du3sch.hpp du3sch.cpp
Source.o: Source.cpp Stopwatch.h Stopwatch.cpp du3sch.cpp du3sch.hpp
Stopwatch.o: Stopwatch.h Stopwatch.cpp
