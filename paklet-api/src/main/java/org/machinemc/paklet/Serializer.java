package org.machinemc.paklet;

import org.machinemc.paklet.modifiers.SerializeWith;
import org.machinemc.paklet.serializers.DefaultSerializer;
import org.machinemc.paklet.serializers.SerializerContext;

/**
 * Serializer for custom packet fields.
 * <p>
 * To mark the serializer as default for fields of a certain type use {@link DefaultSerializer} annotation.
 * <p>
 * To use serializer for a single field of a packet use {@link SerializeWith} or its annotation alias.
 *
 * @param <T> type of the object to serialize
 */
public interface Serializer<T> {

    /**
     * Serializes the given value to the data visitor.
     *
     * @param context serialization context
     * @param visitor visitor
     * @param t value to serialize
     */
    void serialize(SerializerContext context, DataVisitor visitor, T t);

    /**
     * Deserializes a value from the data visitor.
     *
     * @param context serialization context
     * @param visitor visitor
     * @return deserialized value
     */
    T deserialize(SerializerContext context, DataVisitor visitor);
    
}
