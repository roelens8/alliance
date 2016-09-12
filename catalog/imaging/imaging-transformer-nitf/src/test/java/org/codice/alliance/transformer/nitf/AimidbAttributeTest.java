package org.codice.alliance.transformer.nitf;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.stream.Stream;

import org.codice.alliance.transformer.nitf.common.AcftbAttribute;
import org.codice.alliance.transformer.nitf.common.AimidbAttribute;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.junit.Test;

public class AimidbAttributeTest {
    @Test
    public void testAimidbAttribute() throws NitfFormatException {
        Stream.of(AimidbAttribute.values())
                .forEach(attribute -> assertThat(attribute.getShortName(), notNullValue()));
        Stream.of(AcftbAttribute.values())
                .forEach(attribute -> assertThat(attribute.getLongName(), notNullValue()));
    }
}
