package l2.client.io;

import java.io.*;
import java.nio.charset.Charset;

public class DataInputStream extends FilterInputStream implements DataInput {
    private final Charset charset;
    private int position;

    public DataInputStream(InputStream in, Charset charset) {
        super(in);
        this.charset = charset;
    }

    public DataInputStream(InputStream in, Charset charset, int position) {
        this(in, charset);
        this.position = position;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public int getPosition() {
        return position;
    }

    private void incCount(int value) {
        int temp = position + value;
        if (temp < 0) {
            temp = Integer.MAX_VALUE;
        }
        position = temp;
    }

    @Override
    public int read() throws IOException {
        int tmp = in.read();
        if (tmp >= 0)
            incCount(1);
        return tmp;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int tmp = in.read(b, off, len);
        if (tmp >= 0)
            incCount(tmp);
        return tmp;
    }

    @Override
    public int readUnsignedByte() throws UncheckedIOException {
        try {
            int tmp = read();
            if (tmp < 0)
                throw new EOFException();
            return tmp;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws UncheckedIOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();

        try {
            int n = 0;
            while (n < len) {
                int count = read(b, off + n, len - n);
                if (count < 0)
                    throw new EOFException();
                n += count;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
