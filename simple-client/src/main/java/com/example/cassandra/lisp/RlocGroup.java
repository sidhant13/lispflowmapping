package com.example.cassandra.lisp;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "test2", name = "RlocGroup")
public class RlocGroup {

	int ttl;

	boolean authoritative;

	Date registeredDate;

	@Frozen
	MappingUdtLocatorRecord locatorRecords;

	public RlocGroup(int ttl, boolean authoritative, Date registeredDate,
			MappingUdtLocatorRecord locatorRecords) {

		this.ttl = ttl;
		this.authoritative = authoritative;
		this.registeredDate = registeredDate;
		this.locatorRecords = locatorRecords;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public boolean isAuthoritative() {
		return authoritative;
	}

	public void setAuthoritative(boolean authoritative) {
		this.authoritative = authoritative;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public MappingUdtLocatorRecord getLocatorRecords() {
		return locatorRecords;
	}

	public void setLocatorRecords(MappingUdtLocatorRecord locatorRecords) {
		this.locatorRecords = locatorRecords;
	}



}
