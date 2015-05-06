CC = clang++
CF = -std=c++11 -g -Wall

test: du1test.o du1matrix.o
	$(CC) du1test.o du1matrix.o -o test $(CF)

du1matrix.o: du1matrix.hpp du1matrix.cpp
	$(CC) -c du1matrix.cpp $(CF)

du1test.o: du1test.cpp du1matrix.hpp du1matrix.cpp
	$(CC) -c du1test.cpp $(CF)

clean:
	touch test # If it doesn't exist.
	rm test
	rm *.o

