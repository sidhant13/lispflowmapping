package com.example.cassandra.lisp;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.cassandra.dao.CassandraDb;

public class MapServerEmulator {

	CassandraDb dao;

	public MapServerEmulator(CassandraDb dao){
		this.dao=dao;
	}

	public void putMappingKey(String eid, int mask){

		Map<String, String> value_map= new HashMap<String, String>();
		//value_map.put("asd","password");
		//dao.put(new MappingServiceKey(eid, mask), value_map);


		MappingUdtLocatorRecord locatorRecord=  new MappingUdtLocatorRecord("isp1", 98, 99, "10.10.10.10");
		RlocGroup rlocGroup= new RlocGroup(999, false, new Date(), locatorRecord);
		MappingTable mapping_entry= new MappingTable("passwroda123", new MappingServiceKey(eid, mask), rlocGroup);

		dao.put(mapping_entry);
	}
}
