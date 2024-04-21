package org.machinemc.paklet.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.metadata.FixedLength;
import org.machinemc.paklet.metadata.FloatingRange;
import org.machinemc.paklet.metadata.Length;
import org.machinemc.paklet.metadata.Range;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializers;

import java.io.*;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Collection of default serializers.
 */
public final class Serializers {

    /**
     * Serializer for {@code boolean} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Boolean.class, boolean.class})
    public static class Boolean extends SimpleSerializer<java.lang.Boolean> {
        public Boolean() { super(DataVisitor::writeBoolean, DataVisitor::readBoolean); }
    }

    /**
     * Serializer for {@code byte} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Byte.class, byte.class})
    public static class Byte extends SimpleNumberSerializer<java.lang.Byte> {
        public Byte() { super(DataVisitor::writeByte, DataVisitor::readByte, false); }
    }

    /**
     * Serializer for {@code short} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Short.class, short.class})
    public static class Short extends SimpleNumberSerializer<java.lang.Short> {
        public Short() { super(DataVisitor::writeShort, DataVisitor::readShort, false); }
    }

    /**
     * Serializer for {@code int} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Integer.class, int.class})
    public static class Integer extends SimpleNumberSerializer<java.lang.Integer> {
        public Integer() { super(DataVisitor::writeInt, DataVisitor::readInt, false); }
    }

    /**
     * Serializer for {@code long} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Long.class, long.class})
    public static class Long extends SimpleNumberSerializer<java.lang.Long> {
        public Long() { super(DataVisitor::writeLong, DataVisitor::readLong, false); }
    }

    /**
     * Serializer for {@code float} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Float.class, float.class})
    public static class Float extends SimpleNumberSerializer<java.lang.Float> {
        public Float() { super(DataVisitor::writeFloat, DataVisitor::readFloat, false); }
    }

    /**
     * Serializer for {@code double} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Double.class, double.class})
    public static class Double extends SimpleNumberSerializer<java.lang.Double> {
        public Double() { super(DataVisitor::writeDouble, DataVisitor::readDouble, false); }
    }

    /**
     * Serializer for {@code char} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Character.class, char.class})
    public static class Character extends SimpleSerializer<java.lang.Character> {
        public Character() { super(DataVisitor::writeChar, DataVisitor::readChar); }
    }

    /**
     * Serializer for {@link Number} types.
     * <p>
     * Should be primarily used for BigDecimals and BigIntegers.
     * For other number types, the simple number serializers should
     * be prioritized.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({java.lang.Number.class, BigDecimal.class, BigInteger.class})
    public static class Number implements Serializer<java.lang.Number> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.lang.Number value) {
            AnnotatedType annotatedType = context.annotatedType();
            if (annotatedType != null) {
                checkRange(annotatedType, value.longValue());
                checkFloatingRange(annotatedType, value.doubleValue());
            }
            Serializer<java.lang.String> serializer = context.serializerProvider().getFor(java.lang.String.class);
            visitor.write(context, serializer, value.toString());
        }

        @Override
        public java.lang.Number deserialize(SerializerContext context, DataVisitor visitor) {
            Serializer<java.lang.String> serializer = context.serializerProvider().getFor(java.lang.String.class);
            java.lang.String value = visitor.read(context, serializer);
            java.lang.Number num;
            AnnotatedType annotatedType = context.annotatedType();
            if (annotatedType != null && context.annotatedType().getType() == BigInteger.class) {
                num = new BigInteger(value);
            } else {
                num = new BigDecimal(value);
            }
            if (annotatedType != null) {
                checkRange(annotatedType, num.longValue());
                checkFloatingRange(annotatedType, num.doubleValue());
            }
            return num;
        }

    }

    /**
     * Default string serializer for {@link java.lang.String} type
     * that prefixes all strings with the length using {@link java.lang.Integer}
     * serializer used in the current serialization context and then writes
     * the byte array of the string.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports(java.lang.String.class)
    public static class String implements Serializer<java.lang.String> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.lang.String string) {
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), string.length());
            byte[] bytes = string.getBytes();
            Serializer<java.lang.Integer> serializer = context.serializerProvider().getFor(java.lang.Integer.class);
            visitor.write(context, serializer, bytes.length);
            visitor.writeBytes(bytes);
        }

        @Override
        public java.lang.String deserialize(SerializerContext context, DataVisitor visitor) {
            Serializer<java.lang.Integer> serializer = context.serializerProvider().getFor(java.lang.Integer.class);
            int length = visitor.read(context, serializer);
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), length);
            return new java.lang.String(visitor.readBytes(length));
        }

    }

    /**
     * Default serializer for {@link java.util.Collection} and its
     * mostly used implementations that are part of the JDK.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({
            java.util.Collection.class, SequencedCollection.class,
            List.class, LinkedList.class, ArrayList.class,
            Set.class, LinkedHashSet.class, HashSet.class
    })
    public static class Collection implements Serializer<java.util.Collection<?>> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.util.Collection<?> objects) {
            int size = objects.size();
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), size);

            SerializerContext paramContext = context.getContextForParameter(0);

            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            visitor.write(context, intSerializer, size);

            Serializer<Object> paramSerializer = paramContext.serializeWith();
            for (Object object : objects)
                paramSerializer.serialize(paramContext, visitor, object);
        }

        @Override
        public java.util.Collection<?> deserialize(SerializerContext context, DataVisitor visitor) {
            SerializerContext paramContext = context.getContextForParameter(0);

            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            int size = visitor.read(context, intSerializer);
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), size);

            java.util.Collection<Object> objects;
            if (context.annotatedType() == null) objects = new ArrayList<>();
            else objects = createCollectionFromType(context.annotatedType().getType());

            Serializer<Object> paramSerializer = paramContext.serializeWith();
            for (int i = 0; i < size; i++)
                objects.add(paramSerializer.deserialize(paramContext, visitor));

            return objects;
        }

        private java.util.Collection<Object> createCollectionFromType(@Nullable Type target) {
            if (target instanceof ParameterizedType parameterizedType)
                target = parameterizedType.getRawType();

            java.util.Collection<Object> collection;
            if (target == java.util.Collection.class || target == SequencedCollection.class || target == List.class || target == LinkedList.class) collection = new LinkedList<>();
            else if (target == ArrayList.class) collection = new ArrayList<>();
            else if (target == Set.class || target == LinkedHashSet.class) collection = new LinkedHashSet<>();
            else if (target == HashSet.class) collection = new HashSet<>();
            else throw new UnsupportedOperationException("Unsupported type: " + target);
            return collection;
        }

    }

    /**
     * Default serializer for {@link java.util.Map} and its
     * mostly used implementations that are part of the JDK.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({
            java.util.Map.class, SequencedMap.class,
            HashMap.class, LinkedHashMap.class, TreeMap.class
    })
    public static class Map implements Serializer<java.util.Map<?, ?>> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.util.Map<?, ?> map) {
            int size = map.size();
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), size);

            SerializerContext keyContext = context.getContextForParameter(0);
            SerializerContext valueContext = context.getContextForParameter(1);

            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            visitor.write(context, intSerializer, size);

            Serializer<Object> keySerializer = keyContext.serializeWith();
            Serializer<Object> valueSerializer = valueContext.serializeWith();

            for (java.util.Map.Entry<?, ?> entry : map.entrySet()) {
                keySerializer.serialize(keyContext, visitor, entry.getKey());
                valueSerializer.serialize(valueContext, visitor, entry.getValue());
            }
        }

        @Override
        public java.util.Map<?, ?> deserialize(SerializerContext context, DataVisitor visitor) {
            SerializerContext keyContext = context.getContextForParameter(0);
            SerializerContext valueContext = context.getContextForParameter(1);

            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            int size = visitor.read(context, intSerializer);
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), size);

            java.util.Map<Object, Object> map;
            if (context.annotatedType() == null) map = new LinkedHashMap<>();
            else map = createMapFromType(context.annotatedType().getType());

            Serializer<Object> keySerializer = keyContext.serializeWith();
            Serializer<Object> valueSerializer = valueContext.serializeWith();

            for (int i = 0; i < size; i++)
                map.put(
                        keySerializer.deserialize(keyContext, visitor),
                        valueSerializer.deserialize(keyContext, visitor)
                );

            return map;
        }

        private java.util.Map<Object, Object> createMapFromType(@Nullable Type target) {
            if (target instanceof ParameterizedType parameterizedType)
                target = parameterizedType.getRawType();

            java.util.Map<Object, Object> map;
            if (target == java.util.Map.class || target == SequencedMap.class || target == LinkedHashMap.class) map = new LinkedHashMap<>();
            else if (target == HashMap.class) map = new HashMap<>();
            else if (target == TreeMap.class) map = new TreeMap<>();
            else throw new UnsupportedOperationException("Unsupported type: " + target);
            return map;
        }

    }

    /**
     * Serializer for {@link java.util.UUID} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports(java.util.UUID.class)
    public static class UUID extends SimpleSerializer<java.util.UUID> {

        public UUID() {
            super(
                    (visitor, uuid) -> {
                        visitor.writeLong(uuid.getMostSignificantBits());
                        visitor.writeLong(uuid.getLeastSignificantBits());
                    },
                    visitor -> new java.util.UUID(visitor.readLong(), visitor.readLong())
            );
        }

    }

    /**
     * Serializer for {@link Instant} types.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports(java.time.Instant.class)
    public static class Instant extends SimpleSerializer<java.time.Instant> {

        public Instant() {
            super(
                    (visitor, instant) -> visitor.writeLong(instant.toEpochMilli()),
                    visitor -> java.time.Instant.ofEpochMilli(visitor.readLong())
            );
        }

    }

    /**
     * Serializer for {@link java.util.BitSet} types.
     * <p>
     * For bit sets without fixed length, a packed representation of the bit set
     * is created using {@link java.util.BitSet#toLongArray()} and written to the
     * data visitor.
     * <p>
     * Bit sets with fixed length of n bits are encoded as {@code ceil(n / 8)} bytes.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports(java.util.BitSet.class)
    public static class BitSet implements Serializer<java.util.BitSet> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.util.BitSet bitSet) {
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), bitSet.length());

            if (context.annotatedType() != null && context.annotatedType().isAnnotationPresent(FixedLength.class)) {
                int length = context.annotatedType().getAnnotation(FixedLength.class).value();
                if (bitSet.length() > length)
                    throw new IllegalArgumentException("BitSet is larger than expected size");
                visitor.writeBytes(Arrays.copyOf(bitSet.toByteArray(), -Math.floorDiv(-length, 8)));
                return;
            }

            long[] data = bitSet.toLongArray();
            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            visitor.write(context, intSerializer, data.length);
            for (long l : data) visitor.writeLong(l);
        }

        @Override
        public java.util.BitSet deserialize(SerializerContext context, DataVisitor visitor) {
            if (context.annotatedType() != null && context.annotatedType().isAnnotationPresent(FixedLength.class)) {
                int length = context.annotatedType().getAnnotation(FixedLength.class).value();
                java.util.BitSet bitSet = java.util.BitSet.valueOf(visitor.readBytes(-Math.floorDiv(-length, 8)));
                checkLength(context.annotatedType(), bitSet.length());
                return bitSet;
            }

            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            long[] data = new long[visitor.read(context, intSerializer)];
            for (int i = 0; i < data.length; i++) data[i] = visitor.readLong();
            java.util.BitSet bitSet = java.util.BitSet.valueOf(data);
            if (context.annotatedType() != null)
                checkLength(context.annotatedType(), bitSet.length());
            return bitSet;
        }

    }

    /**
     * Serializer for {@link java.lang.Enum} types, where each constant
     * is represented by its ordinal index using the integer serializer
     * in the current serialization context.
     * <p>
     * Have to be either specified using {@link org.machinemc.paklet.modifiers.SerializeWith}
     * or using {@link org.machinemc.paklet.serialization.rule.EnumSerializationRule}.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({}) // has to be specified using @SerializeWith or serialization rule
    public static class Enum implements Serializer<java.lang.Enum<?>> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.lang.Enum<?> value) {
            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            visitor.write(context, intSerializer, value.ordinal());
        }

        @Override
        public java.lang.Enum<?> deserialize(SerializerContext context, DataVisitor visitor) {
            if (context.annotatedType() == null) throw new UnsupportedOperationException();
            try {
                Class<java.lang.Enum<?>> clazz = SerializerContext.asClass(context.annotatedType().getType());
                Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
                return clazz.getEnumConstants()[visitor.read(context, intSerializer)];
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

    }

    /**
     * Serializer for array types, where each array is prefixed with
     * its length using the integer serializer in the current serialization
     * context and then its contents are written using the serializer of their
     * component type.
     * <p>
     * Have to be either specified using {@link org.machinemc.paklet.modifiers.SerializeWith}
     * or using {@link org.machinemc.paklet.serialization.rule.ArraySerializationRule}.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({}) // has to be specified using @SerializeWith or serialization rule
    public static class Array implements Serializer<Object> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, Object object) {
            if (!object.getClass().isArray()) throw new UnsupportedOperationException();
            if (context.annotatedType() == null) throw new UnsupportedOperationException();

            int length = java.lang.reflect.Array.getLength(object);
            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            visitor.write(context, intSerializer, length);

            AnnotatedType componentType = ((AnnotatedArrayType) context.annotatedType()).getAnnotatedGenericComponentType();
            SerializerContext componentContext = context.withType(componentType);

            Serializer<Object> componentSerializer = componentContext.serializeWith();
            for (int i = 0; i < length; i++)
                componentSerializer.serialize(componentContext, visitor, java.lang.reflect.Array.get(object, i));
        }

        @Override
        public Object deserialize(SerializerContext context, DataVisitor visitor) {
            if (context.annotatedType() == null) throw new UnsupportedOperationException();

            Serializer<java.lang.Integer> intSerializer = context.serializerProvider().getFor(java.lang.Integer.class);
            int length = visitor.read(context, intSerializer);

            Class<?> clazz = SerializerContext.asClass(context.annotatedType().getType());
            Object array = java.lang.reflect.Array.newInstance(clazz.getComponentType(), length);

            AnnotatedType componentType = ((AnnotatedArrayType) context.annotatedType()).getAnnotatedGenericComponentType();
            SerializerContext componentContext = context.withType(componentType);

            Serializer<Object> componentSerializer = componentContext.serializeWith();
            for (int i = 0; i < length; i++)
                java.lang.reflect.Array.set(array, i, componentSerializer.deserialize(componentContext, visitor));

            return array;
        }

    }

    /**
     * Serializer for types implementing {@link java.io.Serializable} interface.
     * <p>
     * Have to be either specified using {@link org.machinemc.paklet.modifiers.SerializeWith}
     * or using {@link org.machinemc.paklet.serialization.rule.SerializableSerializationRule}.
     */
    @DefaultSerializer(DefaultSerializers.class)
    @Supports({}) // has to be specified using @SerializeWith or serialization rule
    public static class Serializable implements Serializer<java.io.Serializable> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.io.Serializable serializable) {
            try {
                try (ObjectOutputStream oos = new ObjectOutputStream(visitor.asOutputStream())) {
                    oos.writeObject(serializable);
                    oos.flush();
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        @Override
        public java.io.Serializable deserialize(SerializerContext context, DataVisitor visitor) {
            try {
                try (ObjectInputStream ois = new ObjectInputStream(visitor.asInputStream())) {
                    return (java.io.Serializable) ois.readObject();
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

    }

    private static class SimpleSerializer<T> implements Serializer<T> {

        final BiConsumer<DataVisitor, T> serialize;
        final Function<DataVisitor, T> deserialize;

        public SimpleSerializer(BiConsumer<DataVisitor, T> serialize, Function<DataVisitor, T> deserialize) {
            this.serialize = serialize;
            this.deserialize = deserialize;
        }

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, T t) {
            serialize.accept(visitor, t);
        }

        @Override
        public T deserialize(SerializerContext context, DataVisitor visitor) {
            return deserialize.apply(visitor);
        }

    }

    private static class SimpleNumberSerializer<T extends java.lang.Number> extends SimpleSerializer<T> {

        final boolean floatingPoint;

        public SimpleNumberSerializer(BiConsumer<DataVisitor, T> serialize,
                                      Function<DataVisitor, T> deserialize,
                                      boolean floatingPoint) {
            super(serialize, deserialize);
            this.floatingPoint = floatingPoint;
        }

        @Override
        public T deserialize(SerializerContext context, DataVisitor visitor) {
            T num = super.deserialize(context, visitor);
            AnnotatedType annotatedType = context.annotatedType();
            if (annotatedType != null) {
                checkRange(annotatedType, num.longValue());
                if (floatingPoint) checkFloatingRange(annotatedType, num.doubleValue());
            }
            return num;
        }

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, T t) {
            AnnotatedType annotatedType = context.annotatedType();
            if (annotatedType != null) {
                checkRange(annotatedType, t.longValue());
                if (floatingPoint) checkFloatingRange(annotatedType, t.doubleValue());
            }
            super.serialize(context, visitor, t);
        }

    }

    private Serializers() {
        throw new UnsupportedOperationException();
    }

    private static void checkRange(AnnotatedType type, long value) {
        Range range = type.getAnnotation(Range.class);
        if (range == null) return;
        isInRange(value, range.inclusive(), range.min(), range.max());
    }

    private static void checkFloatingRange(AnnotatedType type, double value) {
        FloatingRange range = type.getAnnotation(FloatingRange.class);
        if (range == null) return;
        isInRange(value, range.inclusive(), range.min(), range.max());
    }

    private static void isInRange(double value, boolean inclusive, double min, double max) {
        if ((inclusive && min <= value && value <= max) || (!inclusive && min < value && value < max))
            return;
        throw new IllegalArgumentException("Value out of bounds, got " + value + ", expected "
                + "value between " + min + " and " + max + ", "
                + (inclusive ? "inclusive" : "exclusive")
        );
    }

    private static void checkLength(AnnotatedType type, int length) {
        FixedLength fLength = type.getAnnotation(FixedLength.class);
        if (fLength != null && length != fLength.value())
            throw new IllegalArgumentException("Expected length " + fLength.value() + ", got " + length);
        Length rLength = type.getAnnotation(Length.class);
        if (rLength != null && (rLength.max() < length || rLength.min() > length))
            throw new IllegalArgumentException("Expected length between " + rLength.min() + " and " + rLength.max() + ", got " + length);
    }

}
