/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class PrimitiveOrEnumTypeConverterTest {

    private ConfigBuilder configBuilder = new ConfigBuilder();


    @Test
    public void testConfigProperiesWithConversions() throws ConfigBuilderException {
        Properties p = new Properties();
        p.setProperty("intProperty", "123");
        p.setProperty("floatProperty", "234.5");
        p.setProperty("longProperty", "345");
        p.setProperty("doubleProperty", "456.7");
        p.setProperty("booleanProperty", "true");
        p.setProperty("charProperty", "X");
        p.setProperty("shortProperty", "123");


        ConfigClassPropertyWithConversions c = configBuilder.buildConfig(ConfigClassPropertyWithConversions.class, p);
        assertEquals( 123, c.intProperty);
        assertEquals( 234.5f, c.floatProperty, 0);
        assertEquals( 345, c.longProperty);
        assertEquals(456.7d, c.doubleProperty, 0);
        assertEquals( true, c.booleanProperty);
        assertEquals( 'X', c.charProperty);
        assertEquals( 123, c.shortProperty);



    }

    @Test
    public void testConfigProperiesWithPrimitiveSetters() throws ConfigBuilderException {
        Properties p = new Properties();
        p.setProperty("intProperty", "123");
        p.setProperty("floatProperty", "234.5");
        p.setProperty("longProperty", "345");
        p.setProperty("doubleProperty", "456.7");
        p.setProperty("booleanProperty", "true");
        p.setProperty("charProperty", "X");
        p.setProperty("shortProperty", "123");


        ConfigClassPropertyWithPrimitivesSetters c = configBuilder.buildConfig(ConfigClassPropertyWithPrimitivesSetters.class, p);
        assertEquals( 123, c.intProperty);
        assertEquals( 234.5f, c.floatProperty, 0);
        assertEquals( 345, c.longProperty);
        assertEquals(456.7d, c.doubleProperty, 0);
        assertEquals( true, c.booleanProperty);
        assertEquals( 'X', c.charProperty);
        assertEquals( 123, c.shortProperty);
    }

    static class ConfigClassPropertyWithConversions {

        private int intProperty;
        private long longProperty;
        private float floatProperty;
        private double doubleProperty;
        private boolean booleanProperty;
        private char charProperty;
        private short shortProperty;

        @ConfigProperty(
            name = "intProperty",
            description = "intProperty"
        )
        public void setIntProperty(Integer intProperty) {
            this.intProperty = intProperty;
        }

        @ConfigProperty(
            name = "floatProperty",
            description = "floatProperty"
        )
        public void setFloatProperty(Float floatProperty) {
            this.floatProperty = floatProperty;
        }

        @ConfigProperty(
            name = "longProperty",
            description = "longProperty"
        )
        public void setLongProperty(Long longProperty) {
            this.longProperty = longProperty;
        }

        @ConfigProperty(
            name = "doubleProperty",
            description = "doubleProperty"
        )
        public void setDoubleProperty(Double doubleProperty) {
            this.doubleProperty = doubleProperty;
        }

        @ConfigProperty(
            name = "booleanProperty",
            description = "booleanProperty"
        )
        public void setBooleanProperty(Boolean booleanProperty) {
            this.booleanProperty = booleanProperty;
        }

        @ConfigProperty(
            name = "charProperty",
            description = "charProperty"
        )
        public void setCharProperty(Character charProperty) {
            this.charProperty = charProperty;
        }

        @ConfigProperty(
            name = "shortProperty",
            description = "shortProperty"
        )
        public void setShortProperty(Short shortProperty) {
            this.shortProperty = shortProperty;
        }

    }

    static class ConfigClassPropertyWithPrimitivesSetters {

        private int intProperty;
        private long longProperty;
        private float floatProperty;
        private double doubleProperty;
        private boolean booleanProperty;
        private char charProperty;
        private short shortProperty;

        @ConfigProperty(
            name = "intProperty",
            description = "intProperty"
        )
        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        @ConfigProperty(
            name = "floatProperty",
            description = "floatProperty"
        )
        public void setFloatProperty(float floatProperty) {
            this.floatProperty = floatProperty;
        }

        @ConfigProperty(
            name = "longProperty",
            description = "longProperty"
        )
        public void setLongProperty(long longProperty) {
            this.longProperty = longProperty;
        }

        @ConfigProperty(
            name = "doubleProperty",
            description = "doubleProperty"
        )
        public void setDoubleProperty(double doubleProperty) {
            this.doubleProperty = doubleProperty;
        }

        @ConfigProperty(
            name = "booleanProperty",
            description = "booleanProperty"
        )
        public void setBooleanProperty(boolean booleanProperty) {
            this.booleanProperty = booleanProperty;
        }

        @ConfigProperty(
            name = "charProperty",
            description = "charProperty"
        )
        public void setCharProperty(char charProperty) {
            this.charProperty = charProperty;
        }

        @ConfigProperty(
            name = "shortProperty",
            description = "shortProperty"
        )
        public void setShortProperty(short shortProperty) {
            this.shortProperty = shortProperty;
        }
    }


}