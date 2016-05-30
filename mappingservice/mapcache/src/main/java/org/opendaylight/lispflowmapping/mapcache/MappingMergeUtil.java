/*
 * Copyright (c) 2016 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.lispflowmapping.mapcache;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opendaylight.lispflowmapping.lisp.util.LispAddressUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.inet.binary.types.rev160303.IpAddressBinary;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.XtrId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.locatorrecords.LocatorRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.mapping.record.container.MappingRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.mapping.record.container.MappingRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.rloc.container.Rloc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Utility class to implement merging of locator sets
 *
 * @author Lorand Jakab
 *
 */
public final class MappingMergeUtil {
    protected static final Logger LOG = LoggerFactory.getLogger(MappingMergeUtil.class);

    // SB Map Register validity period in milliseconds. Default is 3.3 minutes.
    // TODO Added temporarily, until we get a proper configuration infra
    public static final long MIN_REGISTRATION_VALIDITY_SB = 200000L;

    // Utility class, should not be instantiated
    private MappingMergeUtil() {
    }

    private static void mergeCommonMappingRecordFields(MappingRecordBuilder mrb, MappingRecord record) {
        // Set xTR-ID and site-ID from the current mapping, it help with determining the timestamp
        mrb.setXtrId(record.getXtrId());
        mrb.setSiteId(record.getSiteId());
        // For the TTL value we take the minimum of all records
        mrb.setRecordTtl(Math.min(mrb.getRecordTtl(), record.getRecordTtl()));
        if (!mrb.getAction().equals(record.getAction())) {
            LOG.warn("Mapping merge operation: actions are different, which one is used is undefined");
        }
        if (mrb.isAuthoritative() != record.isAuthoritative()) {
            LOG.warn("Mapping merge operation: authoritative status is different, which one is used is undefined");
        }
        if (!mrb.getEid().equals(record.getEid())) {
            LOG.warn("Mapping merge operation: EID records are different, which one is used is undefined");
        }
    }

    private static LocatorRecord mergeLocators(LocatorRecord existingLocator, LocatorRecord newLocator) {
        if (existingLocator.isLocalLocator()) {
            return existingLocator;
        }
        return newLocator;
    }

    private static int compareLocators(LocatorRecord a, LocatorRecord b) {
        byte[] aIp = LispAddressUtil.ipAddressToByteArray(a.getRloc().getAddress());
        byte[] bIp = LispAddressUtil.ipAddressToByteArray(b.getRloc().getAddress());
        return LispAddressUtil.compareIpAddressByteArrays(aIp, bIp);
    }

    private static void mergeLocatorRecords(MappingRecordBuilder mrb, MappingRecord newRecord) {
        List<LocatorRecord> locators = mrb.getLocatorRecord();

        // We assume locators are unique and sorted and don't show up several times (with different or identical
        // p/w/mp/mw), so we create a LinkedHashMap (which preserves order) of the locators from the existing merged
        // record, keyed by the Rloc
        Map<Rloc, LocatorRecord> locatorMap = new LinkedHashMap<Rloc, LocatorRecord>();

        // All locators to be added to the merge set are first stored in this list
        List<LocatorRecord> newLocatorList = new ArrayList<LocatorRecord>();

        for (LocatorRecord locator : locators) {
            locatorMap.put(locator.getRloc(), locator);
        }
        for (LocatorRecord newLocator : newRecord.getLocatorRecord()) {
            Rloc newRloc = newLocator.getRloc();
            if (locatorMap.containsKey(newRloc)) {
                // overlapping locator
                if (locatorMap.get(newRloc).equals(newLocator)) {
                    continue;
                } else {
                    LocatorRecord mergedLocator = mergeLocators(locatorMap.get(newRloc), newLocator);
                    newLocatorList.add(mergedLocator);
                }
            } else {
                // new locator
                newLocatorList.add(newLocator);
            }
        }

        // Build new merged and sorted locator set if need be
        if (newLocatorList.size() != 0) {
            List<LocatorRecord> mergedLocators = new ArrayList<LocatorRecord>();

            int mlIt = 0, lIt = 0;
            while (mlIt < newLocatorList.size() && lIt < locators.size()) {
                int cmp = compareLocators(locators.get(lIt), newLocatorList.get(mlIt));
                if (cmp < 0) {
                    mergedLocators.add(locators.get(lIt));
                    lIt++;
                } else if (cmp > 0) {
                    mergedLocators.add(newLocatorList.get(mlIt));
                    mlIt++;
                } else {
                    // when a locator appears in both lists, keep the new (merged) one and skip the old
                    mergedLocators.add(newLocatorList.get(mlIt));
                    mlIt++;
                    lIt++;
                }
            }
            while (lIt < locators.size()) {
                mergedLocators.add(locators.get(lIt));
                lIt++;
            }
            while (mlIt < newLocatorList.size()) {
                mergedLocators.add(newLocatorList.get(mlIt));
                mlIt++;
            }
            mrb.setLocatorRecord(mergedLocators);
        }
    }

