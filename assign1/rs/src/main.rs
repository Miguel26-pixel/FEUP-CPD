use papi_sys::*;
use std::{time::Instant, fs::File, os::raw::c_longlong, io::prelude::Write};

const PAPI_L1_DCM: i32 = -2147483648;
const PAPI_L2_DCM: i32 = -2147483646;

fn papi_init() -> i32 {
    let mut ret:i32;
    let mut event_set:i32 = PAPI_NULL;

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

    return event_set;
}

fn papi_destroy(mut event_set: i32) -> () {
    let mut ret: i32;
    unsafe {
        ret = PAPI_remove_event(event_set, PAPI_L1_DCM );
        assert_eq!(ret as u32, PAPI_OK);

        ret = PAPI_remove_event( event_set, PAPI_L2_DCM );
        assert_eq!(ret as u32, PAPI_OK);

        ret = PAPI_destroy_eventset( &mut event_set );
        assert_eq!(ret as u32, PAPI_OK);
    }
}

fn dot_product(matrix_size: i32, event_set: i32) -> [u128; 3] {
    let mut dot_product: f64;
    let mut cache_miss_count: [c_longlong; 2] = [0, 0];
    let cache_miss_count_ptr: *mut c_longlong = &mut cache_miss_count as *mut c_longlong;

    let first_factor: Vec<f64> = vec![1.0;(matrix_size*matrix_size) as usize];
    let mut second_factor: Vec<f64> = Vec::with_capacity((matrix_size*matrix_size) as usize);
    let mut result_matrix: Vec<f64> = Vec::with_capacity((matrix_size*matrix_size) as usize);

    for i in 0..matrix_size {
        for _j in 0..matrix_size {
            second_factor.push((i+1) as f64);
        }
    }

    unsafe {
        if PAPI_start(event_set) as u32 != PAPI_OK {
            panic!("Unable to start PAPI");
        }
    }

    let starting_time = Instant::now();

    for i in 0..matrix_size {
        for j in 0..matrix_size {
            dot_product = 0.0;
            for k in 0..matrix_size {
                dot_product += first_factor[(i*matrix_size+k) as usize] * second_factor[(k*matrix_size+j) as usize];
            }
            result_matrix.push(dot_product);
        }
    }

    unsafe {
        if PAPI_stop(event_set, cache_miss_count_ptr) as u32 != PAPI_OK {
            panic!("Unable to stop PAPI");
        }
    }

    let elapsed_time = starting_time.elapsed();

    [elapsed_time.as_millis(), cache_miss_count[0] as u128, cache_miss_count[1] as u128]
}

fn line_multiplication(matrix_size: i32, event_set: i32) -> [u128; 3] {
    let mut cache_miss_count: [c_longlong; 2] = [0, 0];
    let cache_miss_count_ptr: *mut c_longlong = &mut cache_miss_count as *mut c_longlong;

    let first_factor: Vec<f64> = vec![1.0;(matrix_size*matrix_size) as usize];
    let mut second_factor: Vec<f64> = Vec::with_capacity((matrix_size*matrix_size) as usize);
    let mut result_matrix: Vec<f64> = vec![0.0;(matrix_size*matrix_size) as usize];

    for i in 0..matrix_size {
        for _j in 0..matrix_size {
            second_factor.push((i+1) as f64);
        }
    }

    unsafe {
        if PAPI_start(event_set) as u32 != PAPI_OK {
            panic!("Unable to start PAPI");
        }
    }

    let starting_time = Instant::now();

    for i in 0..matrix_size {
        for j in 0..matrix_size {
            for k in 0..matrix_size {
                result_matrix[(i * matrix_size + k) as usize] += first_factor[(i * matrix_size + j) as usize] * second_factor[(j * matrix_size + k) as usize];
            }
        }
    }

    unsafe {
        if PAPI_stop(event_set, cache_miss_count_ptr) as u32 != PAPI_OK {
            panic!("Unable to stop PAPI");
        }
    }

    let elapsed_time = starting_time.elapsed();

    [elapsed_time.as_millis(), cache_miss_count[0] as u128, cache_miss_count[1] as u128]
}

fn main() {
    let args: Vec<String> = std::env::args().collect();

    if args.len() != 2 {
        println!("Invalid argument. Correct usage: ./matrixproduct <dot | line | block>");
        return;
    }

    let algorithm = &args[1];

    let event_set = papi_init();

    let mut results: [u128; 3];

    match algorithm.as_str() {
        "dot" => {
            let mut file: File = File::create("rs_dot_product_metrics.txt").unwrap();
            for size in (600..=3000).step_by(400) {
                results = dot_product(size, event_set);

                file.write(size.to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[0].to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[1].to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[2].to_string().as_bytes()).expect("Unable to write to file.");
                file.write("\n".as_bytes()).expect("Unable to write to file");
            }
        },
        "line" => {
            let mut file: File = File::create("rs_line_multiplication_metrics.txt").unwrap();
            for size in (600..=3000).step_by(400) {
                results = line_multiplication(size, event_set);

                file.write(size.to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[0].to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[1].to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[2].to_string().as_bytes()).expect("Unable to write to file.");
                file.write("\n".as_bytes()).expect("Unable to write to file");
            }

            file = File::create("rs_line_multiplication_extended_metrics.txt").unwrap();
            for size in (4096..=10240).step_by(2048) {
                results = line_multiplication(size, event_set);

                file.write(size.to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[0].to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[1].to_string().as_bytes()).expect("Unable to write to file.");
                file.write(";".as_bytes()).expect("Unable to write to file");
                
                file.write(results[2].to_string().as_bytes()).expect("Unable to write to file.");
                file.write("\n".as_bytes()).expect("Unable to write to file");
            }
        },
        "block" => {

        },
        _ => {
            println!("Invalid argument. Correct usage: ./matrixproduct <dot | line | block>");
        }
    };

    papi_destroy(event_set);
}