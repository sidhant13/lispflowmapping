package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lispflowmapping.netconf.impl.rev140706;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LfmNetconfConnectorProviderModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lispflowmapping.netconf.impl.rev140706.AbstractLfmNetconfConnectorProviderModule {

	private static String methodname;
	private static final Logger LOG = LoggerFactory.getLogger(LfmNetconfConnectorProviderModule.class);


	public LfmNetconfConnectorProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    	methodname = Thread.currentThread().getStackTrace()[1].getMethodName();LOG.info("methodname- " + methodname);

    }

    public LfmNetconfConnectorProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lispflowmapping.netconf.impl.rev140706.LfmNetconfConnectorProviderModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    	methodname = Thread.currentThread().getStackTrace()[1].getMethodName();LOG.info("methodname- " + methodname);

    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        // TODO:implement
    	methodname = Thread.currentThread().getStackTrace()[1].getMethodName();LOG.info("methodname- " + methodname);

        throw new java.lang.UnsupportedOperationException();
    }

}
