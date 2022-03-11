use papi_sys::*;
use std::{io,cmp,time::Instant};

const PAPI_L1_DCM: i32 = -2147483648;
const PAPI_L2_DCM: i32 = -2147483646;

fn on_mult(m_ar:i32, m_br:i32) -> () {
    let mut temp:f64;

    let size:usize = (m_ar*m_ar) as usize;

    let pha: Vec<f64> = vec![1.0;size];
    let mut phb: Vec<f64> = Vec::with_capacity(size);
    let mut phc: Vec<f64> = Vec::with_capacity(size);

    for i in 0..size {
        for _j in 0..m_br {
            phb.push((i+1) as f64);
        }
    }

    let now = Instant::now();

    for i in 0..m_ar {
        for j in 0..m_br {
            temp = 0.0;
            for k in 0..m_ar {
                temp += pha[(i*m_ar+k) as usize] * phb[(k*m_br+j) as usize];
            }
            phc.push(temp);
        }
    }

    let elapsed_time = now.elapsed();

    println!("Time: {} seconds", elapsed_time.as_millis());

    for j in 0..cmp::min(10,m_br) {
        print!("{} ",phc[j as usize]);
    }

    println!();
}

fn on_mult_line(_m_ar:i32, _m_br:i32) -> () {

}

fn main() {
    let mut ret:i32;
    let mut event_set:i32 = PAPI_NULL;
    let mut a:[i64;2] = [0,0];
    let values: *mut i64 = &mut a as *mut i64;

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

        let size:i32;
        let trimmed = buffer.trim();
        match trimmed.parse::<i32>() {
            Ok(i) => size = i,
            Err(..) => continue,
        };

        unsafe {
            ret = PAPI_start(event_set);
            assert_eq!(ret as u32, PAPI_OK);
        }

        if option == "1\n" {
            on_mult(size, size);
        }
        if option == "2\n" {
            on_mult_line(size, size);
        }

        unsafe {
            ret = PAPI_stop(event_set, values);
            assert_eq!(ret as u32, PAPI_OK);
        }

        println!("L1 DCM: {}",a[0]);
        println!("L2 DCM: {}",a[1]);
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
