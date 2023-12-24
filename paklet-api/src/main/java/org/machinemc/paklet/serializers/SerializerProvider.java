package org.machinemc.paklet.serializers;

import org.machinemc.paklet.Serializer;

/**
 * Provider of serializers used in current context.
 */
public interface SerializerProvider {

    /**
     * Returns default serializer for given type.
     *
     * @param type type to get the serializer for
     * @return serializer for given type
     * @param <T> type of the serializer
     * @throws NoSuchSerializerException if there is no serializer for given type
     */
    <T> Serializer<T> getFor(Class<T> type) throws NoSuchSerializerException;

    /**
     * Returns registered serializer of given class.
     *
     * @param clazz class of the serializer
     * @return serializer of given class
     * @param <T> serializer
     * @throws NoSuchSerializerException if there is no serializer of given type
     */
    <T extends Serializer<?>> T getOf(Class<T> clazz) throws NoSuchSerializerException;

}
