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

import org.chorusbdd.chorus.util.function.BiPredicate;

import java.util.Map;
import java.util.Properties;

/**
 * Created by Nick E on 09/01/2015.
 *
 * Apply a function to convert the keys from a source Properties object into a Properties instance
 * with modified keys
 */
class FilteringPropertyLoader implements PropertyLoader {

    private PropertyLoader wrappedLoader;
    private BiPredicate<String,String> predicate;

    public FilteringPropertyLoader(PropertyLoader wrappedLoader, BiPredicate<String, String> predicate) {
        this.wrappedLoader = wrappedLoader;
        this.predicate = predicate;
    }

    public Properties loadProperties() {
        Properties p = wrappedLoader.loadProperties();
        Properties dest = new Properties();
        for ( Map.Entry<Object,Object> mapEntry : p.entrySet()) {
            if(predicate.test(mapEntry.getKey().toString(), mapEntry.getValue().toString())) {
                dest.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }
        return dest;
    }
}
