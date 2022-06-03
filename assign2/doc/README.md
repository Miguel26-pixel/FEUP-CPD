# Distributed and Partitioned Key-Value Store
>Check the project's report for information about the design and implementation details.

## Java Version
- For this project it was used java 18

## Compiling
- From the src directory of the project2, simply use `make` to compile

## Clean
- To clean all the files created when compiling, simply use `make clean`

## Executing
- Execute rmi register, using `rmiregister`. An alternative is to run the process in the background using `remiregister &`
- From the src directory of the project2:
  - To invoke a service node use:
    - `java Store <IP_mcast_addr> <IP_mcast_port> <node_id> <Store_port>`
  - To invoke a test client service use:
    - `java TestClient <node_ap> <operation> [<opnd>]`