    public static MappingRecord mergeMappings(MappingRecord currentMergedMapping, MappingRecord newMapping,
            XtrId xtrId, Date regdate) {
        if (currentMergedMapping == null) {
            return newMapping;
        }

        MappingRecordBuilder mrb = new MappingRecordBuilder(currentMergedMapping);
        mergeCommonMappingRecordFields(mrb, newMapping);
        mergeLocatorRecords(mrb, newMapping);

        if (xtrId != null) {
            mrb.setXtrId(xtrId);
            mrb.setTimestamp(regdate.getTime());
        }

        return mrb.build();
    }

    public static MappingRecord mergeXtrIdMappings(List<Object> records, List<XtrId> expiredMappings,
            Set<IpAddressBinary> sourceRlocs) {
        MappingRecordBuilder mrb = null;
        XtrId xtrId = null;
        Long timestamp = Long.MAX_VALUE;

        for (int i = 0; i < records.size(); i++) {
            MappingRecord record = (MappingRecord) records.get(i);

            // Skip expired mappings and add them to a list to be returned to the caller
            if (timestampIsExpired(record.getTimestamp())) {
                expiredMappings.add(record.getXtrId());
                continue;
            }

            if (mrb == null) {
                mrb = new MappingRecordBuilder((MappingRecord) records.get(i));
            }

            // Save the oldest valid timestamp
            if (record.getTimestamp() < timestamp) {
                timestamp = record.getTimestamp();
                xtrId = record.getXtrId();
            }

            // Merge record fields and locators
            mergeCommonMappingRecordFields(mrb, record);
            mergeLocatorRecords(mrb, record);

            // Save source locator for use in Map-Notify
            sourceRlocs.add(record.getSourceRloc());
        }

        if (mrb == null) {
            LOG.warn("All mappings expired when merging! Unexpected!");
            return null;
        }
        mrb.setXtrId(xtrId);
        mrb.setTimestamp(timestamp);

        return mrb.build();
    }

    public static boolean mappingIsExpired(MappingRecord mapping) {
        Preconditions.checkNotNull(mapping, "mapping should not be null!");
        if (mapping.getTimestamp() != null) {
            return timestampIsExpired(mapping.getTimestamp());
        }
        return false;
    }

    public static boolean timestampIsExpired(Date timestamp) {
        Preconditions.checkNotNull(timestamp, "timestamp should not be null!");
        return timestampIsExpired(timestamp.getTime());
    }

    public static boolean timestampIsExpired(Long timestamp) {
        Preconditions.checkNotNull(timestamp, "timestamp should not be null!");
        if ((System.currentTimeMillis() - timestamp) > MIN_REGISTRATION_VALIDITY_SB ) {
            return true;
        }
        return false;
    }
}