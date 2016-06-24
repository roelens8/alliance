/**
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.transformer.nitf.gmti;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.codice.alliance.catalog.core.api.impl.types.IsrAttributes;
import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.alliance.transformer.nitf.common.TreUtility;
import org.codice.imaging.nitf.core.tre.Tre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;

public enum MtirpbAttribute implements NitfAttribute<Tre> {

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.mtirpb.*` when
     * appropriate
     */

    NUMBER_OF_VALID_TARGETS(Isr.TARGET_REPORT_COUNT,
            "NO_VALID_TARGETS",
            tre -> TreUtility.getTreValue(tre, "NO_VALID_TARGETS"),
            new IsrAttributes().getAttributeDescriptor(Isr.TARGET_REPORT_COUNT),
            "numberOfValidTargets"),

    /*
     * Non-normalized attributes
     */

    AIRCRAFT_LOCATION("aircraftLocation",
            "ACFT_LOC",
            tre -> TreUtility.getTreValue(tre, "ACFT_LOC")),
    AIRCRAFT_ALTITUDE("aircraftAltitude",
            "ACFT_ALT",
            tre -> TreUtility.getTreValue(tre, "ACFT_ALT")),
    AIRCRAFT_ALTITUDE_UNITS("aircraftAltitudeUnitOfMeasure",
            "ACFT_ALT_UNIT",
            tre -> TreUtility.getTreValue(tre, "ACFT_ALT_UNIT")),
    AIRCRAFT_HEADING("aircraftHeading",
            "ACFT_HEADING",
            tre -> TreUtility.getTreValue(tre, "ACFT_HEADING")),
    COSINE_OF_GRAZE_ANGLE("cosineOfGrazeAngle",
            "COSGRZ",
            tre -> TreUtility.getTreValue(tre, "COSGRZ")),
    DESTINATION_POINT("destinationPoint",
            "MTI_DP",
            tre -> TreUtility.getTreValue(tre, "MTI_DP")),
    MTI_LR("mtiLeftOrRight", "MTI_LR", tre -> TreUtility.getTreValue(tre, "MTI_LR")),
    PATCH_NUMBER("patchNumber", "PATCH_NO", tre -> TreUtility.getTreValue(tre, "PATCH_NO")),
    SCAN_DATE_AND_TIME("scanDateAndTime",
            "DATIME",
            tre -> TreUtility.getTreValue(tre, "DATIME")),
    SQUINT_ANGLE("squintAngle",
            "SQUINT_ANGLE",
            tre -> TreUtility.getTreValue(tre, "SQUINT_ANGLE")),
    WIDE_AREA_MTI_FRAME_NUMBER("wideAreaMtiFrameNumber",
            "WAMTI_FRAME_NO",
            tre -> TreUtility.getTreValue(tre, "WAMTI_FRAME_NO")),
    WIDE_AREA_MTI_BAR_NUMBER("wideAreaMtiBarNumber",
            "WAMTI_BAR_NO",
            tre -> TreUtility.getTreValue(tre, "WAMTI_BAR_NO"));

    private static final Logger LOGGER = LoggerFactory.getLogger(MtirpbAttribute.class);

    private static final String ATTRIBUTE_NAME_PREFIX = "mtirpb.";

    private String shortName;

    private String longName;

    private Function<Tre, Serializable> accessorFunction;

    private Set<AttributeDescriptor> attributeDescriptors;

    MtirpbAttribute(String longName, String shortName,
            Function<Tre, Serializable> accessorFunction) {
        this.longName = longName;
        this.shortName = shortName;
        this.accessorFunction = accessorFunction;
        // retrieving metacard attribute descriptor for this attribute to prevent later lookups
        this.attributeDescriptors = new HashSet<>();
        this.attributeDescriptors.add(new AttributeDescriptorImpl(
                ExtNitfUtility.EXT_NITF_PREFIX + ATTRIBUTE_NAME_PREFIX + longName,
                true, /* indexed */
                true, /* stored */
                false, /* tokenized */
                true, /* multivalued */
                BasicTypes.STRING_TYPE));
    }

    MtirpbAttribute(String longName, String shortName, Function<Tre, Serializable> accessorFunction,
            AttributeDescriptor attributeDescriptor, String extNitfName) {
        this.longName = longName;
        this.shortName = shortName;
        this.accessorFunction = accessorFunction;
        // retrieving metacard attribute descriptor for this attribute to prevent later lookups
        this.attributeDescriptors = new HashSet<>();
        this.attributeDescriptors.add(attributeDescriptor);
        if (StringUtils.isNotEmpty(extNitfName)) {
            this.attributeDescriptors.add(ExtNitfUtility.createDuplicateDescriptorAndRename(
                    ATTRIBUTE_NAME_PREFIX + extNitfName,
                    attributeDescriptor));
        }
    }

    @Override
    public String getLongName() {
        return this.longName;
    }

    @Override
    public String getShortName() {
        return this.shortName;
    }

    @Override
    public Function<Tre, Serializable> getAccessorFunction() {
        return this.accessorFunction;
    }

    @Override
    public Set<AttributeDescriptor> getAttributeDescriptors() {
        return this.attributeDescriptors;
    }
}
