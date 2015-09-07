/*
 * Author: Sidhant Hasija
 * Project: Lisp DB Intern Project
 *
 * 1. Uses the cassandra session object to create schema/ data model at the session instance.
 * 2. The queries used in this can also be found at https://github.com/sidhant13/lispflowmapping/blob/master/resources/Cassandra_odl_2.cql
 */

package org.opendaylight.lispflowmapping.cassandradb.setup;

import com.datastax.driver.core.Session;

public class CassandraDbSchemaSetup {

	static Session session;

	public static void createAll(Session session){
		CassandraDbSchemaSetup.session=session;
		createKeyspace();
		createAddressDatetypes();
		createRlocDatatypes();
		createTables();
	}

	private static void createAddressDatetypes() {

		session.execute("CREATE TYPE IF NOT EXISTS lispdb02.ipaddress("
				+ "address inet,"
				+ "afi int);");

		session.execute("CREATE TYPE IF NOT EXISTS lispdb02.macaddress("
				+ "address text,"
				+ "afi int);");

		System.out.println("Created ipaddress and macaddress UDT...");
	}

	private static void createRlocDatatypes() {

		session.execute("CREATE TYPE IF NOT EXISTS lispdb02.locatorrecord_ip("
				+ "name text,"
				+ "priority int,"
				+ "weight int,"
				+ "multicastpriority int,"
				+ "multicastweight int,"
				+ "locallocator boolean,"
				+ "rlocprobed boolean,"
				+ "routed boolean,"
				+ "address inet,"
				+ "afi int"
				+ ");");

		session.execute("CREATE TYPE IF NOT EXISTS lispdb02.locatorrecord_mac("
				+ "name text,"
				+ "priority int,"
				+ "weight int,"
				+ "multicastpriority int,"
				+ "multicastweight int,"
				+ "locallocator boolean,"
				+ "rlocprobed boolean,"
				+ "routed boolean,"
				+ "address text,"
				+ "afi int"
				+ ");");

		session.execute("CREATE TYPE IF NOT EXISTS lispdb02.rlocgroup("
				+ "ttl int,"
				+ "authoritative boolean,"
				+ "registeredDate timestamp,"
				+ "action int,"
				+ "rloc_ip list<frozen<locatorrecord_ip>>,"
				+ "rloc_mac list<frozen<locatorrecord_mac>>"
				+ ");");

		System.out.println("Created locatorrecords and rlocgroup UDT...");
	}



	private static void createKeyspace() {

		session.execute("CREATE KEYSPACE IF NOT EXISTS lispdb02 WITH replication= {"
				+ "'class':'SimpleStrategy',"
				+ "'replication_factor':1"
				+ "	};");

		System.out.println("Created Keyspace lispdb02...");
	}




	private static void createTables() {

		session.execute("CREATE TABLE IF NOT EXISTS lispdb02.lispmappings_ipv4"
				+ "("
				+ "prefix int,"
				+ "subprefix int,"
				+ "mask int,"
				+ "afi int,"
				+ "authkey text,"
				+ "address frozen<rlocgroup>,"
				+ "PRIMARY KEY ((prefix),subprefix)"
				+ ")"
				+ "WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' }"
				+ "AND clustering order by (subprefix DESC);");

		session.execute("CREATE TABLE IF NOT EXISTS lispdb02.lispmappings_ipv6"
				+ "("
				+ "prefix bigint,"
				+ "subprefix bigint,"
				+ "mask int,"
				+ "afi int,"
				+ "authkey text,"
				+ "address frozen<rlocgroup>,"
				+ "PRIMARY KEY ((prefix),subprefix)"
				+ ")"
				+ "WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' }"
				+ "AND clustering order by (subprefix DESC);");

		session.execute("CREATE TABLE IF NOT EXISTS lispdb02.lispmappings_mac"
				+ "("
				+ "eid text,"
				+ "mask int,"
				+ "afi int,"
				+ "authkey text,"
				+ "address frozen<rlocgroup>,"
				+ "PRIMARY KEY (eid)"
				+ ")"
				+ "WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' };");

		System.out.println("Created LispMapping Tables...");

	}


}
