package com.example.cassandra.lisp;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "test2", name = "mapping")
public class MappingTable {

	String password;

    @PartitionKey@Frozen
    @Column( name = "map_key" )
	MappingServiceKey mapkey;

    @Frozen
	RlocGroup address;

	public MappingTable(String password, MappingServiceKey mapkey) {
		this.password = password;
		this.mapkey = mapkey;
	}

	public MappingTable(String password, MappingServiceKey mapkey,
			RlocGroup address) {

		this.password = password;
		this.mapkey = mapkey;
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MappingServiceKey getMapkey() {
		return mapkey;
	}

	public void setMapkey(MappingServiceKey mapkey) {
		this.mapkey = mapkey;
	}

	public RlocGroup getAddress() {
		return address;
	}

	public void setAddress(RlocGroup address) {
		this.address = address;
	}




}
