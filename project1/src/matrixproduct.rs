use std::{thread,cmp,time::{Duration,Instant}};

fn on_mult(m_ar:i32, m_br:i32) -> () {
    let mut temp:f64;

    let size:usize = (m_ar*m_ar) as usize;

    let mut pha: Vec<f64> = vec![1.0;size];
    let mut phb: Vec<f64> = Vec::with_capacity(size);
    let mut phc: Vec<f64> = Vec::with_capacity(size);

    for i in 0..m_br {
        for j in 0..m_br {
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

/*fn on_mult_line(m_ar:i32, m_br:i32) -> () {

}*/

fn main() {
    on_mult(1000, 1000);

    println!("hello world");
}