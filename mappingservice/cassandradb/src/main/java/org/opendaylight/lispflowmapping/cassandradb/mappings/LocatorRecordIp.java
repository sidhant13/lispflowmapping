/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.mappings;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv6;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.locatorrecords.LocatorRecord;

import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "lispdb02", name = "locatorrecord_ip")
public class LocatorRecordIp {

	String name;

	int priority;

	int weight;

	int multicastpriority;

	int multicastweight;

	boolean locallocator;

	boolean rlocprobed;

	boolean routed;

	InetAddress address;

	int afi;

	public LocatorRecordIp(LocatorRecord locator){
		name= locator.getName();
		priority= locator.getPriority();
		weight= locator.getWeight();
		multicastpriority= locator.getMulticastPriority();
		multicastweight= locator.getMulticastWeight();

		Address address = locator.getLispAddressContainer().getAddress();
		if(address instanceof Ipv4){
			this.afi =  ((Ipv4) address).getIpv4Address().getAfi();
			String stringaddress =  ((Ipv4) address).getIpv4Address().getIpv4Address().getValue();
			try {
				this.address = InetAddress.getByName(stringaddress);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		else if(address instanceof Ipv6){
			this.afi =  ((Ipv6) address).getIpv6Address().getAfi();
			String stringaddress =  ((Ipv6) address).getIpv6Address().getIpv6Address().getValue();
			try {
				this.address = InetAddress.getByName(stringaddress);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

	public  LocatorRecordIp() {
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getMulticastpriority() {
		return multicastpriority;
	}

	public void setMulticastpriority(int multicastpriority) {
		this.multicastpriority = multicastpriority;
	}

	public int getMulticastweight() {
		return multicastweight;
	}

	public void setMulticastweight(int multicastweight) {
		this.multicastweight = multicastweight;
	}

	public boolean isLocallocator() {
		return locallocator;
	}

	public void setLocallocator(boolean locallocator) {
		this.locallocator = locallocator;
	}

	public boolean isRlocprobed() {
		return rlocprobed;
	}

	public void setRlocprobed(boolean rlocprobed) {
		this.rlocprobed = rlocprobed;
	}

	public boolean isRouted() {
		return routed;
	}

	public void setRouted(boolean routed) {
		this.routed = routed;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getAfi() {
		return afi;
	}

	public void setAfi(int afi) {
		this.afi = afi;
	}

	@Override
	public String toString() {
		return "LocatorRecordIp [name=" + name + ", priority=" + priority + ", weight=" + weight
				+ ", multicastpriority=" + multicastpriority + ", multicastweight=" + multicastweight + ", locallocator=" + locallocator + ", rlocprobed="
				+ rlocprobed + ", routed=" + routed + ", afi=" + afi + ", address=" + address.toString() + "]";
	}

}
