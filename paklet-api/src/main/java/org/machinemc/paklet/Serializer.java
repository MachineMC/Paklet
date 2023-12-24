package org.machinemc.paklet;

import org.machinemc.paklet.modifiers.SerializeWith;
import org.machinemc.paklet.serializers.DefaultSerializer;
import org.machinemc.paklet.serializers.SerializerContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Serializer for custom packet fields.
 * <p>
 * Additional serializer context can be accessed with {@link #context()}.
 * <p>
 * To mark the serializer as default for fields of a certain type use {@link DefaultSerializer}.
 * <p>
 * To use this serializer for a single field of a packet use {@link SerializeWith}.
 *
 * @param <T> type of the object to serialize
 */
public interface Serializer<T> {

    @ApiStatus.Internal
    ScopedValue<SerializerContext> CONTEXT = ScopedValue.newInstance();

    static SerializerContext context() {
        if (CONTEXT.get() == null)
            throw new IllegalCallerException("Context can not be retrieved from outside of serialization");
        return CONTEXT.get();
    }

    void serialize(DataVisitor visitor, T t);

    T deserialize(DataVisitor visitor);
    
}
