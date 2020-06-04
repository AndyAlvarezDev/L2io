package l2.client.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;

public interface RandomAccess extends DataInput, DataOutput, AutoCloseable {
    String getName();

    void setPosition(int position) throws UncheckedIOException;

    void trimToPosition() throws UncheckedIOException;

    RandomAccess openNewSession(boolean readOnly) throws UncheckedIOException;

    void close() throws UncheckedIOException;

    static RandomAccess randomAccess(ByteBuffer buffer, String name, Charset charset, int position) {
        return new RandomAccess() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Charset getCharset() {
                return charset;
            }

            @Override
            public int getPosition() {
                return position + buffer.position();
            }

            @Override
            public void setPosition(int pos) throws IllegalArgumentException {
                buffer.position(pos - position);
            }

            @Override
            public void trimToPosition() {
                buffer.limit(buffer.position());
            }

            @Override
            public int readUnsignedByte() throws UncheckedIOException {
                try {
                    return buffer.get() & 0xff;
                } catch (BufferUnderflowException e) {
                    throw new UncheckedIOException(new IOException(e));
                }
            }

            @Override
            public void writeByte(int b) throws UncheckedIOException {
                try {
                    buffer.put((byte) b);
                } catch (BufferOverflowException | ReadOnlyBufferException e) {
                    throw new UncheckedIOException(new IOException(e));
                }
            }

            @Override
            public RandomAccess openNewSession(boolean readOnly) throws UncheckedIOException {
                return this;
            }

            @Override
            public void close() {
            }
        };
    }
}
