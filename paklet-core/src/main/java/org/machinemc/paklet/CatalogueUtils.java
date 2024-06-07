package org.machinemc.paklet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Utilities for operation with catalogues system.
 */
final class CatalogueUtils {

    private CatalogueUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns json for catalogue of given class.
     *
     * @param catalogueClass catalogue class
     * @param resourcesAccessor function that accesses the resources stream from given path
     * @return json catalogue
     */
    public static JsonObject getCatalogueForClass(Class<?> catalogueClass, Function<String, InputStream> resourcesAccessor) {
        String path = "/" + catalogueClass.getPackageName().replace('.', '/');
        String fileName = catalogueClass.getCanonicalName().replace(catalogueClass.getPackageName() + ".", "");
        path = path + "/" + fileName + "_catalogue.json";
        try (InputStream is = resourcesAccessor.apply(path)) {
            if (is == null) throw new NullPointerException("There is no catalogue data for class " + catalogueClass.getName());
            return JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Returns all classes in a catalogue under given identifier.
     *
     * @param catalogueClass catalogue class
     * @param type group identifier, can be either {@code packets}, {@code serializers}, or {@code rules}
     * @param resourcesAccessor function that accesses the resources stream from given path
     * @return list of found classes
     * @throws ClassNotFoundException if one of the classes do not exist
     */
    public static List<Class<?>> getClassesOfCatalogue(Class<?> catalogueClass,
                                                       String type,
                                                       Function<String, InputStream> resourcesAccessor) throws ClassNotFoundException {
        JsonObject json = getCatalogueForClass(catalogueClass, resourcesAccessor);
        JsonArray array = json.getAsJsonArray(type);
        List<Class<?>> classes = new ArrayList<>();
        for (JsonElement element : array) {
            String className = element.getAsString().replace('/', '.');
            classes.add(Class.forName(className));
        }
        return classes;
    }

}
