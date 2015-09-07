/*
 * Author: Sidhant Hasija
 * Project: Lisp DB Intern Project
 *
 * 1. Convert the UDT value received from Cassandra into java object rloc_group
 */

package org.opendaylight.lispflowmapping.cassandradb.mappings;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.opendaylight.lispflowmapping.interfaces.dao.MappingServiceRLOCGroup;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.LispAddressContainer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.LispAddressContainerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.ipv4.Ipv4AddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.ipv6.Ipv6AddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.mac.MacAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.locatorrecords.LocatorRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.locatorrecords.LocatorRecordBuilder;
import com.datastax.driver.core.UDTValue;


public class UdtMappingServiceRlocGroupMapper extends AbstractJavaUdtMapper{


	public static MappingServiceRLOCGroup getMappingServiceRlocGroup(UDTValue address){

		if(address==null){
			return null;
		}
		MappingServiceRLOCGroup returnValue= new MappingServiceRLOCGroup(address.getInt("ttl"),
				null, address.getBool("authoritative"));
		returnValue.setRegisterdDate(address.getDate("registereddate"));

		List<LocatorRecord> locators= new ArrayList<>();
		List<UDTValue> listUdtIp = address.getList("rloc_ip", UDTValue.class);
		List<LocatorRecord> locatorsIp= UdtLocatorMapper(listUdtIp);
		locators.addAll(locatorsIp);

		List<UDTValue> listUdtMac = address.getList("rloc_mac", UDTValue.class);
		List<LocatorRecord> locatorsMac= UdtLocatorMapper(listUdtMac);
		locators.addAll(locatorsMac);

		returnValue.setRecords(locators);
		return returnValue;
	}

	/*
	 * convert the UDT locator_records into locator_records java object
	 */
	private static List<LocatorRecord> UdtLocatorMapper(List<UDTValue> listUdt) {

		List<LocatorRecord> locators= new ArrayList<>();
		for(UDTValue value: listUdt){
			LocatorRecord locatorRecord;
			LocatorRecordBuilder lrb= new LocatorRecordBuilder();
			lrb.setName(value.getString("name"))
				.setPriority((short)(value.getInt("priority")))
				.setWeight((short)value.getInt("weight"))
				.setMulticastPriority((short)value.getInt("multicastpriority"))
				.setMulticastWeight((short)value.getInt("multicastweight"));

			if(value.getType().toString().equals(userTypeStringifier(CUSTOM_LOCATOR_IP)))
				lrb.setLispAddressContainer(ipLispAddressContainerUdtMapper(value));
			if(value.getType().toString().equals(userTypeStringifier(CUSTOM_LOCATOR_MAC)))
				lrb.setLispAddressContainer(macLispAddressContainerUdtMapper(value));
			locatorRecord= lrb.build();
			locators.add(locatorRecord);
		}
		return locators;
	}

	/*
	 * convert the mac UDT locator_records into locator_records java object
	 */
	private static LispAddressContainer macLispAddressContainerUdtMapper(UDTValue value) {

		LispAddressContainerBuilder lacb= new LispAddressContainerBuilder();
		String udtAddress= value.getString("address");
		if(udtAddress!=null){
			MacAddress macAddress= new MacAddress(udtAddress);
			MacAddressBuilder macBuilder = new MacAddressBuilder();
			macBuilder.setAfi((short)value.getInt("afi"));
			macBuilder.setMacAddress(macAddress);

			org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.MacBuilder mac=
					new org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.MacBuilder();
			mac.setMacAddress(macBuilder.build());

			lacb.setAddress(mac.build());
			LispAddressContainer lac= lacb.build();
			return lac;
		}
		return null;
	}

	/*
	 * convert the ip UDT locator_records into locator_records java object
	 */
	private static LispAddressContainer ipLispAddressContainerUdtMapper(UDTValue value) {

		LispAddressContainerBuilder lacb= new LispAddressContainerBuilder();
		InetAddress udtAddress= value.getInet("address");

		if(udtAddress instanceof Inet4Address && udtAddress!=null){
			Ipv4Address ipv4address= new Ipv4Address(udtAddress.toString().substring(1));
			Ipv4AddressBuilder ipv4Builder = new Ipv4AddressBuilder();
			ipv4Builder.setAfi((short)value.getInt("afi"));
			ipv4Builder.setIpv4Address(ipv4address);

			org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv4Builder ipv4=
					new org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv4Builder();
			ipv4.setIpv4Address(ipv4Builder.build());

			lacb.setAddress(ipv4.build());
			LispAddressContainer lac= lacb.build();
			return lac;
		}
		else if (udtAddress instanceof Inet6Address && udtAddress!=null){

			Ipv6Address ipv6address= new Ipv6Address(udtAddress.toString().substring(1));
			Ipv6AddressBuilder ipv6Builder = new Ipv6AddressBuilder();
			ipv6Builder.setAfi((short)value.getInt("afi"));
			ipv6Builder.setIpv6Address(ipv6address);

			org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv6Builder ipv6=
					new org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.lispaddress.lispaddresscontainer.address.Ipv6Builder();
			ipv6.setIpv6Address(ipv6Builder.build());

			lacb.setAddress(ipv6.build());
			LispAddressContainer lac= lacb.build();
			return lac;
		}
		return null;
	}
}
