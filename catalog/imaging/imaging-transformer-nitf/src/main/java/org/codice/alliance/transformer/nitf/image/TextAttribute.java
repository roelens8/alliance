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
package org.codice.alliance.transformer.nitf.image;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.text.TextSegment;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;

/**
 * NitfAttributes to represent the properties of a TextSegment.
 */
public enum TextAttribute implements NitfAttribute<TextSegment> {
    FILE_PART_TYPE("filePartType", "TE", segment -> "TE"),
    TEXT_IDENTIFIER("textIdentifier", "TEXTID", TextSegment::getIdentifier),
    TEXT_ATTACHMENT_LEVEL("textAttachmentLevel",
            "TXTALVL",
            TextSegment::getAttachmentLevel,
            Collections.singletonList(BasicTypes.INTEGER_TYPE)),
    TEXT_DATE_AND_TIME("textDateAndTime",
            "TXTDT",
            segment -> convertNitfDate(segment.getTextDateTime()),
            Collections.singletonList(BasicTypes.DATE_TYPE)),
    TEXT_TITLE("textTitle", "TXTITL", TextSegment::getTextTitle),
    TEXT_SECURITY_CLASSIFICATION("textSecurityClassification",
            "TSCLAS",
            segment -> segment.getSecurityMetadata()
                    .getSecurityClassificationSystem()),
    TEXT_CLASSIFICATION_SECURITY_SYSTEM("textClassificationSecuritySystem",
            "TSCLSY",
            segment -> segment.getSecurityMetadata()
                    .getSecurityClassificationSystem()),
    TEXT_CODEWORDS("textCodewords",
            "TSCODE",
            segment -> segment.getSecurityMetadata()
                    .getCodewords()),
    TEXT_CONTROL_AND_HANDLING("textControlandHandling",
            "TSCTLH",
            segment -> segment.getSecurityMetadata()
                    .getControlAndHandling()),
    TEXT_RELEASING_INSTRUCTIONS("textReleasingInstructions",
            "TSREL",
            segment -> segment.getSecurityMetadata()
                    .getReleaseInstructions()),
    TEXT_DECLASSIFICATION_TYPE("textDeclassificationType",
            "TSDCTP",
            segment -> segment.getSecurityMetadata()
                    .getDeclassificationType()),
    TEXT_DECLASSIFICATION_DATE("textDeclassificationDate",
            "TSDCDT",
            segment -> segment.getSecurityMetadata()
                    .getDeclassificationDate()),
    TEXT_DECLASSIFICATION_EXEMPTION("textDeclassificationExemption",
            "TSDCXM",
            segment -> segment.getSecurityMetadata()
                    .getDeclassificationExemption()),
    TEXT_DOWNGRADE("textDowngrade",
            "TSDG",
            segment -> segment.getSecurityMetadata()
                    .getDowngrade()),
    TEXT_DOWNGRADE_DATE("textDowngradeDate",
            "TSDGDT",
            segment -> segment.getSecurityMetadata()
                    .getDowngradeDate()),
    TEXT_CLASSIFICATION_TEXT("textClassificationText",
            "TSCLTX",
            segment -> segment.getSecurityMetadata()
                    .getClassificationText()),
    TEXT_CLASSIFICATION_AUTHORITY_TYPE("textClassificationAuthorityType",
            "TSCA TP",
            segment -> segment.getSecurityMetadata()
                    .getClassificationAuthorityType()),
    TEXT_CLASSIFICATION_AUTHORITY("textClassificationAuthority",
            "TSCAUT",
            segment -> segment.getSecurityMetadata()
                    .getClassificationAuthority()),
    TEXT_CLASSIFICATION_REASON("textClassificationReason",
            "TSCRSN",
            segment -> segment.getSecurityMetadata()
                    .getClassificationReason()),
    TEXT_SECURITY_SOURCE_DATE("textSecuritySourceDate",
            "TSSRDT",
            segment -> segment.getSecurityMetadata()
                    .getSecuritySourceDate()),
    TEXT_SECURITY_CONTROL_NUMBER("textSecurityControlNumber",
            "TSCTLN",
            segment -> segment.getSecurityMetadata()
                    .getSecurityControlNumber()),
    TEXT_FORMAT("textFormat",
            "TXTFMT",
            segment -> segment.getTextFormat()
                    .name()),
    TEXT_EXTENDED_SUBHEADER_DATA_LENGTH("textExtendedSubheaderDataLength",
            "TXSHDL",
            TextSegment::getExtendedHeaderDataOverflow,
            Collections.singletonList(BasicTypes.INTEGER_TYPE));

    public static final String ATTRIBUTE_NAME_PREFIX = "text.";

    private String shortName;

    private String longName;

    private Function<TextSegment, Serializable> accessorFunction;

    private Set<AttributeDescriptor> attributeDescriptors;

    TextAttribute(final String lName, final String sName,
            final Function<TextSegment, Serializable> accessor) {
        this(lName, sName, accessor, Collections.singletonList(BasicTypes.STRING_TYPE));
    }

    TextAttribute(final String lName, final String sName,
            final Function<TextSegment, Serializable> accessor,
            List<AttributeType> attributeTypes) {
        this.longName = lName;
        this.shortName = sName;
        this.accessorFunction = accessor;
        this.attributeDescriptors = createAttributeDescriptors(attributeTypes);
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
    public Function<TextSegment, Serializable> getAccessorFunction() {
        return accessorFunction;
    }

    /**
     * Equivalent to getLongName()
     *
     * @return the attribute's long name.
     */
    @Override
    public String toString() {
        return getLongName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AttributeDescriptor> getAttributeDescriptors() {
        return this.attributeDescriptors;
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

    private Set<AttributeDescriptor> createAttributeDescriptors(
            List<AttributeType> attributeTypes) {
        Set<AttributeDescriptor> attributesDescriptors = new HashSet<>();
        for (AttributeType attribute : attributeTypes) {
            attributesDescriptors.add(createAttributeDescriptor(attribute));
        }
        return attributesDescriptors;
    }

    private AttributeDescriptor createAttributeDescriptor(AttributeType attributeType) {
        return new AttributeDescriptorImpl(
                ExtNitfUtility.EXT_NITF_PREFIX + ATTRIBUTE_NAME_PREFIX + longName,
                true, /* indexed */
                true, /* stored */
                false, /* tokenized */
                true, /* multivalued */
                attributeType);
    }
}
