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
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.alliance.transformer.nitf.common.TreUtility;
import org.codice.imaging.nitf.core.tre.TreGroup;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.types.CoreAttributes;
import ddf.catalog.data.types.Core;

public enum IndexedMtirpbAttribute implements NitfAttribute<TreGroup> {

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.mtirpb.*` when
     * appropriate
     */

    INDEXED_TARGET_LOCATION(Core.LOCATION,
            "TGT_LOC",
            tre -> TreUtility.getTreValue(tre, "TGT_LOC"),
            new CoreAttributes().getAttributeDescriptor(Core.LOCATION),
            "targetLocation"),

    /*
     * Non-normalized attributes
     */

    INDEXED_TARGET_CLASSIFICATION_CATEGORY("targetClassificationCategory",
            "TGT_CAT",
            tre -> getClassificationCategory(tre)),
    INDEXED_TARGET_AMPLITUDE("targetAmplitude",
            "TGT_AMPLITUDE",
            tre -> TreUtility.getTreValue(tre, "TGT_AMPLITUDE")),
    INDEXED_TARGET_HEADING("targetHeading",
            "TGT_HEADING",
            tre -> TreUtility.getTreValue(tre, "TGT_HEADING")),
    INDEXED_TARGET_GROUND_SPEED("targetGroundSpeed",
            "TGT_SPEED",
            tre -> TreUtility.getTreValue(tre, "TGT_SPEED")),
    INDEXED_TARGET_RADIAL_VELOCITY("targetRadialVelocity",
            "TGT_VEL_R",
            tre -> TreUtility.getTreValue(tre, "TGT_VEL_R")),
    INDEXED_TARGET_LOCATION_ACCURACY("targetLocationAccuracy",
            "TGT_LOC_ACCY",
            tre -> TreUtility.getTreValue(tre, "TGT_LOC_ACCY"));

    private static final String ATTRIBUTE_NAME_PREFIX = "mtirpb.";

    private String shortName;

    private String longName;

    private Function<TreGroup, Serializable> accessorFunction;

    private Set<AttributeDescriptor> attributeDescriptors;

    IndexedMtirpbAttribute(String longName, String shortName,
            Function<TreGroup, Serializable> accessorFunction) {
        this.longName = longName;
        this.shortName = shortName;
        this.accessorFunction = accessorFunction;
        // retrieving metacard attribute descriptors for this attribute to prevent later lookups
        this.attributeDescriptors = new HashSet<>();
        this.attributeDescriptors.add(new AttributeDescriptorImpl(
                ExtNitfUtility.EXT_NITF_PREFIX + ATTRIBUTE_NAME_PREFIX + longName,
                true, /* indexed */
                true, /* stored */
                false, /* tokenized */
                true, /* multivalued */
                BasicTypes.STRING_TYPE));
    }

    IndexedMtirpbAttribute(String longName, String shortName,
            Function<TreGroup, Serializable> accessorFunction,
            AttributeDescriptor attributeDescriptor, String extNitfName) {
        this.longName = longName;
        this.shortName = shortName;
        this.accessorFunction = accessorFunction;
        // retrieving metacard attribute descriptors for this attribute to prevent later lookups
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
    public Function<TreGroup, Serializable> getAccessorFunction() {
        return this.accessorFunction;
    }

    @Override
    public Set<AttributeDescriptor> getAttributeDescriptors() {
        return this.attributeDescriptors;
    }

    private static String getClassificationCategory(TreGroup treGroup) {
        Serializable value = TreUtility.getTreValue(treGroup,
                INDEXED_TARGET_CLASSIFICATION_CATEGORY.getShortName());

        if (value == null) {
            return MtiTargetClassificationCategory.U.getLongName();
        }
        return MtiTargetClassificationCategory.valueOf((String) value)
                .getLongName();
    }
}
