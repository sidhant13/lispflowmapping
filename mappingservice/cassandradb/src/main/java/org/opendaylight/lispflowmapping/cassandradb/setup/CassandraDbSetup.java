/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.setup;


import org.opendaylight.lispflowmapping.cassandradb.mappings.LispmappingsIpv4;
import org.opendaylight.lispflowmapping.cassandradb.mappings.LispmappingsIpv6;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;

public class CassandraDbSetup implements ICassandraSetup{

	protected Cluster cluster;
    protected Session session;
    protected BoundStatement insertIpv4KeyStatement;
    protected BoundStatement insertIpv6KeyStatement;
    protected BoundStatement deleteIpv4KeyStatement;
    protected BoundStatement deleteIpv6KeyStatement;

    protected Mapper<LispmappingsIpv6> mapperLispMappingsv6;
    protected Mapper<LispmappingsIpv4> mapperLispMappingsv4;

    private static String defaultLocalAddress="127.0.0.1";
    protected static final String PASSWORD_SUBKEY = "password";
    protected static final String ADDRESS_SUBKEY = "address";
    protected static final String DB_NAME = "lispdb02";

	@Override
    public void connect() {
    	connect(defaultLocalAddress);
    }

	@Override
    public void connect(String node) {
    	//methodname = Thread.currentThread().getStackTrace()[1].getMethodName();LOG.info("methodname- " + methodname);
    	//System.out.println("Connecting to Cassandra DB");

    	cluster = Cluster.builder()
              .addContactPoint(node)
              .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for ( Host host : metadata.getAllHosts() ) {
           System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
                 host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
     }

	@Override
	public void setup() {
		CassandraDbSchemaSetup.createAll(session);
		createPreparedStatements();
		createMapperObjects();
	}

	private void createMapperObjects() {
		mapperLispMappingsv6 = new MappingManager(session).mapper(LispmappingsIpv6.class);
		mapperLispMappingsv4 = new MappingManager(session).mapper(LispmappingsIpv4.class);
	}



	private void createPreparedStatements() {

		createInsertKeyStatements();
		createDeleteKeyStatements();
	}

	private void createInsertKeyStatements() {

		PreparedStatement ipv4KeyStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_ipv4 " +
			      "(prefix, subprefix, afi, mask, authkey) " +
			      "VALUES (?, ?, ?, ?, ?);");
		insertIpv4KeyStatement = new BoundStatement(ipv4KeyStatement);


		PreparedStatement ipv6KeyStatement = session.prepare(
			      "INSERT INTO " +
			       DB_NAME +
			      ".lispmappings_ipv6 " +
			      "(prefix, subprefix, afi, mask, authkey) " +
			      "VALUES (?, ?, ?, ?, ?);");
		insertIpv6KeyStatement = new BoundStatement(ipv6KeyStatement);

	}


	private void createDeleteKeyStatements() {

		PreparedStatement _deleteIpv4KeyStatement = session.prepare(
			      "DELETE authkey FROM " +
			       DB_NAME +
			      ".lispmappings_ipv4 WHERE " +
			      "prefix = ? and subprefix=?;");
		deleteIpv4KeyStatement = new BoundStatement(_deleteIpv4KeyStatement);


		PreparedStatement _deleteIpv6KeyStatement = session.prepare(
			      "DELETE authkey FROM " +
			       DB_NAME +
			      ".lispmappings_ipv6 WHERE " +
			      "prefix = ? and subprefix=?;");
		deleteIpv6KeyStatement = new BoundStatement(_deleteIpv6KeyStatement);
	}

	public static String getDbName() {
		return DB_NAME;
	}

}
