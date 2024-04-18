package org.machinemc.paklet.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.DataVisitor;

/**
 * Wrapper for another serializer that provides functionality for {@link org.machinemc.paklet.modifiers.Optional}
 * modifier.
 *
 * @param <T> type of wrapped serializer
 */
final class OptionalSerializer<T> implements Serializer<T> {

    private final Serializer<T> wrapped;

    /**
     * New optional serializer wrapper for given serializer.
     *
     * @param of serializer
     */
    public OptionalSerializer(Serializer<T> of) {
        wrapped = of;
    }

    @Override
    public void serialize(SerializerContext context, DataVisitor visitor, @Nullable T t) {
        if (t == null) {
            visitor.writeBoolean(false);
            return;
        }
        visitor.writeBoolean(true);
        visitor.write(context, wrapped, t);
    }

    @Override
    public @Nullable T deserialize(SerializerContext context, DataVisitor visitor) {
        if (!visitor.readBoolean()) return null;
        return visitor.read(context, wrapped);
    }

}
