package org.machinemc.paklet.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.modifiers.Optional;
import org.machinemc.paklet.modifiers.SerializeWith;

import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Context of current serialization.
 *
 * @param annotatedType type that is being currently serialized
 * @param serializerProvider provider of other serializers
 */
public record SerializerContext(@Nullable AnnotatedType annotatedType, SerializerProvider serializerProvider) {

    /**
     * Creates new copy of the serializer context for given type.
     *
     * @param annotatedType type
     * @return new context
     */
    public SerializerContext withType(AnnotatedType annotatedType) {
        return new SerializerContext(annotatedType, serializerProvider);
    }

    /**
     * Creates new copy of the serializer context for given token.
     *
     * @param token token implementation
     * @return new context
     * @see Token
     */
    public SerializerContext withType(Token<?> token) {
        AnnotatedParameterizedType type = (AnnotatedParameterizedType) token.getClass().getAnnotatedSuperclass();
        return withType(type.getAnnotatedActualTypeArguments()[0]);
    }

    /**
     * Serializes the provided object to the data visitor within given context.
     * <p>
     * This is used for serialization of parameterized types such as List or Map.
     *
     * @param with context to use; for parameters should be used {@link #getContextForParameter(int)}
     * @param visitor visitor to serialize to
     * @param object object to serialize
     * @param <T> object
     */
    @SuppressWarnings("unchecked")
    public static <T> void serializeWith(SerializerContext with, DataVisitor visitor, T object) {
        Serializer<T> serializer = (Serializer<T>) with.serializeWith();
        serializer.serialize(with, visitor, object);
    }

    /**
     * Deserializes the provided object from the data visitor within given context.
     * <p>
     * This is used for deserialization of parameterized types such as List or Map.
     *
     * @param with context to use, {@link #getContextForParameter(int)}
     * @param visitor visitor to deserialize from
     * @return deserialized object
     * @param <T> object
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeWith(SerializerContext with, DataVisitor visitor) {
        Serializer<T> serializer = (Serializer<T>) with.serializeWith();
        return serializer.deserialize(with, visitor);
    }

    /**
     * Returns serializer that is used for serializing type of this context.
     *
     * @return serializer
     */
    public Serializer<?> serializeWith() {
        if (annotatedType == null) throw new UnsupportedOperationException("Currently no type is being serialized");

        Serializer<?> serializer;

        if (!annotatedType.isAnnotationPresent(SerializeWith.class))
            serializer = serializerProvider.getFor(asClass(annotatedType.getType()));
        else
            serializer = serializerProvider.getOf(asClass(annotatedType.getAnnotation(SerializeWith.class).value()));

        if (annotatedType.isAnnotationPresent(Optional.class))
            return new OptionalSerializer<>(serializer);

        return serializer;
    }

    /**
     * Returns context of a parameter of type being serializer.
     * <p>
     * Can be used for correctly serializing parameterized types such as {@link List}.
     *
     * @param index index of the parameter
     * @return context for the parameter
     * @throws UnsupportedOperationException if this type is not parameterized
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public SerializerContext getContextForParameter(int index) {
        if (annotatedType == null) throw new UnsupportedOperationException("Currently no type is being serialized");
        if (!(annotatedType instanceof AnnotatedParameterizedType parameterized))
            throw new UnsupportedOperationException("This Type has to parameters");
        AnnotatedType[] params = parameterized.getAnnotatedActualTypeArguments();
        if (params.length <= index || index < 0) throw new IndexOutOfBoundsException();
        return new SerializerContext(params[index], serializerProvider);
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> asClass(Type type) {
        return (Class<T>) switch (type) {
            case Class<?> cls -> cls;
            case ParameterizedType parameterized -> parameterized.getRawType();
            case GenericArrayType arrayType -> {
                List<Integer> dimensions = new LinkedList<>();
                Type currentType = arrayType;
                do {
                    currentType = ((GenericArrayType) currentType).getGenericComponentType();
                    dimensions.add(0);
                } while (!(currentType instanceof ParameterizedType));
                int[] dimensionsArray = new int[dimensions.size()];
                for (int i = 0; i < dimensionsArray.length; i++)
                    dimensionsArray[i] = dimensions.get(i);
                yield Array.newInstance(asClass(currentType), dimensionsArray).getClass();
            }
            default -> throw new IllegalStateException("Unexpected type: " + type);
        };
    }

}