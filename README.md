# lispflowmapping
Forked from the Opendaylight lispflowmapping project, it replaces the storage of mappings from internally in Java collection to Cassandra database.

## Setup
1. To run the code and store the mappings in Cassandra, access to an instance of Cassandra is required. To install an instance locally, download and install the distribution from http://cassandra.apache.org/download/. The code was tested with the most recent stable release of the time, i.e. 2.1.9. However, it should also work fine with 2.2.x version.

2. To access the Cassandra database from the Java code, cassandra java drivers need to be referrred, see https://github.com/datastax/java-driver. Dependency of them has been incluided in the project pom file and features definition.
The compatible drivers with the cassandra 2.1 are the 2.1.7 release. If you plan to use cassandra 2.2, update the version of drivers accordingly in the dependency information.
