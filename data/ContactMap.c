#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

int main(int argc, char * argv[]){
  if (argc != 7){
    printf("Wrong number of arguments!\n");
    exit(1);
  }
  int n = (int) strtol(argv[1], NULL, 10);
  int tstart = (int) strtol(argv[2], NULL, 10);
  int tend = (int) strtol(argv[3], NULL, 10);
  double cutoff = strtod(argv[4], NULL);
  char * infile = argv[5];
  char * outfile = argv[6];
  
  int startLine = tstart * (n+2);
  int endLine = (tend+1) * (n+2);
  int lineNum = 0;
  int count = 0;
  int sample = 0;
  
  FILE * fin = fopen(infile, "r");
  
  double contact[n][n];
  double pos[n][3];
  int mag[n];
  
  // Initialise the arrays
  int i, j;
  for (i = 0; i < n; i++){
    for (j = 0; j < n; j++){
      contact[i][j] = 0;
    }
    for (j = 0; j < 3; j++){
      pos[i][j] = 0.0;
    }
    mag[i] = 0;
  }
  
  // Check to make sure the file exists
  if (fin == NULL)
    {
      printf("File does not exists %s\n", infile);
      exit(1);
    }
  
  printf("Starting Reading File\n");
  
  int maxLength = 128;
  char line[maxLength];
  int numCol = 4;
  char * data[numCol];
  char * token;
  char delim[2] = " ";
  char * s;
  while (fgets(line, maxLength, fin) != NULL){
    if (lineNum >= startLine && lineNum < endLine){
      if (count >= 2 && count < n+2){
	int k = 0;
	token = strtok(line, delim);
	while (token != NULL){
	  data[k] = token;
	  token = strtok(NULL, delim);
	  k++;
	}
	int site = count-2;
	pos[site][0] = strtod(data[1], NULL);
	pos[site][1] = strtod(data[2], NULL);
	pos[site][2] = strtod(data[3], NULL);
	
	s = data[0];
	if (strcmp(s,"O") == 0 || strcmp(s,"H") == 0){
	  mag[site] = 0;
	} else if (strcmp(s,"N") == 0 || strcmp(s,"F") == 0){
	  mag[site] = 1;
	} else if (strcmp(s,"C") == 0 || strcmp(s,"S") == 0){
	  mag[site] = 2;
	}
      }
      
      count++;
      
      if (count == n+2){
	count = 0;
	sample++;
	
	// Compute the contact map
	double  dx, dy, dz, diff;
	for (i = 0; i < n; i++){
	  for (j = 0; j < i+1; j++){
	    dx = pos[i][0] - pos[j][0];
	    dy = pos[i][1] - pos[j][1];
	    dz = pos[i][2] - pos[j][2];
	    diff = sqrt(dx*dx+dy*dy+dz*dz);
	    if (diff <= cutoff){
	      if (mag[i] == 0 && mag[j] == 0){
		contact[i][j] += 1;
	      } else if (mag[i] == 2 && mag[j] == 2){
		contact[i][j] += 3;
	      } else {
		contact[i][j] += 2;
	      }
	      //contact[i][j] += 1;
	    }
	  }
	}
      }
    }
    
    lineNum++;
    
    if (lineNum >= endLine){
      break;
    }
  }
  
  fclose(fin);
  
  for (i = 0; i < n; i++)
    for (j = 0; j < i+1; j++)
      contact[i][j] /= (float) sample;
  
  FILE * fout = fopen(outfile, "w");

  // Check to make sure the file exists
  if (fout == NULL){
    printf("File does not exists %s\n", outfile);
    exit(1);
  }

  int x, y;
  for (i = 0; i < n; i++){
    for (j = 0; j < n; j++){
      x = i;
      y = j;
      
      if (j > i){
	x = j;
	y = i;
      }
      
      fprintf(fout, "%d %d %.5f\n", i, j, contact[x][y]);
    }
    fprintf(fout, "\n");
  }
  
  fclose(fout);
  
  exit(0);
}

