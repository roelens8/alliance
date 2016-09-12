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

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.stream.Stream;

import org.codice.alliance.transformer.nitf.common.AcftbAttribute;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.junit.Test;

public class AcftbAttributeTest {

    @Test
    public void testAfctbAttribute() throws NitfFormatException {
        Stream.of(AcftbAttribute.values())
                .forEach(attribute -> assertThat(attribute.getShortName(), notNullValue()));
        Stream.of(AcftbAttribute.values())
                .forEach(attribute -> assertThat(attribute.getLongName(), notNullValue()));
    }
}
