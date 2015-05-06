CC = clang++
N = -o app
T = -o test
BI = -o bez_indexu
CPP = -std=c++11

install: du1test1.cpp du1.o
	@echo Compiling main.
	@$(CC) du1test1.cpp du1.o $(N) $(CPP)

test: du1test2.cpp du1.o
	@echo Compiling test main.
	@$(CC) du1test2.cpp du1.o $(T) $(CPP)

bez_indexu: du1test2.cpp du1_bezindexu.o
	@echo Compiling no index main.
	@$(CC) du1test2_bezindexu.cpp du1_bezindexu.o $(BI) $(CPP)

du1.o: du1.h du1.cpp
	@echo Compiling du1.
	@$(CC) du1.cpp -c $(CPP)

du1_bezindexu.o: du1_bezindexu.h du1_bezindexu.cpp
	@echo Compiling du1_bezindexu.
	@$(CC) du1_bezindexu.cpp -c $(CPP)
