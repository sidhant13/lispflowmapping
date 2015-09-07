/*
 * Author: Sidhant Hasija
 * Project: Lisp DB Intern Project
 *
 * 1. Creates an Instance of CassandraDB DAO or Hashmap DAO
 */

package org.opendaylight.controller.config.yang.config.lfm.ms.dao.cassandradb;

import org.opendaylight.lispflowmapping.cassandradb.CassandraDb;
//import org.opendaylight.lispflowmapping.cassandradb.HashmapDb;

public class CassandradbModule extends org.opendaylight.controller.config.yang.config.lfm.ms.dao.cassandradb.AbstractCassandradbModule {
    public CassandradbModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public CassandradbModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.config.lfm.ms.dao.cassandradb.CassandradbModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }


    @Override
    public java.lang.AutoCloseable createInstance() {

/*		Uncomment to use the original Hashmap DAO and comment the below portion, useful in scenario where
 		you have to quickly switch to Hashmap DAO.
        final HashmapDb hashmapdb = HashmapDb.getInstance();
        hashmapdb.setRecordTimeOut(getRecordTimeout());
        return hashmapdb;*/

        final CassandraDb cassandradb = CassandraDb.getInstance();
        cassandradb.connect();
        cassandradb.setup();
        cassandradb.setRecordTimeOut(getRecordTimeout());
        return cassandradb;
    }

}
