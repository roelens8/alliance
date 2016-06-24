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
package org.codice.alliance.transformer.nitf.common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.codice.alliance.catalog.core.api.impl.types.IsrAttributes;
import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.imaging.nitf.core.tre.Tre;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;

public enum AcftbAttribute implements NitfAttribute<Tre> {

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.acftb.*` when
     * appropriate
     */

    AIRCRAFT_MISSION_ID(Isr.MISSION_ID,
            "AC_MSN_ID",
            tre -> TreUtility.getTreValue(tre, "AC_MSN_ID"),
            new IsrAttributes().getAttributeDescriptor(Isr.MISSION_ID),
            "aircraftMissionId"),
    AIRCRAFT_TAIL_NUMBER(Isr.PLATFORM_ID,
            "AC_TAIL_NO",
            tre -> TreUtility.getTreValue(tre, "AC_TAIL_NO"),
            new IsrAttributes().getAttributeDescriptor(Isr.PLATFORM_ID),
            "aircraftTailNumber"),
    SENSOR_ID_TYPE(Isr.SENSOR_TYPE,
            "SENSOR_ID_TYPE",
            tre -> TreUtility.getTreValue(tre, "SENSOR_ID_TYPE"),
            new IsrAttributes().getAttributeDescriptor(Isr.SENSOR_TYPE),
            "sensorIdType"),
    SENSOR_ID(Isr.SENSOR_ID,
            "SENSOR_ID",
            tre -> TreUtility.getTreValue(tre, "SENSOR_ID"),
            new IsrAttributes().getAttributeDescriptor(Isr.SENSOR_ID),
            "sensorId"),

    /*
     * Non-normalized attributes
     */

    AIRCRAFT_TAKEOFF("aircraftTakeOff", "AC_TO", tre -> TreUtility.getTreValue(tre, "AC_TO")),
    SCENE_SOURCE("sceneSource",
            "SCENE_SOURCE",
            tre -> TreUtility.getTreValue(tre, "SCENE_SOURCE")),
    SCENE_NUMBER("sceneNumber", "SCNUM", tre -> TreUtility.getTreValue(tre, "SCNUM")),
    PROCESSING_DATE("processingDate", "PDATE", tre -> TreUtility.getTreValue(tre, "PDATE")),
    IMMEDIATE_SCENE_HOST("immediateSceneHost",
            "IMHOSTNO",
            tre -> TreUtility.getTreValue(tre, "IMHOSTNO")),
    IMMEDIATE_SCENE_REQUEST_ID("immediateSceneRequestId",
            "IMREQID",
            tre -> TreUtility.getTreValue(tre, "IMREQID")),
    MISSION_PLAN_MODE("missionPlanMode", "MPLAN", tre -> TreUtility.getTreValue(tre, "MPLAN"));

    private static final String ATTRIBUTE_NAME_PREFIX = "acftb.";

    private String shortName;

    private String longName;

    private Function<Tre, Serializable> accessorFunction;

    private Set<AttributeDescriptor> attributeDescriptors;

    AcftbAttribute(final String longName, final String shortName,
            final Function<Tre, Serializable> accessorFunction) {
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

    AcftbAttribute(String longName, String shortName, Function<Tre, Serializable> accessorFunction,
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
    public Function<Tre, Serializable> getAccessorFunction() {
        return this.accessorFunction;
    }

    @Override
    public Set<AttributeDescriptor> getAttributeDescriptors() {
        return this.attributeDescriptors;
    }
}
