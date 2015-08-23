/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*import org.opendaylight.lispflowmapping.cassandradb.mappings.LispmappingsIpv4;
import org.opendaylight.lispflowmapping.cassandradb.mappings.LispmappingsIpv6;
import org.opendaylight.lispflowmapping.cassandradb.mappings.LocatorRecordIp;
import org.opendaylight.lispflowmapping.cassandradb.mappings.LocatorRecordMac;*/
import org.opendaylight.lispflowmapping.cassandradb.mappings.MappingServiceRLOCGroupUDTMapper;
import org.opendaylight.lispflowmapping.cassandradb.mappings.UdtMappingServiceRlocGroupMapper;
//import org.opendaylight.lispflowmapping.cassandradb.mappings.RlocGroup;
import org.opendaylight.lispflowmapping.cassandradb.setup.CassandraDbSetup;
import org.opendaylight.lispflowmapping.cassandradb.util.LispAddressParseUtil;
import org.opendaylight.lispflowmapping.interfaces.dao.ILispDAO;
import org.opendaylight.lispflowmapping.interfaces.dao.IRowVisitor;
import org.opendaylight.lispflowmapping.interfaces.dao.MappingEntry;
import org.opendaylight.lispflowmapping.interfaces.dao.MappingServiceRLOCGroup;
//import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.LispAddressContainer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv6;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Mac;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
/*
import com.datastax.driver.core.PreparedStatement;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Mac;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.locatorrecords.LocatorRecord;
*/
import com.datastax.driver.core.UDTValue;

