# lispflowmapping
Forked from the Opendaylight lispflowmapping project, it replaces the storage of mappings from internally in Java collection to Cassandra database.

## Setup
1. To run the code and store the mappings in Cassandra, access to an instance of Cassandra is required. To install an instance locally, download and setup the distribution from [here](http://cassandra.apache.org/download/). The code was tested with the most recent stable release of the time, i.e. 2.1.9. However, it should also work fine with 2.2.x version.

2. To access the Cassandra database from the Java code, cassandra java drivers need to be referrred, see [link](https://github.com/datastax/java-driver). Their dependency information has been included in the project pom file and features definition.
The compatible drivers with the cassandra 2.1 are the 2.1.7 release. If you plan to use cassandra 2.2, update the version of drivers accordingly in the version tag.

3. For information on how to build the source code, set up the development environment and run karaf refer [Opendaylight wiki](https://wiki.opendaylight.org/view/GettingStarted:Developer_Main).

## Running
1. The cassandraDb project upon being started in the karaf creates the default database schema with the name lispdb02 also described in [file](https://github.com/sidhant13/lispflowmapping/blob/master/resources/Cassandra_odl_2.cql). If you wish to use some other manually configured database, change the name of it in the CassandraDbSetup class variable.

2. Use this [postman collection](https://github.com/sidhant13/lispflowmapping/blob/master/mappingservice/implementation/src/main/resources/lfm_RPCs.json.postman_collection) to  to send and retrieve mappings from the lispflowmapping server.

3. If you wish to shift back to using java collections for storing mappings i.e HashmapDb, see the instructions in CassandraDbModule class. You can use the mappping_blaster.py script in resources folder to quantify the performance in both the cases.

4. To test if the mappings are stored as expected in the CassandraDb, open cqlsh program (cassandra query language shell) which is installed along with the database, shift to the relevant database, lispdb02 in the deafult case, and query the data from relevant tables.

5. For more information on the project refer to the Lisp DB project at the wiki https://wiki.opendaylight.org/view/Interns:Main
