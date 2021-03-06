/*
 * Author: Sidhant Hasija
 * Project: Lisp DB Intern Project
 *
 * 1. Connects to the Cassandra Instance, at the provided address or at the localhost
 * 2. Prepares the various cql queries which will be used by the DAO in various read/write operations
 * 3. Get the reference of Cassandra UDT into Java UserType object.
 */

package org.opendaylight.lispflowmapping.cassandradb.setup;


import java.util.HashMap;
import java.util.Map;

import org.opendaylight.lispflowmapping.cassandradb.mappings.AbstractJavaUdtMapper;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;

public class CassandraDbSetup implements ICassandraSetup{

    private static String defaultLocalAddress="127.0.0.1";
    protected static final String PASSWORD_SUBKEY = "password";
    protected static final String ADDRESS_SUBKEY = "address";
    protected static final String DB_NAME = "lispdb02";
    protected static final String CUSTOM_RLOC = "rlocgroup";
    protected static final String CUSTOM_LOCATOR_MAC = "locatorrecord_mac";
    protected static final String CUSTOM_LOCATOR_IP = "locatorrecord_ip";

	protected Cluster cluster;
    protected Session session;
    protected Metadata metadata;
    protected Map<String, UserType> udtCollection;

    protected PreparedStatement insertIpv4KeyStatement;
    protected PreparedStatement insertIpv6KeyStatement;
    protected PreparedStatement insertMacKeyStatement;
    protected PreparedStatement deleteIpv4KeyStatement;
    protected PreparedStatement deleteIpv6KeyStatement;
    protected PreparedStatement deleteMacKeyStatement;
    protected PreparedStatement insertIpv4MappingStatement;
    protected PreparedStatement insertIpv6MappingStatement;
    protected PreparedStatement insertMacMappingStatement;
    protected PreparedStatement deleteIpv4MappingStatement;
    protected PreparedStatement deleteIpv6MappingStatement;
    protected PreparedStatement deleteMacMappingStatement;
    protected PreparedStatement getIpv4MappingStatement;
	protected PreparedStatement getIpv6MappingStatement;
	protected PreparedStatement getLpmIpv6MappingStatement;
	protected PreparedStatement getMacMappingStatement;
	protected PreparedStatement getIpv6KeyStatement;
	protected PreparedStatement getIpv4KeyStatement;
	protected PreparedStatement getMacKeyStatement;
	protected static UserType rlocgroup;
	protected static UserType locatorrecord_ip;
	protected static UserType locatorrecord_mac;

	@Override
    public void connect() {
    	connect(defaultLocalAddress);
    }

	@Override
    public void connect(String node) {
    	//System.out.println("Connecting to Cassandra DB");

    	cluster = Cluster.builder()
              .addContactPoint(node)
              .build();
        metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());

        for ( Host host : metadata.getAllHosts() ) {
           System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
                 host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
     }

	@Override
	public void setup() {
		CassandraDbSchemaSetup.createAll(session);   //creates the datamodel
		createPreparedStatements();
		createUserTypes();
		createMapperObjects();
	}

	/*
	 * getting the reference of UDT's into Java objects.
	 */
	private void createUserTypes() {
		udtCollection= new HashMap<String,UserType>();

		rlocgroup = metadata.getKeyspace(DB_NAME).getUserType(CUSTOM_RLOC);
		udtCollection.put(CUSTOM_RLOC, rlocgroup);

		locatorrecord_ip = metadata.getKeyspace(DB_NAME).getUserType(CUSTOM_LOCATOR_IP);
		udtCollection.put(CUSTOM_LOCATOR_IP, locatorrecord_ip);

		locatorrecord_mac = metadata.getKeyspace(DB_NAME).getUserType(CUSTOM_LOCATOR_MAC);
		udtCollection.put(CUSTOM_LOCATOR_MAC, locatorrecord_mac);
		AbstractJavaUdtMapper.initialize(udtCollection,DB_NAME);
	}



	private void createMapperObjects() {
/*		MappingManager mappingManager= new MappingManager(session);
		mapperLispMappingsv6 = mappingManager.mapper(LispmappingsIpv6.class);
		mapperLispMappingsv4 = mappingManager.mapper(LispmappingsIpv4.class);*/
	}



	private void createPreparedStatements() {

		createInsertKeyStatements();
		createDeleteKeyStatements();
		createInsertMappingStatements();
		createDeleteMappingStatements();
		createGetKeyStatements();
		createGetMappingStatements();
	}

