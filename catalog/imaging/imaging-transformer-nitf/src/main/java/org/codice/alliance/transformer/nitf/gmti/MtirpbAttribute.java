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
import java.util.function.Function;

import org.codice.alliance.catalog.core.api.impl.types.IsrAttributes;
import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.alliance.transformer.nitf.common.TreUtility;
import org.codice.imaging.nitf.core.tre.Tre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.MetacardType;

public enum MtirpbAttribute implements NitfAttribute<Tre> {
    AIRCRAFT_LOCATION(Isr.DWELL_LOCATION,
            "ACFT_LOC",
            tre -> TreUtility.getTreValue(tre, "ACFT_LOC"),
            new IsrAttributes()),
    NUMBER_OF_VALID_TARGETS(Isr.TARGET_REPORT_COUNT,
            "NO_VALID_TARGETS",
            tre -> TreUtility.getTreValue(tre, "NO_VALID_TARGETS"),
            new IsrAttributes());

    private static final Logger LOGGER = LoggerFactory.getLogger(MtirpbAttribute.class);

    private String shortName;

    private String longName;

    private Function<Tre, Serializable> accessorFunction;

    private AttributeDescriptor attributeDescriptor;

    MtirpbAttribute(String longName, String shortName, Function<Tre, Serializable> accessorFunction,
            MetacardType metacardType) {
        this.longName = longName;
        this.shortName = shortName;
        this.accessorFunction = accessorFunction;
        // retrieving metacard attribute descriptor for this attribute to prevent later lookups
        this.attributeDescriptor = metacardType.getAttributeDescriptor(longName);
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
    public AttributeDescriptor getAttributeDescriptor() {
        return this.attributeDescriptor;
    }
}