import org.opendaylight.lispflowmapping.interfaces.dao.IMappingServiceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraDb extends CassandraDbSetup implements ILispDAO,AutoCloseable{

    private static CassandraDb cassandraInstance = null;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private int recordTimeOut = 240;
    protected static final Logger LOG = LoggerFactory.getLogger(CassandraDb.class);

    public static CassandraDb getInstance(){
    	if(cassandraInstance==null)
    		cassandraInstance = new CassandraDb();
		LOG.info("CassandraDb configured");
		return cassandraInstance;
	}

	@Override
	public void put(Object key, MappingEntry<?>... values) {

        for (MappingEntry<?> entry : values) {
            if(entry.getKey().equals(PASSWORD_SUBKEY)){
            	putAuthenticationKey(key, entry.getValue());
            }
            if(entry.getKey().equals(ADDRESS_SUBKEY)){
            	putMapping(key, entry.getValue());
            }
        }
	}

	private void putMapping(Object key, Object value) {

		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

	    if(value instanceof MappingServiceRLOCGroup){
	    	 MappingServiceRLOCGroup rValue= (MappingServiceRLOCGroup) value;
	         UDTValue rlocGroupValue= MappingServiceRLOCGroupUDTMapper.getUdtfromRlocGroup(rValue);

			if(address instanceof Ipv4)
				putIpv4Mapping((Ipv4) address, rlocGroupValue, mask);
			else if(address instanceof Ipv6)
				putIpv6Mapping((Ipv6) address, rlocGroupValue, mask);
			else if(address instanceof Mac)
				putMacMapping((Mac) address, rlocGroupValue, mask);
		    }
	}

	private void putMacMapping(Mac address, UDTValue rlocGroupValue, int mask) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(insertMacMappingStatement.bind(
	    		 	util.getMacEid(),
	    		 	util.getAfi(),
	    		 	mask,
	    		 	rlocGroupValue
	    		 ));
	}

	private void putIpv6Mapping(Ipv6 address, UDTValue rlocGroupValue, int mask) {

	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(insertIpv6MappingStatement.bind(
	    		 	util.getIpv6prefix(),
	    		 	util.getIpv6suffix(),
	    		 	util.getAfi(),
	    		 	mask,
	    		 	rlocGroupValue
	    		 ));
	}

	private void putIpv4Mapping(Ipv4 address, UDTValue rlocGroupValue, int mask) {

	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(insertIpv4MappingStatement.bind(
	    		 	util.getPrefix(),
	    		 	util.getSuffix(),
	    		 	util.getAfi(),
	    		 	mask,
	    		 	rlocGroupValue
	    		 ));
	}

	private void putAuthenticationKey(Object key, Object value) {

		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

		if(address instanceof Ipv4)
			putIpv4AuthenticationKey((Ipv4) address, mask, value);
		else if(address instanceof Ipv6)
			putIpv6AuthenticationKey((Ipv6) address, mask, value);
		else if(address instanceof Mac)
			putMacAuthenticationKey((Mac) address, mask, value);
	}

	private void putMacAuthenticationKey(Mac address, int mask, Object value) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(insertMacKeyStatement.bind(
	    		 	util.getMacEid(),
	    		 	util.getAfi(),
	    		 	mask,
	    		 	(String) value
	    		 ));
	}

	private void putIpv4AuthenticationKey(Ipv4 address,int mask, Object value) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(insertIpv4KeyStatement.bind(
	    		 	util.getPrefix(),
	    		 	util.getSuffix(),
	    		 	util.getAfi(),
	    		 	mask,
	    		 	(String) value
	    		 ));
	}

	private void putIpv6AuthenticationKey(Ipv6 address, int mask, Object value) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(insertIpv6KeyStatement.bind(
	    		 	util.getIpv6prefix(),
	    		 	util.getIpv6suffix(),
	    		 	util.getAfi(),
	    		 	mask,
	    		 	(String) value
	    		 ));
	}

	@Override
	public void removeSpecific(Object key, String valueKey) {
        if(valueKey.equals(PASSWORD_SUBKEY)){
        	removeAuthenticationKey(key);
        }
        else if(valueKey.equals(ADDRESS_SUBKEY)){
        	removeMapppingKey(key);
        }
	}

	private void removeMapppingKey(Object key) {
		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

		if(address instanceof Ipv4)
			removeIpv4MappingKey((Ipv4) address, mask);
		else if(address instanceof Ipv6)
			removeIpv6MappingKey((Ipv6) address, mask);
		else if(address instanceof Mac)
			removeMacMappingKey((Mac) address, mask);
	}

	private void removeMacMappingKey(Mac address, int mask) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteMacMappingStatement.bind(
	    		 	util.getMacEid()
	    		 ));
	}

	private void removeIpv6MappingKey(Ipv6 address, int mask) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteIpv6MappingStatement.bind(
	    		 	util.getIpv6prefix(),
	    		 	util.getIpv6suffix()
	    		 ));
	}

	private void removeIpv4MappingKey(Ipv4 address, int mask) {

	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteIpv4MappingStatement.bind(
	    		 	util.getPrefix(),
	    		 	util.getSuffix()
	    		 ));
	}

	private void removeAuthenticationKey(Object key) {

		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

		if(address instanceof Ipv4)
			removeIpv4AuthenticationKey((Ipv4) address, mask);
		else if(address instanceof Ipv6)
			removeIpv6AuthenticationKey((Ipv6) address, mask);
		else if(address instanceof Mac)
			removeMacAuthenticationKey((Mac) address, mask);
	}

	private void removeMacAuthenticationKey(Mac address, int mask) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteMacKeyStatement.bind(
	    		 	util.getMacEid()
	    		 	));
	}

	private void removeIpv6AuthenticationKey(Ipv6 address, int mask) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteIpv6KeyStatement.bind(
	    		 	util.getIpv6prefix(),
	    		 	util.getIpv6suffix()
	    		 ));
	}

	private void removeIpv4AuthenticationKey(Ipv4 address, int mask) {
	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteIpv4KeyStatement.bind(
	    		 	util.getPrefix(),
	    		 	util.getSuffix()
	    		 ));
	}

	@Override
	public Object getSpecific(Object key, String valueKey) {

		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

		if(valueKey.equals(PASSWORD_SUBKEY))
			return getAuthenticationKey(address, mask);
		else if(valueKey.equals(PASSWORD_SUBKEY))
			return getMapping(address, mask);

		return null;
	}

	private Object getAuthenticationKey(Address address, int mask){

		String gotSpecific="";
		if(address instanceof Ipv4){
		    LispAddressParseUtil util= new LispAddressParseUtil((Ipv4) address);
			ResultSet results = session.execute(getIpv4KeyStatement.bind(
		    		 	util.getPrefix(),
		    		 	util.getSuffix()
	    		 	));
			for(Row row: results)
				gotSpecific = row.getString("authkey");
			return (Object) gotSpecific;
		}

		else if(address instanceof Ipv6){
			LispAddressParseUtil util= new LispAddressParseUtil((Ipv6) address);
			ResultSet results = session.execute(getIpv6KeyStatement.bind(
		    		 	util.getIpv6prefix(),
		    		 	util.getIpv6suffix()
	    		 	));
			for(Row row: results)
				gotSpecific = row.getString("authkey");
			return (Object) gotSpecific;
			}

		else if(address instanceof Mac){
			LispAddressParseUtil util= new LispAddressParseUtil((Mac) address);
			ResultSet results = session.execute(getMacKeyStatement.bind(
		    		 	util.getMacEid()
	    		 	));
			for(Row row: results)
				gotSpecific = row.getString("authkey");
			return (Object) gotSpecific;
			}

		return null;
	}

	private Object getMapping(Address address, int mask){
		return getMapping(address, mask, false);
	}

	private Object getMapping(Address address, int mask, boolean lpm){
		UDTValue rlocGroupValue=null;

		if(address instanceof Ipv4){
		    LispAddressParseUtil util= new LispAddressParseUtil((Ipv4) address);
			ResultSet results = session.execute(getIpv4MappingStatement.bind(
		    		 	util.getPrefix(),
		    		 	util.getSuffix()
	    		 	));
			for(Row row: results)
				rlocGroupValue= row.getUDTValue("address");
			if(rlocGroupValue!=null){
				MappingServiceRLOCGroup mapping= UdtMappingServiceRlocGroupMapper.getMappingServiceRlocGroup(rlocGroupValue);
				return (Object) mapping;
			}
			else if(rlocGroupValue==null && lpm==false){
				getMapping(address, mask, true);
			}
			return null;
		}

		else if(address instanceof Ipv6){
			LispAddressParseUtil util= new LispAddressParseUtil((Ipv6) address);
			ResultSet results = session.execute(getIpv6MappingStatement.bind(
		    		 	util.getIpv6prefix(),
		    		 	util.getIpv6suffix()
	    		 	));
			for(Row row: results)
				rlocGroupValue = row.getUDTValue("address");
			if(rlocGroupValue!=null){
				MappingServiceRLOCGroup mapping= UdtMappingServiceRlocGroupMapper.getMappingServiceRlocGroup(rlocGroupValue);
				return (Object) mapping;
			}
			else if(rlocGroupValue==null && lpm==false){
				getMapping(address, mask, true);
			}
			return null;
		}

		else if(address instanceof Mac){
			LispAddressParseUtil util= new LispAddressParseUtil((Mac) address);
			ResultSet results = session.execute(getMacMappingStatement.bind(
		    		 	util.getMacEid()
	    		 	));
			for(Row row: results)
				rlocGroupValue = row.getUDTValue("address");
			if(rlocGroupValue!=null){
				MappingServiceRLOCGroup mapping= UdtMappingServiceRlocGroupMapper.getMappingServiceRlocGroup(rlocGroupValue);
				return (Object) mapping;
			}
			else if(rlocGroupValue==null && lpm==false){
				getMapping(address, mask, true);
			}
			return null;
		}

		return null;
	}

	@Override
	public Map<String, Object> get(Object key) {
		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

		Map<String, Object> output= new HashMap<String, Object>();
		output.put(PASSWORD_SUBKEY,getAuthenticationKey(address, mask));
		output.put(ADDRESS_SUBKEY, getMapping(address, mask));

		return output;
	}

	@Override
	public void getAll(IRowVisitor visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Object key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub
	}

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setRecordTimeOut(int recordTimeOut) {
        this.recordTimeOut = recordTimeOut;
    }

    public int getRecordTimeOut() {
        return recordTimeOut;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void close() throws Exception {
    }

}
