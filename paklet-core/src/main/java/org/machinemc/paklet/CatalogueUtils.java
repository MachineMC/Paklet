package org.machinemc.paklet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for operation with catalogues system.
 */
final class CatalogueUtils {

    private CatalogueUtils() {
        throw new UnsupportedOperationException();
    }

    public static JsonObject getCatalogueForClass(Class<?> catalogueClass) {
        String path = "/" + catalogueClass.getName().replace('.', '/') + "_catalogue.json";
        try (InputStream is = SerializerProviderImpl.class.getResourceAsStream(path)) {
            if (is == null) throw new NullPointerException();
            return JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static List<Class<?>> getClassesOfCatalogue(Class<?> catalogueClass, String type) throws ClassNotFoundException {
        JsonObject json = getCatalogueForClass(catalogueClass);
        JsonArray array = json.getAsJsonArray(type);
        List<Class<?>> classes = new ArrayList<>();
        for (JsonElement element : array) {
            String className = element.getAsString().replace('/', '.');
            classes.add(Class.forName(className));
        }
        return classes;
    }

}
