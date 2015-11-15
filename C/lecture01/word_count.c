#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define DEFAULT_MAX_LENGTH 100
#define BUFF_SIZE 100
#define MAX_WORD_COUNT 100

char* words[MAX_WORD_COUNT];
int counts[MAX_WORD_COUNT];
int order[MAX_WORD_COUNT]; // :D
int max_length = DEFAULT_MAX_LENGTH;

int inc_word(const char*);
void order_words();
void swap(int, int);

int main(int argc, char** argv)
{
	char buff[BUFF_SIZE];
	int word_count = 0;

	if(argc > 1)
		max_length = atoi(argv[1]);

	while(scanf("%s ", buff) == 1 || scanf("%s\n", buff) == 1)
	{
		if(strlen(buff) <= max_length)
		{
			++word_count;
			inc_word(buff);
		}
	}
	printf("Total count of words below %d characters: %d\n", max_length, word_count);
	printf("\nIndividual counts:\n");

	order_words();
	int max_count =0;
	for(size_t i = 0; i < MAX_WORD_COUNT; ++i)
	{
		if(counts[i] > max_count)
			max_count = counts[i];
	}

	int cnt;
	for(size_t i = 0; i < MAX_WORD_COUNT; ++i)
	{
		if(words[i] != NULL && counts[i] > 0)
		{
			cnt = 0;
			printf("%*s: ", max_length, words[i]);
			for(; cnt < counts[i]; ++cnt)
				printf("#");
			for(; cnt < max_count; ++cnt)
				printf(" ");
			printf(" |%d\n", counts[i]);
		}
	}
}

int inc_word(const char* word)
{
	for(size_t i = 0; i < MAX_WORD_COUNT; ++i)
	{
		if(words[i] != NULL && strcmp(word, words[i]) == 0)
		{
			++counts[i];
			return 1;
		}
		else if(words[i] == NULL)
		{
			words[i] = strndup(word, strlen(word));
			counts[i] = 0;
			return 1;
		}
	}
	return 0;
}

void order_words()
{
	for(size_t i = 0; i < MAX_WORD_COUNT; ++i)
		order[i] = i;

	for(size_t i = 0; i < MAX_WORD_COUNT; ++i)
	{
		for(size_t j = 0; j < MAX_WORD_COUNT -1; ++j)
		{
			if(counts[order[j]] < counts[order[j + 1]])
				swap(order[j], order[j + 1]);
		}
	}
}

void swap(int i, int j)
{
	int tmp = counts[i];
	counts[i] = counts[j];
	counts[j] = tmp;
}
