package com.example.cassandra.lisp;

import java.net.InetAddress;

import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "test2", name = "LocatorRecord")
public class MappingUdtLocatorRecord {

	String name;

	int priority;

	int weight;

	InetAddress rloc;

	public MappingUdtLocatorRecord(String name, int priority, int weight,String rloc) {

		this.name = name;
		this.priority = priority;
		this.weight = weight;
		 try {
				this.rloc= InetAddress.getByName(rloc);
			} catch (java.net.UnknownHostException e) {
				e.printStackTrace();
			}
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

	public InetAddress getRloc() {
		return rloc;
	}

	public void setRloc(InetAddress rloc) {
		this.rloc = rloc;
	}




}
