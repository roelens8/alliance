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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.codice.alliance.catalog.core.api.impl.types.IsrAttributes;
import org.codice.alliance.catalog.core.api.impl.types.SecurityAttributes;
import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.catalog.core.api.types.Security;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.header.NitfHeader;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.types.ContactAttributes;
import ddf.catalog.data.impl.types.CoreAttributes;
import ddf.catalog.data.impl.types.MediaAttributes;
import ddf.catalog.data.types.Contact;
import ddf.catalog.data.types.Core;
import ddf.catalog.data.types.Media;

/**
 * NitfAttributes to represent the properties of a NitfFileHeader.
 */
public enum NitfHeaderAttribute implements NitfAttribute<NitfHeader> {

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.*` when
     * appropriate
     */

    FILE_PROFILE_NAME(Media.FORMAT,
            "FHDR",
            header -> header.getFileType()
                    .name(),
            new MediaAttributes().getAttributeDescriptor(Media.FORMAT),
            "fileProfileName"),
    FILE_VERSION(Media.FORMAT_VERSION,
            "FVER",
            header -> header.getFileType()
                    .name(),
            new MediaAttributes().getAttributeDescriptor(Media.FORMAT),
            "fileVersion"),
    ORIGINATING_STATION_ID(Isr.ORGANIZATIONAL_UNIT,
            "OSTAID",
            NitfHeader::getOriginatingStationId,
            new IsrAttributes().getAttributeDescriptor(Isr.ORGANIZATIONAL_UNIT),
            "originatingStationId"),
    FILE_TITLE(Core.TITLE,
            "FTITLE",
            NitfHeader::getFileTitle,
            new CoreAttributes().getAttributeDescriptor(Core.TITLE),
            "fileTitle"),
    FILE_DATE_AND_TIME_CREATED(Core.CREATED,
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            new CoreAttributes().getAttributeDescriptor(Core.CREATED),
            "fileDateAndTime"),
    FILE_DATE_AND_TIME_MODIFIED(Core.MODIFIED,
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            new CoreAttributes().getAttributeDescriptor(Core.MODIFIED),
            ""),
    FILE_DATE_AND_TIME_EFFECTIVE(Metacard.EFFECTIVE,
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            new AttributeDescriptorImpl(Metacard.EFFECTIVE, true, /* indexed */
                    true, /* stored */
                    false, /* tokenized */
                    false, /* multivalued */
                    BasicTypes.DATE_TYPE),
            ""),
    FILE_SECURITY_CLASSIFICATION(Security.CLASSIFICATION,
            "FSCLAS",
            header -> header.getFileSecurityMetadata()
                    .getSecurityClassification()
                    .name(),
            new SecurityAttributes().getAttributeDescriptor(Security.CLASSIFICATION),
            "fileSecurityClassification"),
    FILE_CLASSIFICATION_SECURITY_SYSTEM(Security.CLASSIFICATION_SYSTEM,
            "FSCLSY",
            header -> header.getFileSecurityMetadata()
                    .getSecurityClassificationSystem(),
            new SecurityAttributes().getAttributeDescriptor(Security.CLASSIFICATION_SYSTEM),
            "fileClassificationSecuritySystem"),
    FILE_CODE_WORDS(Security.CODEWORDS,
            "FSCODE",
            header -> header.getFileSecurityMetadata()
                    .getCodewords(),
            new SecurityAttributes().getAttributeDescriptor(Security.CODEWORDS),
            "fileCodewords"),
    FILE_CONTROL_AND_HANDLING(Security.DISSEMINATION_CONTROLS,
            "FSCTLH",
            header -> header.getFileSecurityMetadata()
                    .getControlAndHandling(),
            new SecurityAttributes().getAttributeDescriptor(Security.DISSEMINATION_CONTROLS),
            "fileControlAndHandling"),
    FILE_RELEASING_INSTRUCTIONS(Security.RELEASABILITY,
            "FSREL",
            header -> header.getFileSecurityMetadata()
                    .getReleaseInstructions(),
            new SecurityAttributes().getAttributeDescriptor(Security.RELEASABILITY),
            "fileReleasingInstructions"),
    ORIGINATORS_NAME(Contact.CREATOR_NAME,
            "ONAME",
            NitfHeader::getOriginatorsName,
            new ContactAttributes().getAttributeDescriptor(Contact.CREATOR_NAME),
            "originatorsName"),
    ORIGINATORS_PHONE_NUMBER(Contact.CREATOR_PHONE,
            "OPHONE",
            NitfHeader::getOriginatorsPhoneNumber,
            new ContactAttributes().getAttributeDescriptor(Contact.CREATOR_PHONE),
            "originatorsPhoneNumber"),

    /*
     * Non normalized attributes
     */

    COMPLEXITY_LEVEL("complexityLevel",
            "CLEVEL",
            NitfHeader::getComplexityLevel,
            BasicTypes.INTEGER_TYPE),
    FILE_DATE_AND_TIME("fileDateAndTime",
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            BasicTypes.DATE_TYPE),
    STANDARD_TYPE("standardType", "STYPE", NitfHeader::getStandardType, BasicTypes.STRING_TYPE),
    FILE_DECLASSIFICATION_EXEMPTION("fileDeclassificationExemption",
            "FSDCXM",
            header -> header.getFileSecurityMetadata()
                    .getDeclassificationExemption(),
            BasicTypes.STRING_TYPE),
    FILE_DECLASSIFICATION_TYPE("fileDeclassificationType",
            "FSDCTP",
            header -> header.getFileSecurityMetadata()
                    .getDeclassificationType(),
            BasicTypes.STRING_TYPE),
    FILE_DECLASSIFICATION_DATE("fileDeclassificationDate",
            "FSDCDT",
            header -> header.getFileSecurityMetadata()
                    .getDeclassificationDate(),
            BasicTypes.STRING_TYPE),
    FILE_DOWNGRADE("fileDowngrade",
            "FSDG",
            header -> header.getFileSecurityMetadata()
                    .getDowngrade(),
            BasicTypes.STRING_TYPE),
    FILE_DOWNGRADE_DATE("fileDowngradeDate",
            "FSDGDT",
            header -> header.getFileSecurityMetadata()
                    .getDowngradeDate(),
            BasicTypes.STRING_TYPE),
    FILE_CLASSIFICATION_TEXT("fileClassificationText",
            "FSCLTX",
            header -> header.getFileSecurityMetadata()
                    .getClassificationText(),
            BasicTypes.STRING_TYPE),
    FILE_CLASSIFICATION_AUTHORITY_TYPE("fileClassificationAuthorityType",
            "FSCATP",
            header -> header.getFileSecurityMetadata()
                    .getClassificationAuthorityType(),
            BasicTypes.STRING_TYPE),
    FILE_CLASSIFICATION_AUTHORITY("fileClassificationAuthority",
            "FSCAUT",
            header -> header.getFileSecurityMetadata()
                    .getClassificationAuthority(),
            BasicTypes.STRING_TYPE),
    FILE_CLASSIFICATION_REASON("fileClassificationReason",
            "FSCRSN",
            header -> header.getFileSecurityMetadata()
                    .getClassificationReason(),
            BasicTypes.STRING_TYPE),
    FILE_SECURITY_SOURCE_DATE("fileSecuritySourceDate",
            "FSSRDT",
            header -> header.getFileSecurityMetadata()
                    .getSecuritySourceDate(),
            BasicTypes.STRING_TYPE),
    FILE_SECURITY_CONTROL_NUMBER("fileSecurityControlNumber",
            "FSCTLN",
            header -> header.getFileSecurityMetadata()
                    .getSecurityControlNumber(),
            BasicTypes.STRING_TYPE),
    FILE_COPY_NUMBER("fileCopyNumber",
            "FSCOP",
            header -> header.getFileSecurityMetadata()
                    .getFileCopyNumber(),
            BasicTypes.STRING_TYPE),
    FILE_NUMBER_OF_COPIES("fileNumberOfCopies",
            "FSCPYS",
            header -> header.getFileSecurityMetadata()
                    .getFileNumberOfCopies(),
            BasicTypes.STRING_TYPE),
    FILE_BACKGROUND_COLOR("fileBackgroundColor",
            "FBKGC",
            header -> header.getFileBackgroundColour() != null ?
                    header.getFileBackgroundColour()
                            .toString() :
                    "",
            BasicTypes.STRING_TYPE);

    private String shortName;

    private String longName;

    private Function<NitfHeader, Serializable> accessorFunction;

    private Set<AttributeDescriptor> attributeDescriptors;

    /*
     * Used only for Nitf attributes that do not map to the taxonomy.
     */
    private NitfHeaderAttribute(final String lName, final String sName,
            final Function<NitfHeader, Serializable> function, AttributeType attributeType) {
        this(lName,
                sName,
                function,
                new AttributeDescriptorImpl(ExtNitfUtility.EXT_NITF_PREFIX + lName,
                        true, /* indexed */
                        true, /* stored */
                        false, /* tokenized */
                        false, /* multivalued */
                        attributeType),
                "");
    }

    NitfHeaderAttribute(final String lName, final String sName,
            final Function<NitfHeader, Serializable> function,
            AttributeDescriptor attributeDescriptor, String extNitfName) {
        this.shortName = sName;
        this.longName = lName;
        this.accessorFunction = function;
        // retrieving metacard attribute descriptor for this attribute to prevent later lookups
        this.attributeDescriptors = new HashSet<>();
        this.attributeDescriptors.add(attributeDescriptor);
        if (StringUtils.isNotEmpty(extNitfName)) {
            this.attributeDescriptors.add(ExtNitfUtility.createDuplicateDescriptorAndRename(
                    extNitfName,
                    attributeDescriptor));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getShortName() {
        return this.shortName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getLongName() {
        return this.longName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Function<NitfHeader, Serializable> getAccessorFunction() {
        return accessorFunction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AttributeDescriptor> getAttributeDescriptors() {
        return this.attributeDescriptors;
    }

    /**
     * Equivalent to getLongName().
     *
     * @return the attribute's long name.
     */
    public String toString() {
        return getLongName();
    }

    private static Date convertNitfDate(DateTime nitfDateTime) {
        if (nitfDateTime == null || nitfDateTime.getZonedDateTime() == null) {
            return null;
        }

        ZonedDateTime zonedDateTime = nitfDateTime.getZonedDateTime();
        Instant instant = zonedDateTime.toInstant();

        if (instant != null) {
            return Date.from(instant);
        }

        return null;
    }
}
