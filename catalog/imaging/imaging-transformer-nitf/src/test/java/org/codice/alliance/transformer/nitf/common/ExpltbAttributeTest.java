/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.transformer.nitf.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.core.tre.Tre;
import org.junit.Before;
import org.junit.Test;

public class ExpltbAttributeTest {

  private Tre tre;

  @Before
  public void setUp() {
    tre = mock(Tre.class);
  }

  @Test
  public void testAngleToNorth() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.ANGLE_TO_NORTH_ATTRIBUTE.getShortName()))
        .thenReturn("150.001");

    Serializable actual = ExpltbAttribute.ANGLE_TO_NORTH_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(150.001f));
  }

  @Test
  public void testAngleToNorthAccuracy() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.ANGLE_TO_NORTH_ACCURACY_ATTRIBUTE.getShortName()))
        .thenReturn("03.001");

    Serializable actual =
        ExpltbAttribute.ANGLE_TO_NORTH_ACCURACY_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(03.001f));
  }

  @Test
  public void testSquintAngle() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.SQUINT_ANGLE_ATTRIBUTE.getShortName()))
        .thenReturn("-59.002");

    Serializable actual = ExpltbAttribute.SQUINT_ANGLE_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(-59.002f));
  }

  @Test
  public void testSquintAngleAccuracy() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.SQUINT_ANGLE_ACCURACY_ATTRIBUTE.getShortName()))
        .thenReturn("44.002");

    Serializable actual =
        ExpltbAttribute.SQUINT_ANGLE_ACCURACY_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(44.002f));
  }

  @Test
  public void testMode() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.MODE_ATTRIBUTE.getShortName())).thenReturn("LBM");

    Serializable actual = ExpltbAttribute.MODE_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is("LBM"));
  }

  @Test
  public void testGrazeAngle() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.GRAZE_ANGLE_ATTRIBUTE.getShortName()))
        .thenReturn("50.00");

    Serializable actual = ExpltbAttribute.GRAZE_ANGLE_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(50.00f));
  }

  @Test
  public void testGrazeAngleAccuracy() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.GRAZE_ANGLE_ACCURACY_ATTRIBUTE.getShortName()))
        .thenReturn("00.01");

    Serializable actual =
        ExpltbAttribute.GRAZE_ANGLE_ACCURACY_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(00.01f));
  }

  @Test
  public void testSlopeAngle() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.SLOPE_ANGLE_ATTRIBUTE.getShortName()))
        .thenReturn("24.00");

    Serializable actual = ExpltbAttribute.SLOPE_ANGLE_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(24.00f));
  }

  @Test
  public void testPolar() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.POLAR_ATTRIBUTE.getShortName())).thenReturn("HH");

    Serializable actual = ExpltbAttribute.POLAR_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is("HH"));
  }

  @Test
  public void testPixelsPerLine() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.PIXELS_PER_LINE_ATTRIBUTE.getShortName()))
        .thenReturn("03333");

    Serializable actual =
        ExpltbAttribute.PIXELS_PER_LINE_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(3333));
  }

  @Test
  public void testSequenceNumber() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.SEQUENCE_NUMBER_ATTRIBUTE.getShortName()))
        .thenReturn("4");

    Serializable actual =
        ExpltbAttribute.SEQUENCE_NUMBER_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(4));
  }

  @Test
  public void testPrimeId() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.PRIME_ID_ATTRIBUTE.getShortName()))
        .thenReturn("aaaaaaaaaaaa");

    Serializable actual = ExpltbAttribute.PRIME_ID_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is("aaaaaaaaaaaa"));
  }

  @Test
  public void testPrimeBasicEncyclopedia() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.PRIME_BASIC_ENCYCLOPEDIA_ATTRIBUTE.getShortName()))
        .thenReturn("bbbbbbbbbbbbbbb");

    Serializable actual =
        ExpltbAttribute.PRIME_BASIC_ENCYCLOPEDIA_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is("bbbbbbbbbbbbbbb"));
  }

  @Test
  public void testNumberOfSecondaryTargets() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.NUMBER_OF_SECONDARY_TARGETS_ATTRIBUTE.getShortName()))
        .thenReturn("11");

    Serializable actual =
        ExpltbAttribute.NUMBER_OF_SECONDARY_TARGETS_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(11));
  }

  @Test
  public void testCommandedImpulseResponse() throws NitfFormatException {
    when(tre.getFieldValue(ExpltbAttribute.COMMANDED_IMPULSE_RESPONSE_ATTRIBUTE.getShortName()))
        .thenReturn("22");

    Serializable actual =
        ExpltbAttribute.COMMANDED_IMPULSE_RESPONSE_ATTRIBUTE.getAccessorFunction().apply(tre);
    assertThat(actual, is(22));
  }
}
