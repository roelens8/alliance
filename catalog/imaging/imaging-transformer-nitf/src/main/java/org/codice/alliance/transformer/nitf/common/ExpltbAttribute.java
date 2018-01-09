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

import ddf.catalog.data.AttributeType;
import ddf.catalog.data.impl.BasicTypes;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.imaging.nitf.core.tre.Tre;

/** TRE for "Exploitation Related Information" */
public class ExpltbAttribute extends NitfAttributeImpl<Tre> {

  private static final String PREFIX = ExtNitfUtility.EXT_NITF_PREFIX + "expltb.";

  public static final String ANGLE_TO_NORTH = PREFIX + "angle-to-north";

  public static final String ANGLE_TO_NORTH_ACCURACY = PREFIX + "angle-to-north-accuracy";

  public static final String SQUINT_ANGLE = PREFIX + "squint-angle";

  public static final String SQUINT_ANGLE_ACCURACY = PREFIX + "squint-angle-accuracy";

  public static final String MODE = PREFIX + "mode";

  public static final String GRAZE_ANGLE = PREFIX + "graze-angle";

  public static final String GRAZE_ANGLE_ACCURACY = PREFIX + "graze-angle-accuracy";

  public static final String SLOPE_ANGLE = PREFIX + "slope-angle";

  public static final String POLAR = PREFIX + "polar";

  public static final String PIXELS_PER_LINE = PREFIX + "pixels-per-line";

  public static final String SEQUENCE_NUMBER = PREFIX + "sequence-number";

  public static final String PRIME_ID = PREFIX + "prime-id";

  public static final String PRIME_BASIC_ENCYCLOPEDIA = PREFIX + "prime-basic-encyclopedia";

  public static final String NUMBER_OF_SECONDARY_TARGETS = PREFIX + "number-of-secondary-targets";

  public static final String COMMANDED_IMPULSE_RESPONSE = PREFIX + "commanded-impulse-response";

  private static final List<NitfAttribute<Tre>> ATTRIBUTES = new LinkedList<>();

  /*
   * Non-normalized attributes
   */

  public static final ExpltbAttribute ANGLE_TO_NORTH_ATTRIBUTE =
      new ExpltbAttribute(
          ANGLE_TO_NORTH,
          "ANGLE_TO_NORTH",
          tre -> TreUtility.convertToFloat(tre, "ANGLE_TO_NORTH"),
          BasicTypes.FLOAT_TYPE);

  public static final ExpltbAttribute ANGLE_TO_NORTH_ACCURACY_ATTRIBUTE =
      new ExpltbAttribute(
          ANGLE_TO_NORTH_ACCURACY,
          "ANGLE_TO_NORTH_ACCY",
          tre -> TreUtility.convertToFloat(tre, "ANGLE_TO_NORTH_ACCY"),
          BasicTypes.FLOAT_TYPE);

  public static final ExpltbAttribute SQUINT_ANGLE_ATTRIBUTE =
      new ExpltbAttribute(
          SQUINT_ANGLE,
          "SQUINT_ANGLE",
          tre -> TreUtility.convertToFloat(tre, "SQUINT_ANGLE"),
          BasicTypes.FLOAT_TYPE);

  public static final ExpltbAttribute SQUINT_ANGLE_ACCURACY_ATTRIBUTE =
      new ExpltbAttribute(
          SQUINT_ANGLE_ACCURACY,
          "SQUINT_ANGLE_ACCY",
          tre -> TreUtility.convertToFloat(tre, "SQUINT_ANGLE_ACCY"),
          BasicTypes.FLOAT_TYPE);

  public static final ExpltbAttribute MODE_ATTRIBUTE =
      new ExpltbAttribute(
          MODE, "MODE", tre -> TreUtility.convertToString(tre, "MODE"), BasicTypes.STRING_TYPE);

  public static final ExpltbAttribute GRAZE_ANGLE_ATTRIBUTE =
      new ExpltbAttribute(
          GRAZE_ANGLE,
          "GRAZE_ANG",
          tre -> TreUtility.convertToFloat(tre, "GRAZE_ANG"),
          BasicTypes.FLOAT_TYPE);

  public static final ExpltbAttribute GRAZE_ANGLE_ACCURACY_ATTRIBUTE =
      new ExpltbAttribute(
          GRAZE_ANGLE_ACCURACY,
          "GRAZE_ANG_ACCY",
          tre -> TreUtility.convertToFloat(tre, "GRAZE_ANG_ACCY"),
          BasicTypes.FLOAT_TYPE);

  public static final ExpltbAttribute SLOPE_ANGLE_ATTRIBUTE =
      new ExpltbAttribute(
          SLOPE_ANGLE,
          "SLOPE_ANG",
          tre -> TreUtility.convertToFloat(tre, "SLOPE_ANG"),
          BasicTypes.FLOAT_TYPE);

  public static final ExpltbAttribute POLAR_ATTRIBUTE =
      new ExpltbAttribute(
          POLAR, "POLAR", tre -> TreUtility.convertToString(tre, "POLAR"), BasicTypes.STRING_TYPE);

  public static final ExpltbAttribute PIXELS_PER_LINE_ATTRIBUTE =
      new ExpltbAttribute(
          PIXELS_PER_LINE,
          "NSAMP",
          tre -> TreUtility.convertToInteger(tre, "NSAMP"),
          BasicTypes.INTEGER_TYPE);

  public static final ExpltbAttribute SEQUENCE_NUMBER_ATTRIBUTE =
      new ExpltbAttribute(
          SEQUENCE_NUMBER,
          "SEQ_NUM",
          tre -> TreUtility.convertToInteger(tre, "SEQ_NUM"),
          BasicTypes.INTEGER_TYPE);

  public static final ExpltbAttribute PRIME_ID_ATTRIBUTE =
      new ExpltbAttribute(
          PRIME_ID,
          "PRIME_ID",
          tre -> TreUtility.convertToString(tre, "PRIME_ID"),
          BasicTypes.STRING_TYPE);

  public static final ExpltbAttribute PRIME_BASIC_ENCYCLOPEDIA_ATTRIBUTE =
      new ExpltbAttribute(
          PRIME_BASIC_ENCYCLOPEDIA,
          "PRIME_BE",
          tre -> TreUtility.convertToString(tre, "PRIME_BE"),
          BasicTypes.STRING_TYPE);

  public static final ExpltbAttribute NUMBER_OF_SECONDARY_TARGETS_ATTRIBUTE =
      new ExpltbAttribute(
          NUMBER_OF_SECONDARY_TARGETS,
          "N_SEC",
          tre -> TreUtility.convertToInteger(tre, "N_SEC"),
          BasicTypes.INTEGER_TYPE);

  public static final ExpltbAttribute COMMANDED_IMPULSE_RESPONSE_ATTRIBUTE =
      new ExpltbAttribute(
          COMMANDED_IMPULSE_RESPONSE,
          "IPR",
          tre -> TreUtility.convertToInteger(tre, "IPR"),
          BasicTypes.INTEGER_TYPE);

  private ExpltbAttribute(
      String longName,
      String shortName,
      Function<Tre, Serializable> accessorFunction,
      AttributeType attributeType) {
    super(longName, shortName, accessorFunction, attributeType);
    ATTRIBUTES.add(this);
  }

  public static List<NitfAttribute<Tre>> getAttributes() {
    return Collections.unmodifiableList(ATTRIBUTES);
  }
}
