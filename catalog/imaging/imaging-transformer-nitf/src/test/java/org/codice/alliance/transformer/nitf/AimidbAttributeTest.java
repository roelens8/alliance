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
package org.codice.alliance.transformer.nitf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.codice.alliance.transformer.nitf.common.AimidbAttribute;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.core.tre.Tre;
import org.junit.Before;
import org.junit.Test;

public class AimidbAttributeTest {

    private final String US_FIPS_10_4 = "US";

    private final String DATE = "20160920131430";

    private Tre tre;

    @Before
    public void setUp() {
        tre = mock(Tre.class);
    }

    @Test
    public void testAimidbAttriubte() {
        Stream.of(AimidbAttribute.values())
                .forEach(attribute -> assertThat(attribute.getShortName(), notNullValue()));
        Stream.of(AimidbAttribute.values())
                .forEach(attribute -> assertThat(attribute.getLongName(), notNullValue()));
    }

    @Test
    public void testCountryCode() throws NitfFormatException {
        when(tre.getFieldValue(AimidbAttribute.COUNTRY.getShortName())).thenReturn(US_FIPS_10_4);

        String countryCode = (String) AimidbAttribute.COUNTRY.getAccessorFunction()
                .apply(tre);

        assertThat(countryCode, is("USA"));
    }

    @Test
    public void testInvalidCountryCode() throws NitfFormatException {
        when(tre.getFieldValue(AimidbAttribute.COUNTRY.getShortName())).thenReturn("");

        String countryCode = (String) AimidbAttribute.COUNTRY.getAccessorFunction()
                .apply(tre);

        assertThat(countryCode, is(""));
    }

    @Test
    public void testAcquisitionDate() throws NitfFormatException {
        when(tre.getFieldValue(AimidbAttribute.ACQUISITION_DATE.getShortName())).thenReturn(
                DATE);

        String countryCode = (String) AimidbAttribute.ACQUISITION_DATE.getAccessorFunction()
                .apply(tre);

        assertThat(countryCode, is("20160920131430"));
    }
}
