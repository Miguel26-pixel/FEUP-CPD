all:c rust

c:
	g++ -O2 cpp/main.cpp -o matrixmul_cpp -lpapi
	
rust:
	$(shell cd rs; cargo build --release; cd ..)
	$(shell cp rs/target/release/matrixproduct matrixmul_rust)

clean:
	-rm -f matrixmul_cpp matrixmul_rust *metrics.txt
