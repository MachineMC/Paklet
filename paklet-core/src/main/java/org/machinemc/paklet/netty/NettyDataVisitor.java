package org.machinemc.paklet.netty;

import io.netty.buffer.ByteBuf;
import org.machinemc.paklet.DataVisitor;

public class NettyDataVisitor implements DataVisitor {

    private final ByteBuf delegate;

    public NettyDataVisitor(ByteBuf buf) {
        delegate = buf;
    }

    @Override
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        delegate.readBytes(bytes);
        return bytes;
    }

    @Override
    public void readBytes(byte[] bytes) {
        delegate.readBytes(bytes);
    }

    @Override
    public DataVisitor writeBytes(byte[] bytes) {
        delegate.writeBytes(bytes);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return delegate.readBoolean();
    }

    @Override
    public DataVisitor writeBoolean(boolean value) {
        delegate.writeBoolean(value);
        return this;
    }

    @Override
    public byte readByte() {
        return delegate.readByte();
    }

    @Override
    public DataVisitor writeByte(byte value) {
        delegate.writeByte(value);
        return null;
    }

    @Override
    public short readShort() {
        return delegate.readShort();
    }

    @Override
    public DataVisitor writeShort(short value) {
        delegate.writeShort(value);
        return this;
    }

    @Override
    public int readInt() {
        return delegate.readInt();
    }

    @Override
    public DataVisitor writeInt(int value) {
        delegate.writeInt(value);
        return this;
    }

    @Override
    public long readLong() {
        return delegate.readLong();
    }

    @Override
    public DataVisitor writeLong(long value) {
        delegate.writeLong(value);
        return this;
    }

    @Override
    public char readChar() {
        return delegate.readChar();
    }

    @Override
    public DataVisitor writeChar(char value) {
        delegate.writeChar(value);
        return this;
    }

    @Override
    public float readFloat() {
        return delegate.readFloat();
    }

    @Override
    public DataVisitor writeFloat(float value) {
        delegate.writeFloat(value);
        return this;
    }

    @Override
    public double readDouble() {
        return delegate.readDouble();
    }

    @Override
    public DataVisitor writeDouble(double value) {
        delegate.writeDouble(value);
        return this;
    }

    @Override
    public boolean isWriteOnly() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
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
