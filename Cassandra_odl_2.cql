CREATE KEYSPACE IF NOT EXISTS lispdb02 WITH replication= {
'class':'SimpleStrategy',
'replication_factor':1
};


/*
* UDT for storing locatorrecord information
*/
CREATE TYPE IF NOT EXISTS lispdb02.locatorrecord_ip(
name text,
priority int,
weight int,
multicastpriority int,
multicastweight int,
locallocator boolean,
rlocprobed boolean,
routed boolean,
address inet, 
afi int
);

/*
* UDT for storing locatorrecord information
*/
	CREATE TYPE IF NOT EXISTS lispdb02.locatorrecord_mac(
	name text,
	priority int,
	weight int,
	multicastpriority int,
	multicastweight int,
	locallocator boolean,
	rlocprobed boolean,
	routed boolean,
	address text, 
	afi int
	);

/*
* UDT
*/
CREATE TYPE IF NOT EXISTS lispdb02.rlocgroup(
ttl int,
authoritative boolean,
registeredDate timestamp,
action int,
rloc_ip list<frozen<locatorrecord_ip>>,
rloc_mac list<frozen<locatorrecord_mac>>
);


/*
* Table for storing mappings with ipv4 type key
*/
CREATE TABLE IF NOT EXISTS lispdb02.lispmappings_ipv4
(
prefix int,
subprefix int,
mask int,
afi int,
authkey text,
address frozen<rlocgroup>,
PRIMARY KEY ((prefix),subprefix)
)
WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' }
AND clustering order by (subprefix DESC);


/*
* Table for storing mappings with ipv6 type key
*/
CREATE TABLE IF NOT EXISTS lispdb02.lispmappings_ipv6
(
prefix bigint,
subprefix bigint,
mask int,
afi int,
authkey text,
address frozen<rlocgroup>,
PRIMARY KEY ((prefix),subprefix)
) 
WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' }
AND clustering order by (subprefix DESC);


/*
* Table for storing mappings with mac_address type key
*/
CREATE TABLE IF NOT EXISTS lispdb02.lispmappings_mac
(
eid text,
mask int,
afi int,
authkey text,
address frozen<rlocgroup>,
PRIMARY KEY (eid)
) 
WITH caching = { 'keys' : 'NONE', 'rows_per_partition' : '120' }
