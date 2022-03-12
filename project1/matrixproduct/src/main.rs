use papi_sys::*;
use std::{io,cmp,time::Instant};

const PAPI_L1_DCM: i32 = -2147483648;
const PAPI_L2_DCM: i32 = -2147483646;

fn matrix_multiplication(matrix_size: i32) -> () {
    let mut dot_product:f64;

    let first_factor: Vec<f64> = vec![1.0;matrix_size as usize];
    let mut second_factor: Vec<f64> = Vec::with_capacity(matrix_size as usize);
    let mut result_matrix: Vec<f64> = Vec::with_capacity(matrix_size as usize);

    for i in 0..matrix_size {
        for _j in 0..matrix_size {
            second_factor.push((i+1) as f64);
        }
    }

    let now = Instant::now();

    for i in 0..matrix_size {
        for j in 0..matrix_size {
            dot_product = 0.0;
            for k in 0..matrix_size {
                dot_product += first_factor[(i*matrix_size+k) as usize] * second_factor[(k*matrix_size+j) as usize];
            }
            result_matrix.push(dot_product);
        }
    }

    let elapsed_time = now.elapsed();

    println!("Time: {} seconds", elapsed_time.as_millis());

    for j in 0..cmp::min(10,matrix_size) {
        print!("{} ",result_matrix[j as usize]);
    }

    println!();
}

fn matrix_line_multiplication(matrix_size: i32) -> () {
    let first_factor: Vec<f64> = vec![1.0;(matrix_size*matrix_size) as usize];
    let mut second_factor: Vec<f64> = Vec::with_capacity((matrix_size*matrix_size) as usize);
    let mut result_matrix: Vec<f64> = vec![0.0;(matrix_size*matrix_size) as usize];

    for i in 0..matrix_size {
        for _j in 0..matrix_size {
            second_factor.push((i+1) as f64);
        }
    }

    let now = Instant::now();

    for i in 0..matrix_size {
        for j in 0..matrix_size {
            for k in 0..matrix_size {
                result_matrix[(i * matrix_size + k) as usize] += first_factor[(i * matrix_size + j) as usize] * second_factor[(j * matrix_size + k) as usize];
            }
        }
    }

    let elapsed_time = now.elapsed();

    println!("Time: {} seconds", elapsed_time.as_millis());

    for j in 0..cmp::min(10,matrix_size) {
        print!("{} ",result_matrix[j as usize]);
    }

    println!();
}

fn main() {
    let mut ret:i32;
    let mut event_set:i32 = PAPI_NULL;
    let mut cache_miss_count:[i64;2] = [0,0];
    let values: *mut i64 = &mut cache_miss_count as *mut i64;

    unsafe {
        ret = PAPI_library_init(PAPI_VER_CURRENT);
        assert_eq!(ret, PAPI_VER_CURRENT);

        ret = PAPI_create_eventset(&mut event_set);
        assert_eq!(ret as u32, PAPI_OK);

        ret = PAPI_add_event(event_set,PAPI_L1_DCM);
        assert_eq!(ret as u32, PAPI_OK);

        ret = PAPI_add_event(event_set,PAPI_L2_DCM);
        assert_eq!(ret as u32, PAPI_OK);
    }

    loop {
        println!("1. Multiplication");
		println!("2. Line Multiplication");
		println!("3. Block Multiplication"); 
        println!("0. Exit"); 
		println!("Selection?: ");

        let mut option = String::new();
        let stdin = io::stdin(); // We get `Stdin` here.
        stdin.read_line(&mut option).unwrap();

        if option == "0\n" {
            break;
        }

        println!("Dimensions: lins=cols ? ");

        let mut buffer = String::new();
        let stdin = io::stdin(); // We get `Stdin` here.
        stdin.read_line(&mut buffer).unwrap();

        let matrix_size: i32;
        let trimmed = buffer.trim();
        
        match trimmed.parse::<i32>() {
            Ok(i) => matrix_size = i,
            Err(..) => continue,
        };

        unsafe {
            ret = PAPI_start(event_set);
            assert_eq!(ret as u32, PAPI_OK);
        }

        if option == "1\n" {
            matrix_multiplication(matrix_size);
        }
        if option == "2\n" {
            matrix_line_multiplication(matrix_size);
        }

        unsafe {
            ret = PAPI_stop(event_set, values);
            assert_eq!(ret as u32, PAPI_OK);
        }

        println!("L1 Cache Misses: {}",cache_miss_count[0]);
        println!("L2 Cache Misses: {}",cache_miss_count[1]);
    }

    unsafe {
        ret = PAPI_remove_event( event_set, PAPI_L1_DCM );
        assert_eq!(ret as u32, PAPI_OK);

        ret = PAPI_remove_event( event_set, PAPI_L2_DCM );
        assert_eq!(ret as u32, PAPI_OK);

        ret = PAPI_destroy_eventset( &mut event_set );
        assert_eq!(ret as u32, PAPI_OK);
    }
}
