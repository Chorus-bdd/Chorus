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
package org.chorusbdd.chorus.websockets.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * Utility methods for Jackson JSON parsing
 */
public class JsonUtils {


    public static String prettyFormat(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            Object json = mapper.readValue(jsonString, Object.class);
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public static String prettyFormat(Object object) {
        String jsonString;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Failed to convert %s to Json",e
            ));
        }
        return jsonString;
    }

    public static <E> E convertToObject(String jsonString, Class<E> clazz) {
        E result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <E> E convertToObject(byte[] json, Class<E> clazz) {
        E result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String,Object> convertToMap(String jsonString) {
        HashMap<String, Object> result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);
            result = mapper.readValue(jsonString, mapType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
