/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.mappings;

import java.util.Map;

import com.datastax.driver.core.UserType;

public class AbstractJavaUdtMapper {

    protected static final String CUSTOM_RLOC = "rlocgroup";
    protected static final String CUSTOM_LOCATOR_MAC = "locatorrecord_mac";
    protected static final String CUSTOM_LOCATOR_IP = "locatorrecord_ip";

    protected static UserType rlocgroup;
    protected static UserType locatorrecordIp;
    protected static UserType locatorrecordMac;
    protected static String dbname;

	public static void initialize(Map<String,UserType> udtCollection, String dbname){
		rlocgroup= udtCollection.get(CUSTOM_RLOC);
		locatorrecordIp= udtCollection.get(CUSTOM_LOCATOR_IP);
		locatorrecordMac= udtCollection.get(CUSTOM_LOCATOR_MAC);
		AbstractJavaUdtMapper.dbname= dbname;
	}

	protected static String userTypeStringifier(String customLocator) {
		return "frozen" + "<" + dbname + "." + customLocator + ">";
	}
}
