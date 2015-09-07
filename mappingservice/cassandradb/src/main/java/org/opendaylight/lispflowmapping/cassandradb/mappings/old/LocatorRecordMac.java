/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.mappings.old;

import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Mac;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.locatorrecords.LocatorRecord;

/*import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "lispdb02", name = "locatorrecord_mac")*/
public class LocatorRecordMac {

	String name;

	int priority;

	int weight;

	int multicastpriority;

	boolean locallocator;

	boolean rlocprobed;

	boolean routed;

	String address;

	int afi;

	int multicastweight;

	public LocatorRecordMac(LocatorRecord locator){
		name= locator.getName();
		priority= locator.getPriority();
		weight= locator.getWeight();
		multicastpriority= locator.getMulticastPriority();
		multicastweight = locator.getMulticastWeight();
		Address address = locator.getLispAddressContainer().getAddress();
		this.address =  ((Mac) address).getMacAddress().getMacAddress().getValue();
		afi =  ((Mac) address).getMacAddress().getAfi();

	}

	public LocatorRecordMac() {
	}

	@Override
	public String toString() {
		return "LocatorRecordIp [name=" + name + ", priority=" + priority + ", weight=" + weight
				+ ", multicastpriority=" + multicastpriority + ", multicastweight=" + multicastweight + ", locallocator=" + locallocator + ", rlocprobed="
				+ rlocprobed + ", routed=" + routed + ", afi=" + afi + ", address=" + address + "]";
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAfi() {
		return afi;
	}

	public void setAfi(int afi) {
		this.afi = afi;
	}

	public int getMulticastweight() {
		return multicastweight;
	}

	public void setMulticastweight(int multicastweight) {
		this.multicastweight = multicastweight;
	}

}
