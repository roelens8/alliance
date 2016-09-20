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
import java.util.MissingResourceException;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.ddf.libs.location.FIPSFormatConverter;
import org.codice.imaging.nitf.core.tre.Tre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.impl.types.DateTimeAttributes;
import ddf.catalog.data.impl.types.LocationAttributes;
import ddf.catalog.data.types.DateTime;
import ddf.catalog.data.types.Location;

public enum AimidbAttribute implements NitfAttribute<Tre> {

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.acftb.*` when
     * appropriate
     */

    COUNTRY(Location.COUNTRY_CODE,
            "COUNTRY",
            tre -> normalizeCountryCode((String) TreUtility.getTreValue(tre, "COUNTRY")),
            new LocationAttributes().getAttributeDescriptor(Location.COUNTRY_CODE),
            "countryCode"),
    ACQUISITION_DATE(DateTime.START,
            "ACQUISITION_DATE",
            tre -> TreUtility.getTreValue(tre, "ACQUISITION_DATE"),
            new DateTimeAttributes().getAttributeDescriptor(DateTime.START),
            "acquisitionDate");

    private static final Logger LOGGER = LoggerFactory.getLogger(AimidbAttribute.class);

    private static final String ATTRIBUTE_NAME_PREFIX = "aimidb.";

    private String shortName;

    private String longName;

    private Function<Tre, Serializable> accessorFunction;

    private Set<AttributeDescriptor> attributeDescriptors;

    AimidbAttribute(String longName, String shortName, Function<Tre, Serializable> accessorFunction,
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

    private static String normalizeCountryCode(String fipsCountryCode) {
        String countryCode = "";
        try {
            countryCode = FIPSFormatConverter.convertFipsToIso3(fipsCountryCode);
        } catch (MissingResourceException ex) {
            LOGGER.debug("Could not normalize country code: {}.", fipsCountryCode, ex);
        }
        return countryCode;
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