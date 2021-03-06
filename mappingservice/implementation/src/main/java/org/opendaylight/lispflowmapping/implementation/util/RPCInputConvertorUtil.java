/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.lispflowmapping.implementation.util;

import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.AddKeyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.AddMappingInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.EidUri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.MappingOrigin;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.RemoveKeyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.RemoveMappingInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.UpdateKeyInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.UpdateMappingInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.db.instance.AuthenticationKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.db.instance.AuthenticationKeyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.db.instance.Mapping;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.mapping.database.rev150314.db.instance.MappingBuilder;

/**
 * Converts RPC *Input object to other object types
 *
 * @author Lorand Jakab
 *
 */
public class RPCInputConvertorUtil {
    public static AuthenticationKey toAuthenticationKey(AddKeyInput input) {
        AuthenticationKeyBuilder akb = new AuthenticationKeyBuilder();
        akb.setEid(new EidUri(LispAddressStringifier.getURIString(
                input.getLispAddressContainer(), input.getMaskLength())));
        akb.setLispAddressContainer(input.getLispAddressContainer());
        akb.setMaskLength(input.getMaskLength());
        akb.setKeyType(input.getKeyType());
        akb.setAuthkey(input.getAuthkey());
        return akb.build();
    }

    public static AuthenticationKey toAuthenticationKey(UpdateKeyInput input) {
        AuthenticationKeyBuilder akb = new AuthenticationKeyBuilder();
        akb.setEid(new EidUri(LispAddressStringifier.getURIString(
                input.getEid().getLispAddressContainer(), input.getEid().getMaskLength())));
        akb.setLispAddressContainer(input.getEid().getLispAddressContainer());
        akb.setMaskLength(input.getEid().getMaskLength());
        akb.setKeyType(input.getKey().getKeyType());
        akb.setAuthkey(input.getKey().getAuthkey());
        return akb.build();
    }

    public static AuthenticationKey toAuthenticationKey(RemoveKeyInput input) {
        AuthenticationKeyBuilder akb = new AuthenticationKeyBuilder();
        akb.setEid(new EidUri(LispAddressStringifier.getURIString(
                input.getLispAddressContainer(), input.getMaskLength())));
        akb.setLispAddressContainer(input.getLispAddressContainer());
        akb.setMaskLength(input.getMaskLength());
        return akb.build();
    }

    public static Mapping toMapping(AddMappingInput input) {
        MappingBuilder mb = new MappingBuilder();
        mb.setEid(new EidUri(LispAddressStringifier.getURIString(
                input.getLispAddressContainer(), input.getMaskLength())));
        mb.setOrigin(MappingOrigin.Northbound);
        mb.setRecordTtl(input.getRecordTtl());
        mb.setMaskLength(input.getMaskLength());
        mb.setMapVersion(input.getMapVersion());
        mb.setAction(input.getAction());
        mb.setAuthoritative(input.isAuthoritative());
        mb.setLispAddressContainer(input.getLispAddressContainer());
        mb.setLocatorRecord(input.getLocatorRecord());
        return mb.build();
    }

    public static Mapping toMapping(UpdateMappingInput input) {
        MappingBuilder mb = new MappingBuilder();
        mb.setEid(new EidUri(LispAddressStringifier.getURIString(
                input.getLispAddressContainer(), input.getMaskLength())));
        mb.setOrigin(MappingOrigin.Northbound);
        mb.setRecordTtl(input.getRecordTtl());
        mb.setMaskLength(input.getMaskLength());
        mb.setMapVersion(input.getMapVersion());
        mb.setAction(input.getAction());
        mb.setAuthoritative(input.isAuthoritative());
        mb.setLispAddressContainer(input.getLispAddressContainer());
        mb.setLocatorRecord(input.getLocatorRecord());
        return mb.build();
    }

    public static Mapping toMapping(RemoveMappingInput input) {
        MappingBuilder mb = new MappingBuilder();
        mb.setEid(new EidUri(LispAddressStringifier.getURIString(
                input.getLispAddressContainer(), input.getMaskLength())));
        mb.setOrigin(MappingOrigin.Northbound);
        mb.setMaskLength(input.getMaskLength());
        mb.setLispAddressContainer(input.getLispAddressContainer());
        return mb.build();
    }
}
