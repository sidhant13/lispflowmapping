/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.cassandradb.util;

import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv6;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Mac;

public class LispAddressParseUtil {

	int prefix;
	int suffix;
	int afi;
	String hexprefix;
	String hexsuffix;
	long ipv6prefix;
	long ipv6suffix;
	String macEid;

	public LispAddressParseUtil(Ipv6 address) {

		afi= address.getIpv6Address().getAfi();
		String stringIp= address.getIpv6Address().getIpv6Address().getValue();
		parseIpv6Integer(stringIp);
	}

	public LispAddressParseUtil(Ipv4 address) {

		afi= address.getIpv4Address().getAfi();
		String stringIp= address.getIpv4Address().getIpv4Address().getValue();
		parseIpv4Integer(stringIp);
	}

	public LispAddressParseUtil(Mac address) {

		afi= address.getMacAddress().getAfi();
		macEid= address.getMacAddress().getMacAddress().getValue();
	}

	private void parseIpv4Integer(String stringIp) {

		int indexOne= stringIp.indexOf('.');
		int indexTwo= stringIp.indexOf('.', indexOne+1);

		prefix = Integer.valueOf(stringIp.substring(0 ,indexOne));
		prefix *= 256;
		prefix += Integer.valueOf(stringIp.substring(indexOne+1 ,indexTwo));

		indexOne= indexTwo;
		indexTwo= stringIp.indexOf('.', indexTwo+1);
		suffix = Integer.valueOf(stringIp.substring(indexOne+1 ,indexTwo));
		suffix *= 256;
		indexOne= indexTwo;
		suffix += Integer.valueOf(stringIp.substring(indexOne+1));

		//System.out.println(stringIp + " -> " + prefix + "," + suffix);
	}

	private void parseIpv6Integer(String stringIp) {

		int indexOne= stringIp.indexOf(':');
		hexprefix = stringIp.substring(0 ,indexOne);
		int indexTwo= stringIp.indexOf(':', indexOne+1);
		int itr=1;

		while(indexTwo>0){
			hexprefix+=(stringIp.substring(indexOne+1 ,indexTwo));
			indexOne= indexTwo;
			indexTwo= stringIp.indexOf(':', indexTwo+1);
			itr++;
			if(itr==4)
				break;
		}
		if(indexTwo<0)
			hexprefix+=(stringIp.substring(indexOne+1));

		hexsuffix="";
		while(indexTwo>0){
			hexsuffix+=(stringIp.substring(indexOne+1 ,indexTwo));
			indexOne= indexTwo;
			indexTwo= stringIp.indexOf(':', indexTwo+1);
			itr++;
			if(itr==4)
				break;
		}
		if(indexTwo<0)
			hexsuffix+=(stringIp.substring(indexOne+1));

		ipv6prefix=Long.parseLong(hexprefix,16);
		ipv6suffix=Long.parseLong(hexsuffix,16);

		//System.out.println(stringIp + " -> " + ipv6prefix + "," + ipv6suffix);

	}

	public int getPrefix() {
		return prefix;
	}

	public int getSuffix() {
		return suffix;
	}

	public int getAfi() {
		return afi;
	}

	public long getIpv6prefix() {
		return ipv6prefix;
	}

	public long getIpv6suffix() {
		return ipv6suffix;
	}

	public String getMacEid() {
		return macEid;
	}




}
