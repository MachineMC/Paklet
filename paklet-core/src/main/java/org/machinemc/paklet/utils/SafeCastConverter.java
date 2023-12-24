package org.machinemc.paklet.utils;

import org.jetbrains.annotations.Nullable;

public final class SafeCastConverter {

    private SafeCastConverter() {
        throw new UnsupportedOperationException();
    }

    public static @Nullable Object safeCast(Object o, Class<?> clazz) {
        if (o == null) return null;
        return clazz.isAssignableFrom(o.getClass()) ? o : null;
    }

}
