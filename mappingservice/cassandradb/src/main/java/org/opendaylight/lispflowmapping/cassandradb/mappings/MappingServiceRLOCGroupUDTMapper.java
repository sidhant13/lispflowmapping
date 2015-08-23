/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.mappings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.lispflowmapping.interfaces.dao.MappingServiceRLOCGroup;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv6;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Mac;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.locatorrecords.LocatorRecord;

import com.datastax.driver.core.UDTValue;

public class MappingServiceRLOCGroupUDTMapper extends AbstractJavaUdtMapper {



	public static UDTValue getUdtfromRlocGroup(MappingServiceRLOCGroup value){

		UDTValue rlocgroupValue= rlocgroup.newValue();
		rlocgroupValue.setInt("ttl", value.getTtl());
		rlocgroupValue.setDate("registereddate", value.getRegisterdDate());
		rlocgroupValue.setBool("authoritative", value.isAuthoritative());

		Map<String, List<UDTValue>> locators= extractLocators(value.getRecords());

		for(String key : locators.keySet()){
			if(key.equals(CUSTOM_LOCATOR_IP))
				rlocgroupValue.setList("rloc_ip", locators.get(CUSTOM_LOCATOR_IP));
			else if(key.equals(CUSTOM_LOCATOR_MAC))
				rlocgroupValue.setList("rloc_mac", locators.get(CUSTOM_LOCATOR_MAC));
		}
		//System.out.println(rlocgroupValue.toString());
		return rlocgroupValue;
	}


	private static Map<String, List<UDTValue>> extractLocators(List<LocatorRecord> records) {

		Map<String, List<UDTValue>> locators = new HashMap<String,List<UDTValue>>();
    	List<UDTValue> ipv4List= new ArrayList<UDTValue>();
    	List<UDTValue> macList= new ArrayList<UDTValue>();

		for(LocatorRecord value : records){
	        Address address = value.getLispAddressContainer().getAddress();

	        if((address instanceof Ipv4) || (address instanceof Ipv6))
	        	ipv4List.add(locatorToUdtIp(value));
	        else if((address instanceof Mac))
	        	macList.add(locatorToUdtMac(value));
		}
    	locators.put(CUSTOM_LOCATOR_IP, ipv4List);
    	locators.put(CUSTOM_LOCATOR_MAC, macList);
		return locators;
	}

	private static UDTValue locatorToUdtMac(LocatorRecord locator) {

		UDTValue locatorrecordMacValue= locatorrecordMac.newValue();
		locatorrecordMacValue.setString("name", locator.getName())
							.setInt("priority", locator.getPriority())
							.setInt("weight", locator.getWeight())
							.setInt("multicastpriority", locator.getMulticastPriority())
							.setInt("multicastweight",locator.getMulticastWeight());

		Address address = locator.getLispAddressContainer().getAddress();
		String macAddress =  ((Mac) address).getMacAddress().getMacAddress().getValue();
		int afi =  ((Mac) address).getMacAddress().getAfi();

		locatorrecordMacValue.setString("address",macAddress)
							.setInt("afi", afi);
		return locatorrecordMacValue;
	}

	private static UDTValue locatorToUdtIp(LocatorRecord locator) {

		int afi = 0;
		String ipStringAddress;
		InetAddress ipAddress = null;

		UDTValue locatorrecordIpValue= locatorrecordIp.newValue();
		locatorrecordIpValue.setString("name", locator.getName())
							.setInt("priority", locator.getPriority())
							.setInt("weight", locator.getWeight())
							.setInt("multicastpriority", locator.getMulticastPriority())
							.setInt("multicastweight",locator.getMulticastWeight());

		Address address = locator.getLispAddressContainer().getAddress();
		if(address instanceof Ipv4){
			afi =  ((Ipv4) address).getIpv4Address().getAfi();
			ipStringAddress = ((Ipv4) address).getIpv4Address().getIpv4Address().getValue();
			try {
				ipAddress = InetAddress.getByName(ipStringAddress);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		else if(address instanceof Ipv6){
			afi =  ((Ipv6) address).getIpv6Address().getAfi();
			ipStringAddress = ((Ipv6) address).getIpv6Address().getIpv6Address().getValue();
			try {
				ipAddress = InetAddress.getByName(ipStringAddress);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		locatorrecordIpValue.setInet("address",ipAddress)
							.setInt("afi", afi);
		return locatorrecordIpValue;
	}
}
