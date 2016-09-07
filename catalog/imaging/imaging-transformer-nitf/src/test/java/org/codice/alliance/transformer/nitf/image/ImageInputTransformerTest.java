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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.transformer.nitf.MetacardFactory;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.alliance.transformer.nitf.common.NitfHeaderAttribute;
import org.codice.alliance.transformer.nitf.common.NitfHeaderTransformer;
import org.codice.imaging.nitf.fluent.NitfParserInputFlow;
import org.codice.imaging.nitf.fluent.NitfSegmentsFlow;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.federation.FederationException;
import ddf.catalog.source.SourceUnavailableException;
import ddf.catalog.source.UnsupportedQueryException;

public class ImageInputTransformerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageInputTransformerTest.class);

    private static final String GEO_NITF = "i_3001a.ntf";

    private NitfImageTransformer transformer = null;

    private NitfHeaderTransformer headerTransformer = null;

    private MetacardFactory metacardFactory = null;

    @Before
    public void createTransformer()
            throws UnsupportedQueryException, SourceUnavailableException, FederationException {
        transformer = new NitfImageTransformer();

        metacardFactory = new MetacardFactory();
        metacardFactory.setMetacardType(new ImageMetacardType());

        headerTransformer = new NitfHeaderTransformer();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNitfSegmentsFlow() throws Exception {
        transformer.transform(null, new MetacardImpl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMetacard() throws Exception {
        NitfSegmentsFlow nitfSegmentsFlow = mock(NitfSegmentsFlow.class);
        transformer.transform(nitfSegmentsFlow, null);
    }

    @Test
    public void testNitfParsing() throws Exception {
        NitfSegmentsFlow nitfSegmentsFlow = new NitfParserInputFlow().inputStream(getInputStream(
                GEO_NITF))
                .allData();

        Metacard metacard = metacardFactory.createMetacard("101");
        nitfSegmentsFlow = headerTransformer.transform(nitfSegmentsFlow, metacard);
        metacard = transformer.transform(nitfSegmentsFlow, metacard);

        assertNotNull(metacard);

        validateDate(metacard, metacard.getCreatedDate(), "1997-12-17 10:26:30");
        validateDate(metacard, metacard.getEffectiveDate(), "1997-12-17 10:26:30");
        validateDate(metacard, metacard.getModifiedDate(), "1997-12-17 10:26:30");

        assertThat(metacard.getMetacardType()
                .getName(), is("isr.image"));
        assertThat(
                "Checks an uncompressed 1024x1024 8 bit mono image with GEOcentric data. Airfield",
                is(metacard.getTitle()));
        String wkt = metacard.getLocation();
        assertTrue(wkt.matches(
                "^POLYGON \\(\\(85 32.98\\d*, 85.00\\d* 32.98\\d*, 85.00\\d* 32.98\\d*, 85 32.98\\d*, 85 32.98\\d*\\)\\)"));

        Map<NitfAttribute, Object> map = initAttributesToBeAsserted();
        assertAttributesMap(metacard, map);

        Map<NitfAttribute, String> dateMap = initDateAttributesToBeAsserted();
        assertDateAttributesMap(metacard, dateMap);
    }

    @Test
    public void testHandleMissionIdSuccessful() throws Exception {
        Metacard metacard = metacardFactory.createMetacard("missionIdTest");
        transformer.handleMissionIdentifier(metacard, "0123456789ABC");
        assertThat(metacard.getAttribute(Isr.MISSION_ID)
                .getValue(), is("789A"));
    }

    @Test
    public void testHandleMissionIdEmpty() throws Exception {
        Metacard metacard = metacardFactory.createMetacard("noMissionIdTest");
        transformer.handleMissionIdentifier(metacard, "0123456");
        assertThat(metacard.getAttribute(Isr.MISSION_ID), nullValue());
    }

    @Test
    public void testHandleMissionIdNoImageIdentifier() throws Exception {
        Metacard metacard = metacardFactory.createMetacard("noIdentifierTest");
        transformer.handleMissionIdentifier(metacard, null);
        assertThat(metacard.getAttribute(Isr.MISSION_ID), nullValue());
    }

    @Test
    public void testHandleCommentsSuccessful() throws Exception {
        final String blockComment =
                "The credit belongs to the man who is actually in the arena, whose face is marred" +
                        " by dust and sweat and blood; who strives valiantly; who errs, who comes short a"
                        +
                        "gain and again, because there is no effort without error and shortcoming; but wh"
                        +
                        "o does actually strive to do the deeds.";

        List<String> commentsList = Arrays.asList(
                "The credit belongs to the man who is actually in the arena, whose face is marred",
                " by dust and sweat and blood; who strives valiantly; who errs, who comes short a",
                "gain and again, because there is no effort without error and shortcoming; but wh",
                "o does actually strive to do the deeds.");
        Metacard metacard = metacardFactory.createMetacard("commentsTest");
        transformer.handleComments(metacard, commentsList);
        assertThat(metacard.getAttribute(Isr.COMMENTS)
                .getValue(), is(blockComment));
    }

    @Test
    public void testHandleCommentsEmpty() throws Exception {
        List<String> commentsList = new ArrayList<>();
        Metacard metacard = metacardFactory.createMetacard("commentsTest");
        transformer.handleComments(metacard, commentsList);
        assertThat(metacard.getAttribute(Isr.COMMENTS), nullValue());
    }

    private void validateDate(Metacard metacard, Date date, String expectedDate) {
        assertNotNull(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(formatter.format(date), is(expectedDate));
        LOGGER.info("metacard = {}", metacard.getMetadata());
    }

    private InputStream getInputStream(String filename) {
        assertNotNull("Test file missing",
                getClass().getClassLoader()
                        .getResource(filename));
        return getClass().getClassLoader()
                .getResourceAsStream(filename);
    }

    private void assertDateAttributesMap(Metacard metacard, Map<NitfAttribute, String> map) {
        for (Map.Entry<NitfAttribute, String> entry : map.entrySet()) {
            for (AttributeDescriptor attributeDescriptor : (Set<AttributeDescriptor>) entry.getKey()
                    .getAttributeDescriptors()) {
                Attribute attribute = metacard.getAttribute(attributeDescriptor.getName());
                Date fileDateAndTime = (Date) attribute.getValue();
                String fileDateAndTimeString =
                        DateTimeFormatter.ISO_INSTANT.format(fileDateAndTime.toInstant());

                assertThat(fileDateAndTimeString, is(entry.getValue()));
            }
        }
    }

    private void assertAttributesMap(Metacard metacard, Map<NitfAttribute, Object> map) {
        for (Map.Entry<NitfAttribute, Object> entry : map.entrySet()) {
            for (AttributeDescriptor attributeDescriptor : (Set<AttributeDescriptor>) entry.getKey()
                    .getAttributeDescriptors()) {
                Attribute attribute = metacard.getAttribute(attributeDescriptor.getName());
                if (attribute != null) {
                    assertThat(attribute.getValue(), is(entry.getValue()));
                } else {
                    assertThat(attribute, nullValue());
                }
            }
        }
    }

    private Map<NitfAttribute, Object> initAttributesToBeAsserted() {
        //key value pair of attributes and expected values
        Map<NitfAttribute, Object> map = new HashMap<>();
        map.put(NitfHeaderAttribute.FILE_PROFILE_NAME, "NITF_TWO_ONE");
        map.put(NitfHeaderAttribute.FILE_VERSION, "NITF_TWO_ONE");
        map.put(NitfHeaderAttribute.COMPLEXITY_LEVEL, 3);
        map.put(NitfHeaderAttribute.STANDARD_TYPE, "BF01");
        map.put(NitfHeaderAttribute.ORIGINATING_STATION_ID, "i_3001a");
        map.put(NitfHeaderAttribute.FILE_TITLE,
                "Checks an uncompressed 1024x1024 8 bit mono image with GEOcentric data. Airfield");
        map.put(NitfHeaderAttribute.FILE_SECURITY_CLASSIFICATION, "UNCLASSIFIED");
        map.put(NitfHeaderAttribute.FILE_CLASSIFICATION_SECURITY_SYSTEM, null);
        map.put(NitfHeaderAttribute.FILE_CODE_WORDS, null);
        map.put(NitfHeaderAttribute.FILE_CONTROL_AND_HANDLING, null);
        map.put(NitfHeaderAttribute.FILE_RELEASING_INSTRUCTIONS, null);
        map.put(NitfHeaderAttribute.FILE_DECLASSIFICATION_TYPE, null);
        map.put(NitfHeaderAttribute.FILE_DECLASSIFICATION_DATE, null);
        map.put(NitfHeaderAttribute.FILE_DECLASSIFICATION_EXEMPTION, null);
        map.put(NitfHeaderAttribute.FILE_DOWNGRADE, null);
        map.put(NitfHeaderAttribute.FILE_RELEASING_INSTRUCTIONS, null);
        map.put(NitfHeaderAttribute.FILE_DOWNGRADE_DATE, null);
        map.put(NitfHeaderAttribute.FILE_CLASSIFICATION_TEXT, null);
        map.put(NitfHeaderAttribute.FILE_CLASSIFICATION_AUTHORITY_TYPE, null);
        map.put(NitfHeaderAttribute.FILE_CLASSIFICATION_AUTHORITY, null);
        map.put(NitfHeaderAttribute.FILE_CLASSIFICATION_REASON, null);
        map.put(NitfHeaderAttribute.FILE_SECURITY_SOURCE_DATE, null);
        map.put(NitfHeaderAttribute.FILE_SECURITY_CONTROL_NUMBER, null);
        map.put(NitfHeaderAttribute.FILE_COPY_NUMBER, "00000");
        map.put(NitfHeaderAttribute.FILE_NUMBER_OF_COPIES, "00000");
        map.put(NitfHeaderAttribute.FILE_BACKGROUND_COLOR, "[0xff,0xff,0xff]");
        map.put(NitfHeaderAttribute.ORIGINATORS_NAME, "JITC Fort Huachuca, AZ");
        map.put(NitfHeaderAttribute.ORIGINATORS_PHONE_NUMBER, "(520) 538-5458");
        map.put(ImageAttribute.FILE_PART_TYPE, "IM");
        map.put(ImageAttribute.IMAGE_IDENTIFIER_1, "Missing ID");
        map.put(ImageAttribute.TARGET_IDENTIFIER, null);
        map.put(ImageAttribute.IMAGE_IDENTIFIER_2, "- BASE IMAGE -");
        map.put(ImageAttribute.IMAGE_SECURITY_CLASSIFICATION, "UNCLASSIFIED");
        map.put(ImageAttribute.IMAGE_CLASSIFICATION_SECURITY_SYSTEM, null);
        map.put(ImageAttribute.IMAGE_CODEWORDS, null);
        map.put(ImageAttribute.IMAGE_CONTROL_AND_HANDLING, null);
        map.put(ImageAttribute.IMAGE_RELEASING_INSTRUCTIONS, null);
        map.put(ImageAttribute.IMAGE_DECLASSIFICATION_TYPE, null);
        map.put(ImageAttribute.IMAGE_DECLASSIFICATION_DATE, null);
        map.put(ImageAttribute.IMAGE_DECLASSIFICATION_EXEMPTION, null);
        map.put(ImageAttribute.IMAGE_DOWNGRADE, null);
        map.put(ImageAttribute.IMAGE_DOWNGRADE_DATE, null);
        map.put(ImageAttribute.IMAGE_CLASSIFICATION_TEXT, null);
        map.put(ImageAttribute.IMAGE_CLASSIFICATION_AUTHORITY_TYPE, null);
        map.put(ImageAttribute.IMAGE_CLASSIFICATION_AUTHORITY, null);
        map.put(ImageAttribute.IMAGE_CLASSIFICATION_REASON, null);
        map.put(ImageAttribute.IMAGE_SECURITY_SOURCE_DATE, null);
        map.put(ImageAttribute.IMAGE_SECURITY_CONTROL_NUMBER, null);
        map.put(ImageAttribute.IMAGE_SOURCE, "Unknown");
        map.put(ImageAttribute.NUMBER_OF_SIGNIFICANT_ROWS_IN_IMAGE, 1024L);
        map.put(ImageAttribute.NUMBER_OF_SIGNIFICANT_COLUMNS_IN_IMAGE, 1024L);
        map.put(ImageAttribute.PIXEL_VALUE_TYPE, "INTEGER");
        map.put(ImageAttribute.IMAGE_REPRESENTATION, "MONOCHROME");
        map.put(ImageAttribute.IMAGE_CATEGORY, "VISUAL");
        map.put(ImageAttribute.ACTUAL_BITS_PER_PIXEL_PER_BAND, 8);
        map.put(ImageAttribute.PIXEL_JUSTIFICATION, "RIGHT");
        map.put(ImageAttribute.IMAGE_COORDINATE_REPRESENTATION, "GEOGRAPHIC");
        map.put(ImageAttribute.NUMBER_OF_IMAGE_COMMENTS, 0);
        map.put(ImageAttribute.IMAGE_COMMENT_1, null);
        map.put(ImageAttribute.IMAGE_COMPRESSION, "NOTCOMPRESSED");
        return map;
    }

    private Map<NitfAttribute, String> initDateAttributesToBeAsserted() {
        //key value pair of attributes and expected values
        Map<NitfAttribute, String> map = new HashMap<>();
        map.put(NitfHeaderAttribute.FILE_DATE_AND_TIME, "1997-12-17T10:26:30Z");
        map.put(ImageAttribute.IMAGE_DATE_AND_TIME, "1996-12-17T10:26:30Z");
        return map;
    }
}