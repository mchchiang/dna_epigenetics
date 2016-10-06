/*
 * randtest.c
 *
 * output a bunch of random numbers to test generator
 */

#include<stdio.h>
#include<stdlib.h>
#include"random.h"

int main(int argc, char * argv[])
{
  FILE * fout = fopen("randnum.dat", "w");
  long i;
  int max = 1000;
  long n = 100000000;

  int * count = malloc(sizeof(int) * max);
  int r;
  
  init_rand(-1000);

  for (i = 0; i < max; i++)
    count[i] = 0;

  for (i = 0; i < n; i++)
    {
      r = rand_int(0,max);
      count[r]++;
    }
  
  for (i = 0; i < max; i++)
    fprintf(fout, "%ld %d\n", i, count[i]);

  fclose(fout);
  return EXIT_SUCCESS;
}
