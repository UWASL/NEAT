# NEAT

NEAT is a testing framework for distributed systems that simplifies coordinating multiple clients and injecting different types of network-partitioning faults. It simplifies testing by allowing developers to specify a global order for client operations and by providing a simple API for creating and healing partitions as well as crashing nodes.

### Installation
Neat requires Java 8 to run.

Install the dependencies using maven and compile the source code

```sh
$ mvn install
```

### Running Basic Test

To run the basic test provided in the example folder

```sh
$ java -jar target/netpart-0.0.1-SNAPSHOT-shaded.jar example.basic.BasicTest
```

### Project Web Page

https://dsl.uwaterloo.ca/projects/neat/
