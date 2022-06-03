# Distributed and Partitioned Key-Value Store
>Check the project's report for information about the design and implementation details.

## Compiling
- From the src directory of the project2, simply use `make` to compile

## Clean
- To clean all the files created by compiling, simply use `make clean`

## Executing
- From the src directory of the project2:
  - To invoke a service node use:
    - `java Store <IP_mcast_addr> <IP_mcast_port> <node_id> <Store_port>`
  - To invoke a test client service use:
    - `java TestClient <node_ap> <operation> [<opnd>]`
