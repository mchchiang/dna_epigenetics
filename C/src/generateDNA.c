/* 
 * generateDNA.c
 *
 * Generate the position of the nucleosomes
 * 
 * Arguments required:
 * lx, ly, lz, N, seed
 */

#include<stdio.h>
#include<stdlib.h>
#include<math.h>

int main (int argc, char * argv[])
{
  if (argc != 6)
    {
      printf("Need five arguments in total.\n");
      return EXIT_FAILURE;
    }
  
  double lx = strtod(argv[1], NULL);
  double ly = strtod(argv[2], NULL);
  double lz = strtod(argv[3], NULL);
  int N = (int) strtol(argv[4], NULL, 10);
  int seed = (int) strtol(argv[5], NULL, 10);
  int dimension = 3;
  
  double ** dna;
  int i, j;
  dna = malloc(sizeof(*dna) * N);
  for (i = 0 ; i < N; i++)
    {
      dna[i] = malloc(sizeof(double) * dimension);
    }

  /* 
   * generate the string of nucleosomes (beads)
   * the first nucleosome is at (0,0,0)
   * the second nucleosome is at a radial distance r = 1 
   * from the first nucleosome at a random set of angles (theta, phi)
   * and so on
   */

  for (i = 0; i < dimension; i++)
      dna[0][i] = 0.0;

  double r1, phi;
  double costheta, sintheta;
  double pi = atan(1.0) * 4.0;
  srand(seed);

  for (i = 1; i < N; i++)
    {
      do
	{
	  r1 = ((double) rand()/ (double) RAND_MAX);
	  costheta = 1.0 - 2.0 * r1; //correspond to theta range [0, pi]
	  sintheta = sqrt(1.0 - costheta*costheta);
	  r1 = ((double) rand()/ (double) RAND_MAX);
	  phi = 2.0 * pi * r1;
	  dna[i][0] = dna[i-1][0] + sintheta * cos(phi);
	  dna[i][1] = dna[i-1][1] + sintheta * sin(phi);
	  dna[i][2] = dna[i-1][2] + costheta;
	}
      while(fabs(dna[i][0]) > lx / 2.0 ||
	    fabs(dna[i][1]) > ly / 2.0 ||
	    fabs(dna[i][2]) > lz / 2.0);
    }
  
  /* write initial configuration to file */
  char * filename = "dna_init_config.dat";
  FILE * fout;
  int type = 1;
  fout = fopen(filename, "w");
  
  for (i = 0; i < N; i++)
    {
      fprintf(fout, "%d %d ", (i+1), type);
      for (j = 0; j < dimension; j++)
	{
	  fprintf(fout, "%.16f ", dna[i][j]); 
	}
      fprintf(fout, "\n");
    }

  for (i = 0; i < N; i++)
    {
      free(dna[i]);
    }
  free(dna);
  
  return EXIT_SUCCESS;
}
