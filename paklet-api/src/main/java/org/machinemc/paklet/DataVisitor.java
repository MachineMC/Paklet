package org.machinemc.paklet;

import org.jetbrains.annotations.Contract;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

/**
 * Source of data user can read from/write to.
 */
public interface DataVisitor {

    byte[] readBytes(int length);

    void readBytes(byte[] bytes);

    @Contract("_ -> this")
    DataVisitor writeBytes(byte[] bytes);

    default <T> T read(Serializer<T> serializer) {
        return serializer.deserialize(this);
    }

    @Contract("_, _ -> this")
    default <T> DataVisitor write(Serializer<T> serializer, T object) {
        serializer.serialize(this, object);
        return this;
    }

    @Contract("_, _ -> this")
    default DataVisitor write(DataVisitor other, BiConsumer</* this */ DataVisitor, /* other */ DataVisitor> consumer) {
        consumer.accept(this, other);
        return this;
    }

    @Contract("_, _ -> this")
    default DataVisitor write(DataVisitor other, int length) {
        writeBytes(other.readBytes(length));
        return this;
    }

    boolean readBoolean();

    @Contract("_ -> this")
    DataVisitor writeBoolean(boolean value);

    byte readByte();

    @Contract("_ -> this")
    DataVisitor writeByte(byte value);

    short readShort();

    @Contract("_ -> this")
    DataVisitor writeShort(short value);

    int readInt();

    @Contract("_ -> this")
    DataVisitor writeInt(int value);

    long readLong();

    @Contract("_ -> this")
    DataVisitor writeLong(long value);

    char readChar();

    @Contract("_ -> this")
    DataVisitor writeChar(char value);

    float readFloat();

    @Contract("_ -> this")
    DataVisitor writeFloat(float value);

    double readDouble();

    @Contract("_ -> this")
    DataVisitor writeDouble(double value);

    @Contract(pure = true)
    default InputStream asInputStream() {
        return new DataVisitorInputStream(this);
    }

    @Contract(pure = true)
    default OutputStream asOutputStream() {
        return new DataVisitorOutputStream(this);
    }

    @Contract(pure = true)
    default DataVisitor writeOnly() {
        return new DelegateWriteOnlyDataVisitor(this);
    }

    @Contract(pure = true)
    default DataVisitor readOnly() {
        return new DelegateReadOnlyDataVisitor(this);
    }

    boolean isWriteOnly();

    boolean isReadOnly();

    int readerIndex();

    @Contract("_ -> this")
    DataVisitor readerIndex(int readerIndex);

    int writerIndex();

    @Contract("_ -> this")
    DataVisitor writerIndex(int writerIndex);

    default byte[] bytes() {
        int reader = readerIndex();
        readerIndex(0);
        byte[] bytes = new byte[writerIndex()];
        readBytes(bytes);
        readerIndex(reader);
        return bytes;
    }

    default byte[] finish() {
        int length = writerIndex();
        int reader = readerIndex();
        byte[] bytes = new byte[length - reader];
        readBytes(bytes);
        return bytes;
    }

}

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