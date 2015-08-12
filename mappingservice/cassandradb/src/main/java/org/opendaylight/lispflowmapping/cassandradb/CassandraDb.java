/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opendaylight.lispflowmapping.cassandradb.mappings.LispmappingsIpv4;
import org.opendaylight.lispflowmapping.cassandradb.mappings.LispmappingsIpv6;
import org.opendaylight.lispflowmapping.cassandradb.mappings.LocatorRecordIp;
import org.opendaylight.lispflowmapping.cassandradb.mappings.LocatorRecordMac;
import org.opendaylight.lispflowmapping.cassandradb.mappings.RlocGroup;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.locatorrecords.LocatorRecord;

import org.opendaylight.lispflowmapping.interfaces.dao.IMappingServiceKey;

public class CassandraDb extends CassandraDbSetup implements ILispDAO{

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
	     System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());

		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

        RlocGroup rlocGroup = new RlocGroup();
	    if(value instanceof MappingServiceRLOCGroup){
	    	 MappingServiceRLOCGroup rValue= (MappingServiceRLOCGroup) value;
	    	 rlocGroup= rlocGroup.setAuthoritative(rValue.isAuthoritative())
	    			 .setRegistereddate(rValue.getRegisterdDate())
	    			 .setTtl(rValue.getTtl());

	    	 List<?> locators = extractLocators(rValue.getRecords());
	    	 for(Object locator : locators){
	 	        if(locator instanceof LocatorRecordIp)
	 	        	rlocGroup.setRloc_ip((LocatorRecordIp)locator);
	 	        else if(locator instanceof LocatorRecordMac)
		 	        rlocGroup.setRloc_mac((LocatorRecordMac)locator);
	 		}
	     }

		if(address instanceof Ipv4)
			putIpv4Mapping((Ipv4) address, rlocGroup, mask, value);
		else if(address instanceof Ipv6)
			putIpv6Mapping((Ipv6) address, rlocGroup, mask, value);

	}

	private void putIpv6Mapping(Ipv6 address, RlocGroup rlocGroup, int mask, Object value) {
	     System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());

	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     LispmappingsIpv6 tableEntry= new LispmappingsIpv6().setPrefix(util.getIpv6prefix())
	    		 .setSubprefix(util.getIpv6suffix())
	    		 .setMask(mask)
	    		 .setAfi(util.getAfi())
	    		 .setAddress(rlocGroup);

	     System.out.println(tableEntry.toString());
	     mapperLispMappingsv6.save(tableEntry);
	}

	private void putIpv4Mapping(Ipv4 address, RlocGroup rlocGroup, int mask, Object value) {
	     System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());

	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     LispmappingsIpv4 tableEntry= new LispmappingsIpv4().setPrefix(util.getPrefix())
	    		 .setSubprefix(util.getSuffix())
	    		 .setMask(mask)
	    		 .setAfi(util.getAfi())
	    		 .setAddress(rlocGroup);
	     System.out.println(tableEntry.toString());
	     mapperLispMappingsv4.save(tableEntry);
	}

	private List<?> extractLocators(List<LocatorRecord> records) {
		List<Object> locators = new ArrayList<>();
		for(LocatorRecord value : records){
	        Address address = value.getLispAddressContainer().getAddress();

	        if((address instanceof Ipv4) || (address instanceof Ipv6))
	        	locators.add(new LocatorRecordIp(value));
	        if((address instanceof Mac))
	        	locators.add(new LocatorRecordMac(value));
		}
		return locators;
	}



	private void putAuthenticationKey(Object key, Object value) {

		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

		if(address instanceof Ipv4)
			putIpv4AuthenticationKey((Ipv4) address, mask, value);
		else if(address instanceof Ipv6)
			putIpv6AuthenticationKey((Ipv6) address, mask, value);

	}

	private void putIpv4AuthenticationKey(Ipv4 address,int mask, Object value) {
	     System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());

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
	     System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());

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

	}

	private void removeAuthenticationKey(Object key) {

		IMappingServiceKey explicitKey = ((IMappingServiceKey) key);
        Address address = explicitKey.getEID().getAddress();
        int mask= explicitKey.getMask();

		if(address instanceof Ipv4)
			removeIpv4AuthenticationKey((Ipv4) address, mask);
		else if(address instanceof Ipv6)
			removeIpv6AuthenticationKey((Ipv6) address, mask);
	}

	private void removeIpv6AuthenticationKey(Ipv6 address, int mask) {
	     System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());

	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteIpv6KeyStatement.bind(
	    		 	util.getIpv6prefix(),
	    		 	util.getIpv6suffix()
	    		 ));
	}

	private void removeIpv4AuthenticationKey(Ipv4 address, int mask) {
	     System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());

	     LispAddressParseUtil util= new LispAddressParseUtil(address);
	     session.execute(deleteIpv4KeyStatement.bind(
	    		 	util.getPrefix(),
	    		 	util.getSuffix()
	    		 ));
	}

	@Override
	public Object getSpecific(Object key, String valueKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> get(Object key) {
		// TODO Auto-generated method stub
		return null;
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




}
