package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Serializer;

/**
 * Wrapper for another serializer that provides functionality for {@link org.machinemc.paklet.modifiers.Optional}.
 * @param <T> type of wrapped serializer
 */
class OptionalSerializer<T> implements Serializer<T> {

    private final Serializer<T> wrapped;

    public OptionalSerializer(Serializer<T> of) {
        wrapped = of;
    }

    @Override
    public void serialize(DataVisitor visitor, @Nullable T t) {
        if (t == null) {
            visitor.writeBoolean(false);
            return;
        }
        visitor.writeBoolean(true);
        visitor.write(wrapped, t);
    }

    @Override
    public @Nullable T deserialize(DataVisitor visitor) {
        if (!visitor.readBoolean()) return null;
        return visitor.read(wrapped);
    }

}
