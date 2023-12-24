package org.machinemc.paklet;

import java.io.InputStream;

class DelegateWriteOnlyDataVisitor implements DataVisitor {

    private final DataVisitor delegate;

    DelegateWriteOnlyDataVisitor(DataVisitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] readBytes(int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readBytes(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeBytes(byte[] bytes) {
        delegate.writeBytes(bytes);
        return this;
    }

    @Override
    public <T> T read(Serializer<T> serializer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean readBoolean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeBoolean(boolean value) {
        delegate.writeBoolean(value);
        return this;
    }

    @Override
    public byte readByte() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeByte(byte value) {
        delegate.writeByte(value);
        return this;
    }

    @Override
    public short readShort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeShort(short value) {
        delegate.writeShort(value);
        return this;
    }

    @Override
    public int readInt() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeInt(int value) {
        delegate.writeInt(value);
        return this;
    }

    @Override
    public long readLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeLong(long value) {
        delegate.writeLong(value);
        return this;
    }

    @Override
    public char readChar() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeChar(char value) {
        delegate.writeChar(value);
        return this;
    }

    @Override
    public float readFloat() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeFloat(float value) {
        delegate.writeFloat(value);
        return this;
    }

    @Override
    public double readDouble() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeDouble(double value) {
        delegate.writeDouble(value);
        return this;
    }

    @Override
    public InputStream asInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataVisitor writeOnly() {
        return this;
    }

    @Override
    public DataVisitor readOnly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWriteOnly() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
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
