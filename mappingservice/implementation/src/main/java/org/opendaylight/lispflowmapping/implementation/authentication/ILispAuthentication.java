package org.opendaylight.lispflowmapping.implementation.authentication;

import org.opendaylight.lispflowmapping.type.lisp.MapNotify;
import org.opendaylight.lispflowmapping.type.lisp.MapRegister;


public interface ILispAuthentication {
	public boolean validate(MapRegister mapRegister);
	public byte[] getAuthenticationData(MapNotify mapNotify);
	public int getAuthenticationLength();
	public static final int MAP_REGISTER_AND_NOTIFY_AUTHENTICATION_POSITION = 16;
	
}