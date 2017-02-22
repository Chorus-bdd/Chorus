package org.chorusbdd.chorus.stepserver.util;

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
