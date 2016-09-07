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
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.codice.alliance.catalog.core.api.impl.types.IsrAttributes;
import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.types.DateTimeAttributes;
import ddf.catalog.data.impl.types.MediaAttributes;
import ddf.catalog.data.types.Media;

/**
 * NitfAttributes to represent the properties of a ImageSegment.
 */
public enum ImageAttribute implements NitfAttribute<ImageSegment> {

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.image.*` when
     * appropriate
     */

    IMAGE_DATE_AND_TIME(ddf.catalog.data.types.DateTime.START,
            "IDATIM",
            segment -> convertNitfDate(segment.getImageDateTime()),
            new DateTimeAttributes().getAttributeDescriptor(ddf.catalog.data.types.DateTime.START),
            "imageDateAndTime"),
    TARGET_IDENTIFIER(Isr.TARGET_ID,
            "TGTID",
            ImageAttribute::getTargetId,
            new IsrAttributes().getAttributeDescriptor(Isr.TARGET_ID),
            "targetIdentifier"),
    IMAGE_IDENTIFIER_2(Isr.IMAGE_ID,
            "IID2",
            ImageSegment::getImageIdentifier2,
            new IsrAttributes().getAttributeDescriptor(Isr.IMAGE_ID),
            "imageIdentifier2"),
    IMAGE_SOURCE(Isr.ORIGINAL_SOURCE,
            "ISORCE",
            ImageSegment::getImageSource,
            new IsrAttributes().getAttributeDescriptor(Isr.ORIGINAL_SOURCE),
            "imageSource"),
    NUMBER_OF_SIGNIFICANT_ROWS_IN_IMAGE(Media.HEIGHT,
            "NROWS",
            ImageSegment::getNumberOfRows,
            new MediaAttributes().getAttributeDescriptor(Media.HEIGHT),
            "numberOfSignificantRowsInImage"),
    NUMBER_OF_SIGNIFICANT_COLUMNS_IN_IMAGE(Media.WIDTH,
            "NCOLS",
            ImageSegment::getNumberOfColumns,
            new MediaAttributes().getAttributeDescriptor(Media.WIDTH),
            "numberOfSignificantColumnsInImage"),
    IMAGE_REPRESENTATION(Media.ENCODING,
            "IREP",
            segment -> segment.getImageRepresentation()
                    .name(),
            new MediaAttributes().getAttributeDescriptor(Media.ENCODING),
            "imageRepresentation"),
    IMAGE_CATEGORY(Isr.CATEGORY,
            "ICAT",
            segment -> segment.getImageCategory()
                    .name(),
            new IsrAttributes().getAttributeDescriptor(Isr.CATEGORY),
            "imageCategory"),
    IMAGE_COMPRESSION(Media.COMPRESSION,
            "IC",
            segment -> segment.getImageCompression()
                    .name(),
            new MediaAttributes().getAttributeDescriptor(Media.COMPRESSION),
            "imageCompression"),

    /*
     * Non normalized attributes
     */

    FILE_PART_TYPE("filePartType", "IM", segment -> "IM", BasicTypes.STRING_TYPE),
    IMAGE_IDENTIFIER_1("imageIdentifier1",
            "IID1",
            ImageSegment::getIdentifier,
            BasicTypes.STRING_TYPE),
    IMAGE_SECURITY_CLASSIFICATION("imageSecurityClassification",
            "ISCLAS",
            segment -> segment.getSecurityMetadata()
                    .getSecurityClassification()
                    .name(),
            BasicTypes.STRING_TYPE),
    IMAGE_CLASSIFICATION_SECURITY_SYSTEM("imageClassificationSecuritySystem",
            "ISCLSY",
            segment -> segment.getSecurityMetadata()
                    .getSecurityClassificationSystem(),
            BasicTypes.STRING_TYPE),
    IMAGE_CODEWORDS("imageCodewords",
            "ISCODE",
            segment -> segment.getSecurityMetadata()
                    .getCodewords(),
            BasicTypes.STRING_TYPE),
    IMAGE_CONTROL_AND_HANDLING("imageControlandHandling",
            "ISCTLH",
            segment -> segment.getSecurityMetadata()
                    .getControlAndHandling(),
            BasicTypes.STRING_TYPE),
    IMAGE_RELEASING_INSTRUCTIONS("imageReleasingInstructions",
            "ISREL",
            segment -> segment.getSecurityMetadata()
                    .getReleaseInstructions(),
            BasicTypes.STRING_TYPE),
    IMAGE_DECLASSIFICATION_TYPE("imageDeclassificationType",
            "ISDCTP",
            segment -> segment.getSecurityMetadata()
                    .getDeclassificationType(),
            BasicTypes.STRING_TYPE),
    IMAGE_DECLASSIFICATION_DATE("imageDeclassificationDate",
            "ISDCDT",
            segment -> segment.getSecurityMetadata()
                    .getDeclassificationDate(),
            BasicTypes.STRING_TYPE),
    IMAGE_DECLASSIFICATION_EXEMPTION("imageDeclassificationExemption",
            "ISDCXM",
            segment -> segment.getSecurityMetadata()
                    .getDeclassificationExemption(),
            BasicTypes.STRING_TYPE),
    IMAGE_DOWNGRADE("imageDowngrade",
            "ISDG",
            segment -> segment.getSecurityMetadata()
                    .getDowngrade(),
            BasicTypes.STRING_TYPE),
    IMAGE_DOWNGRADE_DATE("imageDowngradeDate",
            "ISDGDT",
            segment -> segment.getSecurityMetadata()
                    .getDowngradeDate(),
            BasicTypes.STRING_TYPE),
    IMAGE_CLASSIFICATION_TEXT("imageClassificationText",
            "ISCLTX",
            segment -> segment.getSecurityMetadata()
                    .getClassificationText(),
            BasicTypes.STRING_TYPE),
    IMAGE_CLASSIFICATION_AUTHORITY_TYPE("imageClassificationAuthorityType",
            "ISCATP",
            segment -> segment.getSecurityMetadata()
                    .getClassificationAuthorityType(),
            BasicTypes.STRING_TYPE),
    IMAGE_CLASSIFICATION_AUTHORITY("imageClassificationAuthority",
            "ISCAUT",
            segment -> segment.getSecurityMetadata()
                    .getClassificationAuthority(),
            BasicTypes.STRING_TYPE),
    IMAGE_CLASSIFICATION_REASON("imageClassificationReason",
            "ISCRSN",
            segment -> segment.getSecurityMetadata()
                    .getClassificationReason(),
            BasicTypes.STRING_TYPE),
    IMAGE_SECURITY_SOURCE_DATE("imageSecuritySourceDate",
            "ISSRDT",
            segment -> segment.getSecurityMetadata()
                    .getSecuritySourceDate(),
            BasicTypes.STRING_TYPE),
    IMAGE_SECURITY_CONTROL_NUMBER("imageSecurityControlNumber",
            "ISCTLN",
            segment -> segment.getSecurityMetadata()
                    .getSecurityControlNumber(),
            BasicTypes.STRING_TYPE),
    PIXEL_VALUE_TYPE("pixelValueType",
            "PVTYPE",
            segment -> segment.getPixelValueType()
                    .name(),
            BasicTypes.STRING_TYPE),
    ACTUAL_BITS_PER_PIXEL_PER_BAND("actualBitsPerPixelPerBand",
            "ABPP",
            ImageSegment::getActualBitsPerPixelPerBand,
            BasicTypes.INTEGER_TYPE),
    PIXEL_JUSTIFICATION("pixelJustification",
            "PJUST",
            segment -> segment.getPixelJustification()
                    .name(),
            BasicTypes.STRING_TYPE),
    IMAGE_COORDINATE_REPRESENTATION("imageCoordinateRepresentation",
            "ICORDS",
            segment -> segment.getImageCoordinatesRepresentation()
                    .name(),
            BasicTypes.STRING_TYPE),
    NUMBER_OF_IMAGE_COMMENTS("numberOfImageComments",
            "NICOM",
            segment -> segment.getImageComments()
                    .size(),
            BasicTypes.INTEGER_TYPE),
    IMAGE_COMMENT_1("imageComment1",
            "ICOM1",
            segment -> segment.getImageComments()
                    .size() > 0 ?
                    segment.getImageComments()
                            .get(0) :
                    "",
            BasicTypes.STRING_TYPE),
    IMAGE_COMMENT_2("imageComment2",
            "ICOM2",
            segment -> segment.getImageComments()
                    .size() > 1 ?
                    segment.getImageComments()
                            .get(1) :
                    "",
            BasicTypes.STRING_TYPE),
    IMAGE_COMMENT_3("imageComment3",
            "ICOM3",
            segment -> segment.getImageComments()
                    .size() > 2 ?
                    segment.getImageComments()
                            .get(2) :
                    "",
            BasicTypes.STRING_TYPE),
    NUMBER_OF_BANDS("numberOfBands", "NBANDS", ImageSegment::getNumBands, BasicTypes.INTEGER_TYPE),
    IMAGE_MODE("imageMode",
            "IMODE",
            segment -> segment.getImageMode()
                    .name(),
            BasicTypes.STRING_TYPE),
    NUMBER_OF_BLOCKS_PER_ROW("numberOfBlocksPerRow",
            "NBPR",
            ImageSegment::getNumberOfBlocksPerRow,
            BasicTypes.INTEGER_TYPE),
    NUMBER_OF_BLOCKS_PER_COLUMN("numberOfBlocksPerColumn",
            "NBPC",
            ImageSegment::getNumberOfBlocksPerColumn,
            BasicTypes.INTEGER_TYPE),
    NUMBER_OF_PIXELS_PER_BLOCK_HORIZONTAL("numberOfPixelsPerBlockHorizontal",
            "NPPBH",
            ImageSegment::getNumberOfPixelsPerBlockHorizontal,
            BasicTypes.INTEGER_TYPE),
    NUMBER_OF_PIXELS_PER_BLOCK_VERTICAL("numberOfPixelsPerBlockVertical",
            "NPPBV",
            ImageSegment::getNumberOfPixelsPerBlockVertical,
            BasicTypes.INTEGER_TYPE),
    NUMBER_OF_BITS_PER_PIXEL("numberOfBitsPerPixel",
            "NBPP",
            ImageSegment::getNumberOfBitsPerPixelPerBand,
            BasicTypes.INTEGER_TYPE),
    IMAGE_DISPLAY_LEVEL("imageDisplayLevel",
            "IDLVL",
            ImageSegment::getImageDisplayLevel,
            BasicTypes.INTEGER_TYPE),
    IMAGE_ATTACHMENT_LEVEL("imageAttachmentLevel",
            "IALVL",
            ImageSegment::getAttachmentLevel,
            BasicTypes.INTEGER_TYPE),
    IMAGE_LOCATION("imageLocation",
            "ILOC",
            segment -> segment.getImageLocationRow() + "," + segment.getImageLocationColumn(),
            BasicTypes.STRING_TYPE),
    IMAGE_MAGNIFICATION("imageMagnification",
            "IMAG",
            segment -> segment.getImageMagnification()
                    .trim(),
            BasicTypes.STRING_TYPE);

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAttribute.class);

    private static final String ATTRIBUTE_NAME_PREFIX = "image.";

    private String shortName;

    private String longName;

    private Function<ImageSegment, Serializable> accessorFunction;

    private Set<AttributeDescriptor> attributeDescriptors;

    ImageAttribute(final String lName, final String sName,
            final Function<ImageSegment, Serializable> accessor, AttributeType attributeType) {
        this.accessorFunction = accessor;
        this.shortName = sName;
        this.longName = lName;
        // retrieving metacard attribute descriptor for this attribute to prevent later lookups
        this.attributeDescriptors = Collections.singleton(new AttributeDescriptorImpl(
                ExtNitfUtility.EXT_NITF_PREFIX + ATTRIBUTE_NAME_PREFIX + lName,
                true, /* indexed */
                true, /* stored */
                false, /* tokenized */
                true, /* multivalued */
                attributeType));
    }

    ImageAttribute(final String lName, final String sName,
            final Function<ImageSegment, Serializable> accessor,
            AttributeDescriptor attributeDescriptor, String extNitfName) {
        this.accessorFunction = accessor;
        this.shortName = sName;
        this.longName = lName;
        // retrieving metacard attribute descriptor for this attribute to prevent later lookups
        this.attributeDescriptors = new HashSet<>();
        this.attributeDescriptors.add(attributeDescriptor);
        if (StringUtils.isNotEmpty(extNitfName)) {
            this.attributeDescriptors.add(ExtNitfUtility.createDuplicateDescriptorAndRename(
                    ATTRIBUTE_NAME_PREFIX + extNitfName,
                    attributeDescriptor));
        }
    }

    private static String getTargetId(ImageSegment imageSegment) {
        if (imageSegment == null || imageSegment.getImageTargetId() == null) {
            return null;
        }

        try {
            return imageSegment.getImageTargetId()
                    .textValue()
                    .trim();
        } catch (NitfFormatException nfe) {
            LOGGER.debug(nfe.getMessage(), nfe);
        }

        return null;
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
    public Function<ImageSegment, Serializable> getAccessorFunction() {
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
     * Equivalent to getLongName()
     *
     * @return the attribute's long name.
     */
    @Override
    public String toString() {
        return getLongName();
    }
}
