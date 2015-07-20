package com.example.cassandra;

import com.example.cassandra.dao.CassandraDb;
import com.example.cassandra.lisp.MapServerEmulator;

public class LispEmulator {

	public static void main(String[] args) {

		/*
		 * Connect to Cassandra database server.
		 * Set up the schema
		 *
		 */
		CassandraDb dao= new CassandraDb("127.0.0.1");

		/*
		 * Pass the cassandra DB reference to the class emulating Lisp MapServer
		 */
		MapServerEmulator mapServer= new MapServerEmulator(dao);

		/*
		 * Put an IPv4 address type key in the databse
		 */
		mapServer.putMappingKey("192.168.1.1",32);
		System.exit(0);
	}

}





