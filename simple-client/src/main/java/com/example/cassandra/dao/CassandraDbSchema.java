package com.example.cassandra.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.UserType;

public class CassandraDbSchema {

	private static Session session;
	private static Cluster cluster;

	public static boolean createKeyspace(){
		ResultSet results= session.execute("CREATE KEYSPACE IF NOT EXISTS test2 WITH replication"
				+ "= {'class':'SimpleStrategy', 'replication_factor':3};");

		System.out.println(results.toString());
		return true;
	}

	public static UserType getUdtDatatype(String typename){
		return cluster.getMetadata().getKeyspace("test1").getUserType(typename);
	}

	public static BoundStatement prepareKeyInsertStatement() {
		PreparedStatement statement = session.prepare("INSERT INTO test1.mapping "
				+ " (map_key)"
				+ " VALUES (?);");
		BoundStatement boundStatement = new BoundStatement(statement);
		return boundStatement;
	}

	public static BoundStatement prepareValueInsertStatement() {
		PreparedStatement statement = session.prepare("Update test1.mapping "
				+ " Set map_value = ?"
				+ " where map_key=?;");
		BoundStatement boundStatement = new BoundStatement(statement);
		return boundStatement;
	}

	public static void setCassandraClusterSession(Cluster temp_cluster, Session temp_session){
		session=temp_session;
		cluster= temp_cluster;
	}

	private static void createUdtKey(){
		ResultSet results= session.execute("CREATE TYPE IF NOT EXISTS test1.mapkey"
				+ " (eid inet,"
				+ " mask int);");

		System.out.println(results.toString());

	}

	private static void createUdtLocatorRecord(){
		ResultSet results= session.execute("CREATE TYPE IF NOT EXISTS test2.LocatorRecord(name text,"
				+ "priority int,"
				+ "weight int,"
				+ "rloc inet)");

		System.out.println(results.toString());

	}

	private static void createUdtRlocGroup(){
		ResultSet results= session.execute("CREATE TYPE IF NOT EXISTS test2.RlocGroup(ttl int,"
				+ "authoritative boolean,"
				+ "registeredDate timestamp,"
				+ "locatorRecords frozen<LocatorRecord>);");

		System.out.println(results.toString());

	}

	public static void createUdt(){
		createUdtKey();
		createUdtLocatorRecord();
		createUdtRlocGroup();
	}

	public static boolean createTableMapping(){
		ResultSet results= session.execute("CREATE TABLE IF NOT EXISTS test2.mapping "
				+ "(map_key frozen<mapkey>,"
				+ "password text,"
				+ "address frozen<RlocGroup>,"
				+ "PRIMARY KEY (map_key)) "
				+ "WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' };");

		System.out.println(results.toString());
		return true;
	}

}

/*
 * Log if table exists or is newly created.
 */