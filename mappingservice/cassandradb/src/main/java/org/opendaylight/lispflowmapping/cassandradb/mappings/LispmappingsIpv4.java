/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.mappings;

import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "lispdb02", name = "lispmappings_ipv4")
public class LispmappingsIpv4{

    @PartitionKey
    int prefix;
    @PartitionKey
    int subprefix;

    @Frozen
    RlocGroup address;

    int afi;

    String authkey;

    int mask;

	@Override
	public String toString() {
		return "LispmappingsIpv4 [prefix=" + prefix + ", subprefix=" + subprefix + ", address=" + address.toString() + ", afi="
				+ afi + ", authkey=" + authkey + ", mask=" + mask + "]";
	}

	public LispmappingsIpv4 setPrefix(int prefix) {
		this.prefix = prefix;
		return this;
	}

	public LispmappingsIpv4 setSubprefix(int subprefix) {
		this.subprefix = subprefix;
		return this;
	}

	public LispmappingsIpv4 setAddress(RlocGroup address) {
		this.address = address;
		return this;
	}

	public LispmappingsIpv4 setAfi(int afi) {
		this.afi = afi;
		return this;
	}

	public LispmappingsIpv4 setAuthkey(String authkey) {
		this.authkey = authkey;
		return this;
	}

	public LispmappingsIpv4 setMask(int mask) {
		this.mask = mask;
		return this;
	}

	public int getPrefix() {
		return prefix;
	}

	public int getSubprefix() {
		return subprefix;
	}

	public RlocGroup getAddress() {
		return address;
	}

	public int getAfi() {
		return afi;
	}

	public String getAuthkey() {
		return authkey;
	}

	public int getMask() {
		return mask;
	}

}

