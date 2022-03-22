#include <stdio.h>
#include <iostream>
#include <iomanip>
#include <time.h>
#include <cstdlib>
#include <papi.h>

#define SYSTEMTIME clock_t
 
void matrixMultiplication(int matrixSize) 
{
	
	SYSTEMTIME Time1, Time2;
		
	double dotProduct;
	int i, j, k;

	double *firstFactor, *secondFactor, *resultMatrix;

    firstFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	secondFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	resultMatrix = (double *)malloc((matrixSize * matrixSize) * sizeof(double));

	for(i = 0; i < matrixSize; i++)
		for(j = 0; j < matrixSize; j++)
			firstFactor[i * matrixSize + j] = (double) 1.0;



	for(i = 0; i < matrixSize; i++)
		for(j = 0; j < matrixSize; j++)
			secondFactor[i * matrixSize + j] = (double) (i+1);



    Time1 = clock();

	for(i = 0; i < matrixSize; i++) {
		for(j = 0; j < matrixSize; j++) {	
			dotProduct = 0;
			for(k= 0; k < matrixSize; k++) {	
				dotProduct += firstFactor[i * matrixSize + k] * secondFactor[k * matrixSize + j];
			}
			resultMatrix[i * matrixSize + j] = dotProduct;
		}
	}


    Time2 = clock();

	std::cout << "Elapsed time: " << (double)(Time2 - Time1) / CLOCKS_PER_SEC << "s" << std::endl;

	// display 10 elements of the result matrix tto verify correctness
	std::cout << "First 10 elements of the result matrix: " << std::endl;
	
	for(i = 0; i < 1; i++) {	
		for(j = 0; j < std::min(10, matrixSize); j++)
			std::cout << resultMatrix[j] << " ";
	}
	std::cout << std::endl;

    free(firstFactor);
    free(secondFactor);
    free(resultMatrix);
}

// add code here for line x line matriz multiplication
void matrixLineMultiplication(int matrixSize) {
    SYSTEMTIME Time1, Time2;
		
	int i, j, k;

	double *firstFactor, *secondFactor, *resultMatrix;

    firstFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	secondFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	resultMatrix = (double *)malloc((matrixSize * matrixSize) * sizeof(double));

	for(i = 0; i < matrixSize; i++)
		for(j = 0; j < matrixSize; j++) {
			firstFactor[i * matrixSize + j]  = (double) 1.0;
			secondFactor[i * matrixSize + j] = (double) (i+1);
			resultMatrix[i * matrixSize + j] = (double) 0.0;
		}

    Time1 = clock();

	for (i = 0; i < matrixSize; i++) {
		for (j = 0; j < matrixSize; j++) {
			for (k = 0; k < matrixSize; k++) {
				resultMatrix[i * matrixSize + k] += firstFactor[i * matrixSize + j] * secondFactor[j * matrixSize + k];
			}
		}
	}


    Time2 = clock();

	std::cout << "Elapsed time: " << (double)(Time2 - Time1) / CLOCKS_PER_SEC << "s" << std::endl;

	// display 10 elements of the result matrix tto verify correctness
	std::cout << "First 10 elements of the result matrix: " << std::endl;
	
	for(i = 0; i < 1; i++) {	
		for(j = 0; j < std::min(10, matrixSize); j++)
			std::cout << resultMatrix[j] << " ";
	}
	std::cout << std::endl;

    free(firstFactor);
    free(secondFactor);
    free(resultMatrix);
}

// add code here for block x block matriz multiplication
void OnMultBlock(int m_ar, int m_br, int bkSize)
{
    SYSTEMTIME Time1, Time2;
		
	int i, j;

	double *firstFactor, *secondFactor, *resultMatrix;

    firstFactor = (double *)malloc((m_ar * m_ar) * sizeof(double));
	secondFactor = (double *)malloc((m_br * m_br) * sizeof(double));
	resultMatrix = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i = 0; i < m_ar; i++)
		for(j = 0; j < m_ar; j++){
			firstFactor[i * m_ar + j]  = (double) 1.0;
			secondFactor[i * m_ar + j] = (double) (i+1);
			resultMatrix[i * m_ar + j] = (double) 0.0;
		}

	Time1 = clock();

	int i, j, k, I, J, K;
	double block;
	for (J=0 ; J<m_ar ; J+=bkSize) {
		for (K=0 ; K<m_ar ; K+=bkSize) {
			for (I=0 ; I<m_ar ; I+=bkSize) {
				for (j=J ; j<J+bkSize-1 && j<m_ar ; j++) {
					for (k=K; k<K+bkSize-1 && k<m_ar ; k++) {
						block = firstFactor[j*m_ar+k];
						for (i=I ; i<I+bkSize-1 && i<m_ar ; i++)
							resultMatrix[j*m_ar+i] += secondFactor[k*m_ar+i]*block;
					}
			}
			}
		}
	}

	Time2 = clock();

	std::cout << "Elapsed time: " << (double)(Time2 - Time1) / CLOCKS_PER_SEC << "s" << std::endl;

	// display 10 elements of the result matrix tto verify correctness
	std::cout << "First 10 elements of the result matrix: " << std::endl;
	
	for(i = 0; i < 1; i++) {	
		for(j = 0; j < std::min(10, m_ar); j++)
			std::cout << resultMatrix[j] << " ";
	}
	std::cout << std::endl;

    free(firstFactor);
    free(secondFactor);
    free(resultMatrix);
    
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
				matrixMultiplication(matrixSize);
				break;
			case 2:
				matrixLineMultiplication(matrixSize);  
				break;
			case 3:
				std::cout << "Block Size? ";
				std::cout << std::endl << "1. 128 blocks" << std::endl 
					<< "2. 256 blocks" << std::endl
					<< "3. 512 blocks" << std::endl;
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
