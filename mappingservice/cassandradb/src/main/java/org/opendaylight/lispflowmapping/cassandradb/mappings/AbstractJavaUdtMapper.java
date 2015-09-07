/*
 * Author: Sidhant Hasija
 * Project: Lisp DB Intern Project
 *
 * 1. Abstract class for Java to UDT mapper and vice-versa.
 *
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

    /*
     * store the reference for UDT retrieved while setting up the schema
     */
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