	/*
	 * creating cql statements to delete the mappings from the three mapping tables.
	 */
	private void createDeleteMappingStatements() {

		deleteIpv4MappingStatement = session.prepare(
				"DELETE address FROM " +
				DB_NAME +
				".lispmappings_ipv4 WHERE " +
				"prefix = ? and subprefix=?;");

		deleteIpv6MappingStatement = session.prepare(
				"DELETE address FROM " +
				DB_NAME +
				".lispmappings_ipv6 WHERE " +
				"prefix = ? and subprefix=?;");

		deleteMacMappingStatement = session.prepare(
				"DELETE address FROM " +
				DB_NAME +
				".lispmappings_mac WHERE " +
			      "eid= ?;");
	}

	/*
	 * creating cql statements to insert the mappings into the three mapping tables.
	 */
	private void createInsertMappingStatements() {

		insertIpv4MappingStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_ipv4 " +
			      "(prefix, subprefix, afi, mask, address) " +
			      "VALUES (?, ?, ?, ?, ?);");

		insertIpv6MappingStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_ipv6 " +
			      "(prefix, subprefix, afi, mask, address) " +
			      "VALUES (?, ?, ?, ?, ?);");

		insertMacMappingStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_mac " +
			      "(eid, afi, mask, address) " +
			      "VALUES (?, ?, ?, ?);");
	}

	/*
	 * creating cql statements to insert the key into the three mapping tables.
	 */
	private void createInsertKeyStatements() {

		insertIpv4KeyStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_ipv4 " +
			      "(prefix, subprefix, afi, mask, authkey) " +
			      "VALUES (?, ?, ?, ?, ?);");

		insertIpv6KeyStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_ipv6 " +
			      "(prefix, subprefix, afi, mask, authkey) " +
			      "VALUES (?, ?, ?, ?, ?);");

		insertMacKeyStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_mac " +
			      "(eid, afi, mask, authkey) " +
			      "VALUES (?, ?, ?, ?);");
	}

	/*
	 * creating cql statements to delete the key from the three mapping tables.
	 */
	private void createDeleteKeyStatements() {

		deleteIpv4KeyStatement = session.prepare(
			      "DELETE authkey FROM " +
			       DB_NAME +
			      ".lispmappings_ipv4 WHERE " +
			      "prefix = ? and subprefix=?;");

		deleteIpv6KeyStatement = session.prepare(
			      "DELETE authkey FROM " +
			       DB_NAME +
			      ".lispmappings_ipv6 WHERE " +
			      "prefix = ? and subprefix=?;");

		deleteMacKeyStatement = session.prepare(
			      "DELETE authkey FROM " +
			       DB_NAME +
			      ".lispmappings_mac WHERE " +
			      "eid= ?;");
	}

	/*
	 * creating cql statements to retrieve the mappings from the three mapping tables.
	 */
	private void createGetKeyStatements() {

		getIpv4KeyStatement = session.prepare(
			      "Select authkey FROM " +
			       DB_NAME +
			      ".lispmappings_ipv4 WHERE " +
			      "prefix = ? and subprefix=?;");

		getIpv6KeyStatement = session.prepare(
			      "Select authkey FROM " +
			       DB_NAME +
			      ".lispmappings_ipv6 WHERE " +
			      "prefix = ? and subprefix=?;");

		getMacKeyStatement = session.prepare(
			      "Select authkey FROM " +
			       DB_NAME +
			      ".lispmappings_mac WHERE " +
			      "eid= ?;");
	}


	/*
	 * creating cql statements to retrieve the mappings from the three mapping tables.
	 */
	private void createGetMappingStatements() {

		getIpv4MappingStatement = session.prepare(
			      "Select address FROM " +
			       DB_NAME +
			      ".lispmappings_ipv4 WHERE " +
			      "prefix = ? and subprefix=?;");

		getLpmIpv4MappingStatement = session.prepare(
			      "Select address FROM " +
			       DB_NAME +
			      ".lispmappings_ipv4 WHERE " +
			      "prefix = ? and subprefix < ? and mask < ? ;");

		getIpv6MappingStatement = session.prepare(
			      "Select address FROM " +
			       DB_NAME +
			      ".lispmappings_ipv6 WHERE " +
			      "prefix = ? and subprefix=?;");

		getLpmIpv6MappingStatement = session.prepare(
			      "Select address FROM " +
			       DB_NAME +
			      ".lispmappings_ipv6 WHERE " +
			      "prefix = ? and subprefix < ? and mask < ?;");

		getMacMappingStatement = session.prepare(
			      "Select address FROM " +
			       DB_NAME +
			      ".lispmappings_mac WHERE " +
			      "eid= ?;");
	}

	public static String getDbName() {
		return DB_NAME;
	}

	public Map<String, UserType> getUdtCollection() {
		return udtCollection;
	}

	public static UserType getRlocgroupUdt() {
		return rlocgroup;
	}

	public static UserType getLocatorrecordIpUdt() {
		return locatorrecord_ip;
	}

	public static UserType getLocatorrecordMacUdt() {
		return locatorrecord_mac;
	}

}
