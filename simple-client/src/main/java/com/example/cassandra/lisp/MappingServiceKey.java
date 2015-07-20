package com.example.cassandra.lisp;


import com.datastax.driver.mapping.annotations.UDT;

import java.net.InetAddress;

@UDT(keyspace = "test2", name = "mapkey")
	public class MappingServiceKey{

		InetAddress eid;
		int mask;

		public MappingServiceKey(String eid, int mask) {
			 try {
					this.eid= InetAddress.getByName(eid);
				} catch (java.net.UnknownHostException e) {
					e.printStackTrace();
				}
			this.mask= mask;
		}

		public InetAddress getEid() {
			return eid;
		}
		public void setEid(InetAddress eid) {
			this.eid = eid;
		}
		public int getMask() {
			return mask;
		}
		public void setMask(int mask) {
			this.mask = mask;
		}

		@Override
		public String toString() {
			return "MappingServiceKey [eid=" + eid + ", mask=" + mask + "]";
		}

	}
