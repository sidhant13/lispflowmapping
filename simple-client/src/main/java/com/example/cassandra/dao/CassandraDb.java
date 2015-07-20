package com.example.cassandra.dao;

import java.util.Map;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.example.cassandra.lisp.MappingServiceKey;
import com.example.cassandra.lisp.MappingTable;

public class CassandraDb {
   private Cluster cluster;
   private Session session;

   private UserType udt_mapkey_type;
   private BoundStatement keyInsertStatement;
   private BoundStatement valueInsertStatement;
   Mapper<MappingTable> mapper;

   public CassandraDb(String node){

	   connect(node);
	   /*
	    * Set up Cassandra schema.
	    */
	   CassandraDbSchema.setCassandraClusterSession(cluster, session);
	   CassandraDbSchema.createKeyspace();
	   CassandraDbSchema.createUdt();
	   CassandraDbSchema.createTableMapping();

	   /*
	    * Prepared statement to insert key and values
	    */
	   keyInsertStatement= CassandraDbSchema.prepareKeyInsertStatement();
	   valueInsertStatement= CassandraDbSchema.prepareValueInsertStatement();
	   /*
	    * Get UDT type reference from database
	    */
	   udt_mapkey_type= CassandraDbSchema.getUdtDatatype("mapkey");

	   /*
	    * Get reference of mapper that maps Java Objects to Cassandra tables and UDT
	    */
	   mapper = new MappingManager(session).mapper(MappingTable.class);
   }

	/*
	 * Put key and values in Cassandra using prepared statement.
	 */
    public void put(MappingServiceKey key, Map<String, String>... value_map){

	UDTValue udt_mapkey_value= udt_mapkey_type.newValue().setInet("eid", key.getEid())
			  .setInt("mask", key.getMask());
	//session.execute(keyInsertStatement.bind(udt_mapkey_value));
	session.execute(valueInsertStatement.bind(value_map[0],udt_mapkey_value));
	querySchema();
   }

   /*
    * Put key and values using Mapper Object
    */
   public void put(MappingTable mapping_entry){

	//UDTValue udt_mapkey_value= udt_mapkey_type.newValue().setInet("eid", key.getEid())
			//  .setInt("mask", key.getMask());
	 System.out.println(mapping_entry.toString());
	mapper.save(mapping_entry);
	//querySchema();
   }

   private void connect(String node) {
      cluster = Cluster.builder()
            .addContactPoint(node)
            .build();
      Metadata metadata = cluster.getMetadata();
      System.out.printf("Connected to cluster: %s\n",
            metadata.getClusterName());
      for ( Host host : metadata.getAllHosts() ) {
         System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
               host.getDatacenter(), host.getAddress(), host.getRack());
      }
      session = cluster.connect();
   }




   public void querySchema() {
      ResultSet results = session.execute("SELECT * FROM test1.mapping " +
             "");
/*      for (Row row : results) {
	UDTValue value = row.getUDTValue("map_key");

      System.out.println(value.getInet("eid") + "," + value.getInt("mask") + row.getMap("arg0", Class<String>, Class<String>));
      }*/
   }

   public void close() {
      session.close();
      cluster.close();
   }
}
