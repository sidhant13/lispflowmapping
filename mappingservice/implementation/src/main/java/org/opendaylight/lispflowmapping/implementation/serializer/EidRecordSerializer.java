/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.lispflowmapping.implementation.serializer;

import java.nio.ByteBuffer;

import org.opendaylight.lispflowmapping.implementation.serializer.address.LispAddressSerializer;
import org.opendaylight.lispflowmapping.implementation.util.ByteUtil;
import org.opendaylight.lispflowmapping.implementation.util.LispAFIConvertor;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.LispAFIAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.eidrecords.EidRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.control.plane.rev150314.eidrecords.EidRecordBuilder;

public class EidRecordSerializer {

    private static final EidRecordSerializer INSTANCE = new EidRecordSerializer();

    // Private constructor prevents instantiation from other classes
    private EidRecordSerializer() {
    }

    public static EidRecordSerializer getInstance() {
        return INSTANCE;
    }

    public EidRecord deserialize(ByteBuffer requestBuffer) {
        /* byte reserved = */requestBuffer.get();
        short maskLength = (short) (ByteUtil.getUnsignedByte(requestBuffer));
        LispAFIAddress prefix = LispAddressSerializer.getInstance().deserialize(requestBuffer);
        return new EidRecordBuilder().setLispAddressContainer(LispAFIConvertor.toContainer(prefix))
                .setMask(maskLength).build();
    }
}
