package org.machinemc.paklet;

import org.jetbrains.annotations.Contract;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

/**
 * Source of packet data.
 */
public interface DataVisitor {

    /**
     * Reads bytes of N length.
     *
     * @param length length
     * @return bytes
     */
    byte[] readBytes(int length);

    /**
     * Fills byte array with written data.
     *
     * @param bytes array to fill
     */
    void readBytes(byte[] bytes);

    /**
     * Writes bytes to this data visitor.
     *
     * @param bytes bytes to write
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeBytes(byte[] bytes);

    /**
     * Reads next object using given serializer.
     *
     * @param context serialization context
     * @param serializer serializer
     * @return next object
     * @param <T> object type
     */
    default <T> T read(SerializerContext context, Serializer<T> serializer) {
        return serializer.deserialize(context, this);
    }

    /**
     * Writes object using given serializer.
     *
     * @param context serialization context
     * @param serializer serializer
     * @param object object to write
     * @return this
     * @param <T> object type
     */
    @Contract("_, _, _ -> this")
    default <T> DataVisitor write(SerializerContext context, Serializer<T> serializer, T object) {
        serializer.serialize(context, this, object);
        return this;
    }

    /**
     * Writes data from another data visitor.
     *
     * @param other other data visitor
     * @param consumer consumer where first data visitor is this one, and the other one is the provided one
     * @return this
     */
    @Contract("_, _ -> this")
    default DataVisitor write(DataVisitor other, BiConsumer</* this */ DataVisitor, /* other */ DataVisitor> consumer) {
        consumer.accept(this, other);
        return this;
    }

    /**
     * Writes bytes from other data visitor into this one.
     *
     * @param other other data visitor
     * @param length length
     * @return this
     */
    @Contract("_, _ -> this")
    default DataVisitor write(DataVisitor other, int length) {
        writeBytes(other.readBytes(length));
        return this;
    }

    /**
     * Writes all remaining bytes from other data visitor into this one.
     *
     * @param other other data visitor
     * @return this
     */
    @Contract("_ -> this")
    default DataVisitor write(DataVisitor other) {
        writeBytes(other.finish());
        return this;
    }

    /**
     * @return next boolean
     */
    boolean readBoolean();

    /**
     * @param value next boolean
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeBoolean(boolean value);

    /**
     * @return next byte
     */
    byte readByte();

    /**
     * @param value next byte
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeByte(byte value);

    /**
     * @return next short
     */
    short readShort();

    /**
     * @param value next short
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeShort(short value);

    /**
     * @return next int
     */
    int readInt();

    /**
     * @param value next int
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeInt(int value);

    /**
     * @return next long
     */
    long readLong();

    /**
     * @param value next long
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeLong(long value);

    /**
     * @return next char
     */
    char readChar();

    /**
     * @param value next char
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeChar(char value);

    /**
     * @return next float
     */
    float readFloat();

    /**
     * @param value next float
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeFloat(float value);

    /**
     * @return next double
     */
    double readDouble();

    /**
     * @param value next double
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writeDouble(double value);

    /**
     * Returns this data visitor as input stream.
     *
     * @return input stream
     */
    @Contract(pure = true)
    default InputStream asInputStream() {
        return new DataVisitorInputStream(this);
    }

    /**
     * Returns this data visitor as output stream.
     *
     * @return output stream
     */
    @Contract(pure = true)
    default OutputStream asOutputStream() {
        return new DataVisitorOutputStream(this);
    }

    /**
     * @return this data visitor with only write operations allowed
     */
    @Contract(pure = true)
    default DataVisitor writeOnly() {
        return new DelegateWriteOnlyDataVisitor(this);
    }

    /**
     * @return this data visitor with only read operations allowed
     */
    @Contract(pure = true)
    default DataVisitor readOnly() {
        return new DelegateReadOnlyDataVisitor(this);
    }

    /**
     * @return whether this data visitor allows write only operations
     */
    boolean isWriteOnly();

    /**
     * @return whether this data visitor allows read only operations
     */
    boolean isReadOnly();

    /**
     * @return current reader index
     */
    int readerIndex();

    /**
     * @param readerIndex new reader index
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor readerIndex(int readerIndex);

    /**
     * @return writer index
     */
    int writerIndex();

    /**
     * @param writerIndex new writer index
     * @return this
     */
    @Contract("_ -> this")
    DataVisitor writerIndex(int writerIndex);

    /**
     * Returns all bytes written to this buffer from start.
     * <p>
     * To get the remaining bytes use {@link #finish()}.
     *
     * @return all bytes written to this buffer
     */
    default byte[] bytes() {
        int reader = readerIndex();
        readerIndex(0);
        byte[] bytes = new byte[writerIndex()];
        readBytes(bytes);
        readerIndex(reader);
        return bytes;
    }

    /**
     * Returns all remaining bytes.
     *
     * @return remaining bytes
     */
    default byte[] finish() {
        int length = writerIndex();
        int reader = readerIndex();
        byte[] bytes = new byte[length - reader];
        readBytes(bytes);
        return bytes;
    }

}

/**
 * Input stream from data visitor.
 */
class DataVisitorInputStream extends InputStream {

    private final DataVisitor visitor;

    DataVisitorInputStream(DataVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public int read() {
        return Byte.toUnsignedInt(visitor.readByte());
    }

}

/**
 * Output stream from data visitor.
 */
class DataVisitorOutputStream extends OutputStream {

    private final DataVisitor visitor;

    DataVisitorOutputStream(DataVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public void write(int b) {
        visitor.writeByte((byte) b);
    }

}