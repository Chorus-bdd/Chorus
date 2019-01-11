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
package org.chorusbdd.chorus.util.properties;

import java.util.*;

/**
 * Created by nick on 12/01/15.
 */
class CompositePropertyLoader implements PropertyLoader {

    private LinkedList<PropertyLoader> childLoaders = new LinkedList<>();

    public CompositePropertyLoader(PropertyLoader... propertyLoaders) {
        this(Arrays.asList(propertyLoaders));
    }

    public CompositePropertyLoader( Collection<PropertyLoader> loaders ) {
        childLoaders.addAll(loaders);
    }

    public CompositePropertyLoader() {
    }

    public void add(PropertyLoader propertyLoader) {
        childLoaders.add(propertyLoader);
    }

    public void addAll(List<PropertyLoader> propertyLoaders) {
        childLoaders.addAll(propertyLoaders);
    }

    @Override
    public Properties loadProperties()  {
        Properties result = new Properties();
        for( PropertyLoader p : childLoaders) {
            result.putAll(p.loadProperties());
        }
        return result;
    }
}
