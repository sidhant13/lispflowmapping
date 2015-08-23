/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.mappings;

import java.util.Date;

/*import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "lispdb02", name = "rlocgroup")*/
public class RlocGroup {

	int ttl;

	boolean authoritative;

	Date registereddate;

	int action;

//	@Frozen
	LocatorRecordIp rloc_ip;

//	@Frozen
	LocatorRecordMac rloc_mac;

	public RlocGroup setTtl(int ttl) {
		this.ttl = ttl;
		return this;
	}

	public RlocGroup setAuthoritative(boolean authoritative) {
		this.authoritative = authoritative;
		return this;
	}

	public RlocGroup setRegistereddate(Date registereddate) {
		this.registereddate = registereddate;
		return this;
	}

	public RlocGroup setAction(int action) {
		this.action = action;
		return this;
	}

	public RlocGroup setRloc_ip(LocatorRecordIp rloc_ip) {
		this.rloc_ip = rloc_ip;
		return this;
	}

	public RlocGroup setRloc_mac(LocatorRecordMac rloc_mac) {
		this.rloc_mac = rloc_mac;
		return this;
	}

	public int getTtl() {
		return ttl;
	}

	public boolean isAuthoritative() {
		return authoritative;
	}

	public Date getRegistereddate() {
		return registereddate;
	}

	public int getAction() {
		return action;
	}

	public LocatorRecordIp getRloc_ip() {
		return rloc_ip;
	}

	public LocatorRecordMac getRloc_mac() {
		return rloc_mac;
	}

	@Override
	public String toString() {
		if(rloc_mac==null)
			rloc_mac=new LocatorRecordMac();
		if(rloc_ip==null)
			rloc_ip=new LocatorRecordIp();

		return "RlocGroup [ttl=" + ttl + ", authoritative=" + authoritative + ", registereddate=" + registereddate.toString()
				+ ", action=" + action + ", rloc_ip=" + rloc_ip.toString() + ", rloc_mac=" + rloc_mac.toString() + "]";
	}


}
