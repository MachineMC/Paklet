package org.machinemc.paklet;

import java.io.OutputStream;
import java.util.function.BiConsumer;

public class DelegateReadOnlyDataVisitor implements DataVisitor {

    private final DataVisitor delegate;

    DelegateReadOnlyDataVisitor(DataVisitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] readBytes(int length) {
        return delegate.readBytes(length);
    }

    @Override
    public void readBytes(byte[] bytes) {
        delegate.readBytes(bytes);
    }

    @Override
    public DataVisitor writeBytes(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> DataVisitor write(Serializer<T> serializer, T object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor write(DataVisitor other, BiConsumer<DataVisitor, DataVisitor> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor write(DataVisitor other, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean readBoolean() {
        return delegate.readBoolean();
    }

    @Override
    public DataVisitor writeBoolean(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte readByte() {
        return delegate.readByte();
    }

    @Override
    public DataVisitor writeByte(byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short readShort() {
        return delegate.readShort();
    }

    @Override
    public DataVisitor writeShort(short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readInt() {
        return delegate.readInt();
    }

    @Override
    public DataVisitor writeInt(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long readLong() {
        return delegate.readLong();
    }

    @Override
    public DataVisitor writeLong(long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char readChar() {
        return delegate.readChar();
    }

    @Override
    public DataVisitor writeChar(char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float readFloat() {
        return delegate.readFloat();
    }

    @Override
    public DataVisitor writeFloat(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double readDouble() {
        return delegate.readDouble();
    }

    @Override
    public DataVisitor writeDouble(double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream asOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeOnly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor readOnly() {
        return this;
    }

    @Override
    public boolean isWriteOnly() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public int readerIndex() {
        return delegate.readerIndex();
    }

    @Override
    public DataVisitor readerIndex(int readerIndex) {
        delegate.readerIndex(readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return delegate.writerIndex();
    }

    @Override
    public DataVisitor writerIndex(int writerIndex) {
        delegate.writerIndex(writerIndex);
        return this;
    }

}
