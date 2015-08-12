/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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

		session.execute("CREATE TYPE IF NOT EXISTS lispdb01.ipaddress("
				+ "address inet,"
				+ "afi int);");

		session.execute("CREATE TYPE IF NOT EXISTS lispdb01.macaddress("
				+ "address text,"
				+ "afi int);");

		System.out.println("Created ipaddress and macaddress UDT...");
	}

	private static void createRlocDatatypes() {

		session.execute("CREATE TYPE IF NOT EXISTS lispdb01.locatorrecord("
				+ "name text,"
				+ "priority int,"
				+ "weight int,"
				+ "multicastpriority int,"
				+ "multicastweight int,"
				+ "locallocator boolean,"
				+ "rlocprobed boolean,"
				+ "routed boolean,"
				+ "rloc_ip frozen<ipaddress>,"
				+ "rloc_mac frozen<macaddress>,"
				+ ");");

		session.execute("CREATE TYPE IF NOT EXISTS lispdb01.rlocgroup("
				+ "ttl int,"
				+ "authoritative boolean,"
				+ "registeredDate timestamp,"
				+ "action int,"
				+ "records frozen<locatorrecord>"
				+ ");");

		System.out.println("Created locatorrecord and rlocgroup UDT...");
	}



	private static void createKeyspace() {

		session.execute("CREATE KEYSPACE IF NOT EXISTS lispdb01 WITH replication= {"
				+ "'class':'SimpleStrategy',"
				+ "'replication_factor':1"
				+ "	};");

		System.out.println("Created Keyspace lispdb01...");
	}




	private static void createTables() {

		session.execute("CREATE TABLE IF NOT EXISTS lispdb01.lispmappings_ipv4"
				+ "("
				+ "prefix int,"
				+ "subprefix int,"
				+ "afi int,"
				+ "mask int,"
				+ "authkey text,"
				+ "address frozen<rlocgroup>,"
				+ "PRIMARY KEY ((prefix),subprefix)"
				+ ")"
				+ "WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' }"
				+ "AND clustering order by (subprefix DESC);");

		session.execute("CREATE TABLE IF NOT EXISTS lispdb01.lispmappings_ipv6"
				+ "("
				+ "prefix int,"
				+ "subprefix int,"
				+ "afi int,"
				+ "mask int,"
				+ "authkey text,"
				+ "address frozen<rlocgroup>,"
				+ "PRIMARY KEY ((prefix),subprefix)"
				+ ")"
				+ "WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' }"
				+ "AND clustering order by (subprefix DESC);");

		session.execute("CREATE TABLE IF NOT EXISTS lispdb01.lispmappings_mac"
				+ "("
				+ "eid text,"
				+ "afi int,"
				+ "mask int,"
				+ "authkey text,"
				+ "address frozen<rlocgroup>,"
				+ "PRIMARY KEY (eid)"
				+ ")"
				+ "WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' };");

		System.out.println("Created LispMapping Tables...");

	}


}
