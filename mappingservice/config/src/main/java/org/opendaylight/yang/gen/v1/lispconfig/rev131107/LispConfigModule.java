package org.opendaylight.yang.gen.v1.lispconfig.rev131107;
/**
* Actual state of lisp configuration.
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LispConfigModule extends org.opendaylight.yang.gen.v1.lispconfig.rev131107.AbstractLispConfigModule {
	private static String methodname;
    protected static final Logger LOG = LoggerFactory.getLogger(LispConfigModule.class);

    public LispConfigModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    	methodname = Thread.currentThread().getStackTrace()[1].getMethodName();LOG.info("methodname- " + methodname);

    }

    public LispConfigModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.lispconfig.rev131107.LispConfigModule oldModule, java.lang.AutoCloseable oldInstance) {
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
