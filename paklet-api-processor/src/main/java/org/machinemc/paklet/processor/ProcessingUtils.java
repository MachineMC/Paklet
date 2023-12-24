package org.machinemc.paklet.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utils for processing classes using annotation processors.
 */
public final class ProcessingUtils {

    private ProcessingUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates mirror of a given class during processing time.
     *
     * @param clazz supplier for class to create mirror for
     * @return mirror of the class
     */
    public static TypeMirror mirror(Supplier<Class<?>> clazz) {
        try {
            var ignored = clazz.get();
            throw new IllegalStateException("Expected a MirroredTypeException to be thrown but got " + ignored);
        } catch (MirroredTypeException exception) {
            return exception.getTypeMirror();
        }
    }

    /**
     * Creates mirror of a given classes during processing time.
     *
     * @param classes supplier for classes to create mirror for
     * @return mirror of the classes
     */
    @SuppressWarnings("unchecked")
    public static List<TypeMirror> mirrorAll(Supplier<Class<?>[]> classes) {
        try {
            var ignored = classes.get();
            throw new IllegalStateException("Expected a MirroredTypesException to be thrown but got " + Arrays.toString(ignored));
        } catch (MirroredTypesException exception) {
            return (List<TypeMirror>) exception.getTypeMirrors();
        }
    }

    /**
     * Returns internal name of given type. Is later compatible with ASM library.
     *
     * @param element type element
     * @return internal name of the type
     */
    public static String getInternalName(TypeElement element) {
        int innerCount = 0;
        Element outer = element;
        while (true) {
            outer = outer.getEnclosingElement();
            if (!(outer instanceof TypeElement)) break;
            innerCount++;
        }
        StringBuilder name = new StringBuilder(element.getQualifiedName().toString());
        for (int i = 0; i < innerCount; i++)
            name.setCharAt(name.lastIndexOf("."), '$');
        return name.toString().replace('.', '/');
    }

}
