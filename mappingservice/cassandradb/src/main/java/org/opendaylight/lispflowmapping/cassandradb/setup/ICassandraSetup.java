/*
 * Author: Sidhant Hasija
 * Project: Lisp DB Intern Project
 *
 */
package org.opendaylight.lispflowmapping.cassandradb.setup;

public interface ICassandraSetup {

		public void connect(String node);

		public void connect();

		public void setup();

}

