/*
 * dnamodel.c
 *
 */
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include"random.h"
int n = 60;
double F = 0.4;
double alpha;
int sweeps = 10000;
int myseed = -1;
int run;
int * dna;
int numInState[3];
int time = 0;
char * filepath;
double * probMState;
FILE * statefile;
FILE * probfile;

void init_random_state();
void init_specified_state(int * states);
void next_step();
void recruit_conversion(int n1, int n2);
void noisy_conversion(int n1, double p1, double p2);
void open_writers();
void close_writers();
void set_state(int index, int value);
int get_state(int index);
int rand_int(int min, int max);
double rand_double();


int main(int argc, char * argv[])
{
  if (argc < 6)
    {
      printf("Need 5 arguments (n, ratio, sweeps, run, filepath)\n");
      return EXIT_FAILURE;
    }

  //read in parameter values
  n = (int) strtol(argv[1], NULL, 10);
  F = strtod(argv[2], NULL);
  sweeps = (int) strtol(argv[3], NULL, 10);
  run = strtol(argv[4], NULL, 10);
  filepath = malloc(sizeof(char) * (strlen(argv[5])+1));
  strcpy(filepath, argv[5]);
  alpha = F / (1 + F); 
  
  probMState = malloc(sizeof(double) * (n+1));
  int i;
  for (i = 0; i <= n; i++)
    {
      probMState[i] = 0.0;
    }
  
  init_rand(myseed); //initialise random generator
  
  //initialise the dna
  dna = malloc(sizeof(int) * n);
  if (dna == NULL)
    {
      printf("malloc fails in creating dna\n");
      return EXIT_FAILURE;
    }
  init_random_state();
  
  //open output files
  int size = 200;
  char * name = malloc(sizeof(char) * (size+1));
  size = snprintf(name, size,  "n_%d_F_%.2f_t_%d_run_%d.dat", n, F, sweeps, run);
  name = realloc(name, sizeof(char) * (size+1));
  snprintf(name, size+1, "n_%d_F_%.2f_t_%d_run_%d.dat", n, F, sweeps, run);
  char * statefilename = malloc(sizeof(char) * (size+strlen("state_")+1));
  char * probfilename = malloc(sizeof(char) * (size+strlen("prob_")+1));
  strcpy(statefilename, "state_");
  strcat(statefilename, name);
  strcpy(probfilename, "prob_");
  strcat(probfilename, name);
  statefile = fopen(statefilename, "w");
  probfile = fopen(probfilename, "w");
  if (statefile == NULL || probfile == NULL)
    {
      printf("Fail to open output files\n");
      return EXIT_FAILURE;
    }
 
  //run the simulation
  for (time = 0; time < sweeps; time++)
    {
      for (i = 0; i < n; i++)
	{
	  next_step();
	}
      //      printf("t = %d\n", time);
      fprintf(statefile, "%d %d %d %d\n", 
	      time, numInState[0], numInState[1], numInState[2]);
      probMState[numInState[2]] += 1.0;
    }

  for (i = 0; i <= n; i++)
    {
      probMState[i] /= (double) sweeps;
      fprintf(probfile, "%d %.5f\n", i, probMState[i]);
    }

  //close output files
  fclose(statefile);
  fclose(probfile);

  free(dna);
  free(name);
  free(probMState);
  free(filepath);

  return EXIT_SUCCESS;
}


void init_random_state()
{
  numInState[0] = 0;
  numInState[1] = 0;
  numInState[2] = 0;
  int i;
  for (i = 0; i < n; i++)
    {
      int s = rand_int(0, 3);
      dna[i] = s;
      numInState[s]++;
    }
}

void init_specified_state(int * states)
{
  numInState[0] = 0;
  numInState[1] = 0;
  numInState[2] = 0;
  int i;
  for (i = 0; i < n; i++)
    {
      int s = states[i];
      dna[i] = s;
      numInState[s]++;
    }
}

void next_step()
{
  //step 1: select a random nucleosome
  int n1 = rand_int(0, n);
  double p1 = rand_double();

  //step 2a: recruited conversion
  if (p1 < alpha)
    {
      //pick another nucleosome site
      int n2;
      do
	{
	  n2 = rand_int(0, n);
	}
      while (n2 == n1);
      recruit_conversion(n1, n2);
    }
  else
    {
      noisy_conversion(n1, rand_double(), rand_double());
    }
}

void recruit_conversion(int n1, int n2)
{
  int s1 = dna[n1];
  int s2 = dna[n2];
  if (s1 != s2 && s2 != 1)
    {
      if (s2 == 2)
	{
	  set_state(n1, s1+1);
	}
      else if (s2 == 0)
	{
	  set_state(n1, s1-1);
	}
    }
}

void noisy_conversion(int n1, double p1, double p2)
{
  int s1 = dna[n1];
  if (s1 == 0 && p1 < 1.0/3.0)
    {
      set_state(n1, s1+1);
    }
  else if (s1 == 1 && p1 < 1.0/3.0)
    {
      set_state(n1, s1+1);
    }
  else if (s1 == 1 && p1 < 2.0/3.0)
    {
      set_state(n1, s1-1);
    }
  else if (s1 == 2 && p1 < 1.0/3.0)
    {
      set_state(n1, s1-1);
    }
}

void set_state(int index, int value)
{
  if (index >= 0 && index < n &&
      value >= 0 && value <= 2)
    {
      numInState[dna[index]]--;
      dna[index] = value;
      numInState[value]++;
    }
}

int get_state(int index)
{
  if (index >= 0 && index < n)
    {
      return dna[index];
    }
  return -1;
}
