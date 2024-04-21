package org.machinemc.paklet.utils;

import org.jetbrains.annotations.Nullable;

/**
 * Converter that checks whether it is safe to cast.
 */
public final class SafeCastConverter {

    private SafeCastConverter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether object {@code o} can be cased to given class.
     *
     * @param o object
     * @param clazz class
     * @return given object or null if the object can not be cast safely
     */
    public static @Nullable Object safeCast(Object o, Class<?> clazz) {
        if (o == null) return null;
        return clazz.isAssignableFrom(o.getClass()) ? o : null;
    }

}
