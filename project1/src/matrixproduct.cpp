#include <stdio.h>
#include <iostream>
#include <iomanip>
#include <time.h>
#include <cstdlib>
#include <papi.h>

#define SYSTEMTIME clock_t
 
void OnMult(int m_ar, int m_br) 
{
	
	SYSTEMTIME Time1, Time2;
	
	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;
	

		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;



	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);



    Time1 = clock();

	for(i=0; i<m_ar; i++)
	{	for( j=0; j<m_br; j++)
		{	temp = 0;
			for( k=0; k<m_ar; k++)
			{	
				temp += pha[i*m_ar+k] * phb[k*m_br+j];
			}
			phc[i*m_ar+j]=temp;
		}
	}


    Time2 = clock();
	sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1) / CLOCKS_PER_SEC);
	std::cout << st;

	// display 10 elements of the result matrix tto verify correctness
	std::cout << "Result matrix: " << std::endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<std::min(10,m_br); j++)
			std::cout << phc[j] << " ";
	}
	std::cout << std::endl;

    free(pha);
    free(phb);
    free(phc);
	
	
}

// add code here for line x line matriz multiplication
void OnMultLine(int m_ar, int m_br)
{
    
    
}

// add code here for block x block matriz multiplication
void OnMultBlock(int m_ar, int m_br, int bkSize)
{
    
    
}



void handle_error (int retval)
{
  printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(1);
}

void init_papi() {
  int retval = PAPI_library_init(PAPI_VER_CURRENT);
  if (retval != PAPI_VER_CURRENT && retval < 0) {
    printf("PAPI library version mismatch!\n");
    exit(1);
  }
  if (retval < 0) handle_error(retval);

  std::cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
            << " MINOR: " << PAPI_VERSION_MINOR(retval)
            << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
}


int main (int argc, char *argv[])
{
	int matrixSize, blockSize;
	int operation;
	
	int PAPIEvents = PAPI_NULL;
  	long long values[2];
  	int ret;
	

	ret = PAPI_library_init( PAPI_VER_CURRENT );
	if ( ret != PAPI_VER_CURRENT )
		std::cout << "FAIL" << std::endl;


	ret = PAPI_create_eventset(&PAPIEvents);
		if (ret != PAPI_OK) std::cout << "ERROR: create eventset" << std::endl;


	ret = PAPI_add_event(PAPIEvents,PAPI_L1_DCM );
	if (ret != PAPI_OK) std::cout << "ERROR: PAPI_L1_DCM" << std::endl;


	ret = PAPI_add_event(PAPIEvents,PAPI_L2_DCM);
	if (ret != PAPI_OK) std::cout << "ERROR: PAPI_L2_DCM" << std::endl;


	operation=1;
	do {
		std::cout << std::endl << "1. Multiplication" << std::endl 
			<< "2. Line Multiplication" << std::endl
			<< "3. Block Multiplication" << std::endl
			<< "Selection?: ";
		
		std::cin >>operation;
		
		if (operation == 0)
			break;
		
		
		std::cout << "Matrix Size: ";
   		std::cin >> matrixSize;

		
		ret = PAPI_start(PAPIEvents);
		if (ret != PAPI_OK)
			std::cout << "ERROR: Start PAPI" << std::endl;

		switch (operation){
			case 1:
				OnMult(matrixSize, matrixSize);
				break;
			case 2:
				OnMultLine(matrixSize, matrixSize);  
				break;
			case 3:
				std::cout << "Block Size? ";
				std::cin >> blockSize;
				
				OnMultBlock(matrixSize, matrixSize, blockSize);  
				break;

		}

  		ret = PAPI_stop(PAPIEvents, values);
  		if (ret != PAPI_OK) std::cout << "ERROR: Stop PAPI" << std::endl;

		std::cout << "L1 Cache Misses: " << values[0] << std::endl 
			<< "L2 Cache Misses: " << values[1] << std::endl; 

		ret = PAPI_reset(PAPIEvents);
		if (ret != PAPI_OK)
			std::cout << "FAIL reset" << std::endl; 



	}while (operation != 0);

	ret = PAPI_remove_event( PAPIEvents, PAPI_L1_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << std::endl; 

	ret = PAPI_remove_event( PAPIEvents, PAPI_L2_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << std::endl; 

	ret = PAPI_destroy_eventset( &PAPIEvents );
	if ( ret != PAPI_OK )
		std::cout << "FAIL destroy" << std::endl;

}
