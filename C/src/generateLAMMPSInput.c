/*
 * generateLAMMPSInput.c
 *
 */
#include<stdio.h>
#include<stdlib.h>


int main (int argc, char * argv[])
{
  double lx, ly, lz;
  int i, j;
  int nbead;
  int nbond = nbead-1;
  int nangle = nangle-2;

  char * foutname = "lammps_input";
  FILE * fout;
  
  fout = fopen(foutname, "w");
  fprintf(fout, "LAMMPS data file from restart file: timestep = 0, procs = 1\n");
  fprintf(fout, "%d\tatoms\n", nbead);
  fprintf(fout, "%d\tbonds\n", nbond);
  fprintf(fout, "%d\tangles\n\n", nangle);
  
  fprintf(fout, "%d\tatom types");

  fprintf(fout, "%f %f xlo xhi", -lx/2.0, lx/2.0);
  fprintf(fout, "%f %f ylo yhi", -ly/2.0, ly/2.0);
  fprintf(fout, "%f %f zlo zhi", -lz/2.0, lz/2.0);

  fprintf(fout, "\n\nMasses\n\n");
  

  fprintf(fout, "\nAtoms\n\n");

  for (i = 0; i < nbead; i++)
    {
      fprintf(fout, "%d %d %d %.16f %.16f %.16f %d %d %d");
    }

  fprintf("\n\nVelocities\n\n");
  
  for (i = 0; i < nbead; i++)
    {
      fprintf(fout, "%d %d %d %d", (i+1), 0, 0, 0);
    }

  fprintf(fout, "\n\nBonds\n\n");

  for (i = 0; i < nbond; i++)
    {
      fprintf(fout, "%d %d %d %d", (i+1), 1, (i+1), (i+2));
    }

  fprintf(fout, "\n\nAngles\n\n");

  for (i = 0; i < nangle; i++)
    {
      fprintf(fout, "%d %d %d %d %d", (i+1), 1, (i+1), (i+2), (i+3));
    }

}
