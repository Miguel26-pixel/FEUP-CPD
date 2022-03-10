fn main() {
    let bindings = bindgen::Builder::default()
        .header("src/papi.h")
        .generate()
        .unwrap();

    bindings.write_to_file("../src/papi.rs")
        .expect("Couldn't write bindings!");
}
