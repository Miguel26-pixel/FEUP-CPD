#include <papi.h>
#include <iostream>
#include <string>
#include <vector>

#define ERROR -1
#define SUCCESS 0

int papiInit(int& eventSet) {
    if (PAPI_library_init(PAPI_VER_CURRENT) != PAPI_VER_CURRENT) {
        std::cout << "Unable to initialize PAPI." << std::endl;
        return ERROR;
    }

    eventSet = PAPI_NULL;

    if (PAPI_create_eventset(&eventSet) != PAPI_OK) {
        std::cout << "Unable to create set of PAPI events." << std::endl;
        return ERROR;
    }

    if (PAPI_add_event(eventSet, PAPI_L1_DCM) != PAPI_OK) {
        std::cout << "Unable to add L1 cache misses to set of PAPI events." << std::endl;
        return ERROR;
    }

    if (PAPI_add_event(eventSet, PAPI_L2_DCM) != PAPI_OK) {
        std::cout << "Unable to add L2 cache misses to set of PAPI events." << std::endl;
        return ERROR;
    }

    return SUCCESS;
}

int papiDestroy(int& eventSet) {

    if (PAPI_remove_event(eventSet, PAPI_L1_DCM) != PAPI_OK) {
        std::cout << "Unable to remove L1 cache misses from set of PAPI events." << std::endl;
        return ERROR;
    }

    if (PAPI_remove_event(eventSet, PAPI_L2_DCM) != PAPI_OK) {
        std::cout << "Unable to remove L2 cache misses from set of PAPI events." << std::endl;
        return ERROR;
    }

    if (PAPI_destroy_eventset(&eventSet) != PAPI_OK) {
        std::cout << "Unable to destroy set of PAPI events." << std::endl;
        return ERROR;
    }

    return SUCCESS;
}

std::vector<double> dotMultiplication(int matrixSize, int& eventSet) {
    clock_t start, end;
    double dotProduct;
    long long cache_miss_count[2];

    double *firstFactor, *secondFactor, *resultMatrix;

    firstFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	secondFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	resultMatrix = (double *)malloc((matrixSize * matrixSize) * sizeof(double));

    for(int i = 0; i < matrixSize; i++)
		for(int j = 0; j < matrixSize; j++) {
            firstFactor[i*matrixSize + j] = 1.0;
			secondFactor[i*matrixSize + j] = i + 1.0;
        }

    if (PAPI_start(eventSet) != PAPI_OK) {
        std::cout << "Unable to start PAPI. Cache miss count should be ignored." << std::endl;
    }

    start = clock();

    for(int i = 0; i < matrixSize; i++) {
		for(int j = 0; j < matrixSize; j++) {
			dotProduct = 0.0;
			for(int k= 0; k < matrixSize; k++) {
				dotProduct += firstFactor[i * matrixSize + k] * secondFactor[k * matrixSize + j];
			}
			resultMatrix[i * matrixSize + j] = dotProduct;
		}
	}

    end = clock();

    if (PAPI_stop(eventSet, cache_miss_count) != PAPI_OK) {
        std::cout << "Unable to stop PAPI. Cache miss count should be ignored." << std::endl;
    }

    double elapsedTime = (double)(end - start) / CLOCKS_PER_SEC;

    return std::vector<double>({elapsedTime, (double)cache_miss_count[0], (double)cache_miss_count[1]});
}

