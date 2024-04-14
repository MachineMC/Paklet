package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Serializer;
import org.machinemc.paklet.metadata.FixedLength;
import org.machinemc.paklet.metadata.FloatingRange;
import org.machinemc.paklet.metadata.Length;
import org.machinemc.paklet.metadata.Range;

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
public class Serializers {

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

    @Supports({java.lang.Boolean.class, boolean.class})
    public static class Boolean extends SimpleSerializer<java.lang.Boolean> {
        public Boolean() { super(DataVisitor::writeBoolean, DataVisitor::readBoolean); }
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

    @Supports({java.lang.Byte.class, byte.class})
    public static class Byte extends SimpleNumberSerializer<java.lang.Byte> {
        public Byte() { super(DataVisitor::writeByte, DataVisitor::readByte, false); }
    }

    @Supports({java.lang.Short.class, short.class})
    public static class Short extends SimpleNumberSerializer<java.lang.Short> {
        public Short() { super(DataVisitor::writeShort, DataVisitor::readShort, false); }
    }

    @Supports({java.lang.Integer.class, int.class})
    public static class Integer extends SimpleNumberSerializer<java.lang.Integer> {
        public Integer() { super(DataVisitor::writeInt, DataVisitor::readInt, false); }
    }

    @Supports({java.lang.Long.class, long.class})
    public static class Long extends SimpleNumberSerializer<java.lang.Long> {
        public Long() { super(DataVisitor::writeLong, DataVisitor::readLong, false); }
    }

    @Supports({java.lang.Float.class, float.class})
    public static class Float extends SimpleNumberSerializer<java.lang.Float> {
        public Float() { super(DataVisitor::writeFloat, DataVisitor::readFloat, false); }
    }

    @Supports({java.lang.Double.class, double.class})
    public static class Double extends SimpleNumberSerializer<java.lang.Double> {
        public Double() { super(DataVisitor::writeDouble, DataVisitor::readDouble, false); }
    }

    @Supports({java.lang.Character.class, char.class})
    public static class Character extends SimpleSerializer<java.lang.Character> {
        public Character() { super(DataVisitor::writeChar, DataVisitor::readChar); }
    }

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

    @Supports({
            java.util.Collection.class,
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

            for (Object object : objects)
                SerializerContext.serializeWith(paramContext, visitor, object);
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

            for (int i = 0; i < size; i++)
                objects.add(SerializerContext.deserializeWith(paramContext, visitor));

            return objects;
        }
        private java.util.Collection<Object> createCollectionFromType(@Nullable Type target) {
            if (target instanceof ParameterizedType parameterizedType)
                target = parameterizedType.getRawType();

            java.util.Collection<Object> collection;
            if (target == java.util.Collection.class) collection = new ArrayList<>();
            else if (target == List.class || target == LinkedList.class) collection = new LinkedList<>();
            else if (target == ArrayList.class) collection = new ArrayList<>();
            else if (target == Set.class || target == LinkedHashSet.class) collection = new LinkedHashSet<>();
            else if (target == HashSet.class) collection = new HashSet<>();
            else throw new UnsupportedOperationException("Unsupported type: " + target);
            return collection;
        }
    }

    @Supports(java.util.UUID.class)
    public static class UUID implements Serializer<java.util.UUID> {
        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.util.UUID uuid) {
            visitor.writeLong(uuid.getMostSignificantBits());
            visitor.writeLong(uuid.getLeastSignificantBits());
        }
        @Override
        public java.util.UUID deserialize(SerializerContext context, DataVisitor visitor) {
            return new java.util.UUID(visitor.readLong(), visitor.readLong());
        }
    }

    @Supports(java.time.Instant.class)
    public static class Instant implements Serializer<java.time.Instant> {
        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, java.time.Instant instant) {
            visitor.writeLong(instant.toEpochMilli());
        }
        @Override
        public java.time.Instant deserialize(SerializerContext context, DataVisitor visitor) {
            return java.time.Instant.ofEpochMilli(visitor.readLong());
        }
    }

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
            for (int i = 0; i < length; i++)
                SerializerContext.serializeWith(componentContext, visitor, java.lang.reflect.Array.get(object, i));
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
            for (int i = 0; i < length; i++)
                java.lang.reflect.Array.set(array, i, SerializerContext.deserializeWith(componentContext, visitor));

            return array;
        }
    }

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
