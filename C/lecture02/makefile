CC=clang
ARGS=-std=c99 -g -fPIC
EXEC=ext

%.o: %.c
	$(CC) -c $< -o $@ $(ARGS)

all: main.o libext.so
	$(CC) main.o -o $(EXEC) $(ARGS)

clean:
	rm *.o

main.o: main.c
extreme.o: extreme.c
libext.so: extreme.o
	$(CC) -shared extreme.o -o libext.so