std::vector<double> lineMultiplication(int matrixSize, int& eventSet) {
    clock_t start, end;
    long long cache_miss_count[2];

    double *firstFactor, *secondFactor, *resultMatrix;

    firstFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	secondFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	resultMatrix = (double *)malloc((matrixSize * matrixSize) * sizeof(double));

    for(int i = 0; i < matrixSize; i++)
		for(int j = 0; j < matrixSize; j++) {
			firstFactor[i*matrixSize + j] = 1.0;
			secondFactor[i*matrixSize + j] = i + 1.0;
			resultMatrix[i*matrixSize + j] = 0.0;
		}

    if (PAPI_start(eventSet) != PAPI_OK) {
        std::cout << "Unable to start PAPI. Cache miss count should be ignored." << std::endl;
    }

    start = clock();

    for (int i = 0; i < matrixSize; i++) {
		for (int j = 0; j < matrixSize; j++) {
			for (int k = 0; k < matrixSize; k++) {
				resultMatrix[i * matrixSize + k] += firstFactor[i * matrixSize + j] * secondFactor[j * matrixSize + k];
			}
		}
	}

    end = clock();

    if (PAPI_stop(eventSet, cache_miss_count) != PAPI_OK) {
        std::cout << "Unable to stop PAPI. Cache miss count should be ignored." << std::endl;
    }

    double elapsedTime = (double) (end - start) / CLOCKS_PER_SEC;

    return std::vector<double>({elapsedTime, (double)cache_miss_count[0], (double)cache_miss_count[1]});

}

std::vector<double> blockMultiplication(int matrixSize, int blockSize, int& eventSet) {
    clock_t start, end;
    long long cache_miss_count[2];

    int i, ii, j, jj, k, kk;
    double block;

    double *firstFactor, *secondFactor, *resultMatrix;

    firstFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	secondFactor = (double *)malloc((matrixSize * matrixSize) * sizeof(double));
	resultMatrix = (double *)malloc((matrixSize * matrixSize) * sizeof(double));

    for(i = 0; i < matrixSize; i++)
		for(j = 0; j < matrixSize; j++) {
			firstFactor[i*matrixSize + j] = 1.0;
			secondFactor[i*matrixSize + j] = i + 1.0;
			resultMatrix[i*matrixSize + j] = 0.0;
		}

    if (PAPI_start(eventSet) != PAPI_OK) {
        std::cout << "Unable to start PAPI. Cache miss count should be ignored." << std::endl;
    }

    start = clock();

    for (ii = 0 ; ii < matrixSize ; ii += blockSize)
		for (jj = 0 ; jj < matrixSize ; jj += blockSize)
			for (kk = 0 ; kk < matrixSize ; kk += blockSize)
				for (i = ii ; i < ii + blockSize ; i++)
					for (j = jj; j < jj + blockSize; j++)
						for (k = kk ; k < kk + blockSize ; k++)
							resultMatrix[i * matrixSize + k] += firstFactor[i * matrixSize + j] * secondFactor[j * matrixSize + k];

    end = clock();

    if (PAPI_stop(eventSet, cache_miss_count) != PAPI_OK) {
        std::cout << "Unable to stop PAPI. Cache miss count should be ignored." << std::endl;
    }

    double elapsedTime = (double) (end - start) / CLOCKS_PER_SEC;

    return std::vector<double>({elapsedTime, (double)cache_miss_count[0], (double)cache_miss_count[1]});

}

int main (int argc, char* argv[]) {
    if (argc < 2) {
        std::cout << "Correct usage: ./matrixMult <dot | line | block>" << std::endl;
        return ERROR;
    }

    int eventSet;
    if (papiInit(eventSet) != SUCCESS) {
        return ERROR;
    }

    std::string operation = argv[1];
    std::vector<double> ret;

    if (operation == "dot") {
        ret = dotMultiplication(1024, eventSet);
    } else if (operation == "line") {
        ret = lineMultiplication(2048, eventSet);
    } else if (operation == "block") {
        ret = blockMultiplication(2048, 512, eventSet);
    } else {
        std::cout << "Invalid argument." << std::endl 
            << "Correct usage: ./matrixMult <dot | line | block>" 
            << std::endl;
        return ERROR;
    }
    
    std::cout << "Elapsed time: " << ret[0] << std::endl;
    std::cout << "L1 Cache miss count: " << ret[1] << std::endl;
    std::cout << "L2 Cache miss count: " << ret[2] << std::endl;

    if (papiDestroy(eventSet) != SUCCESS) {
        return ERROR;
    }

    return SUCCESS;
}