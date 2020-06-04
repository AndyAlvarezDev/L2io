package l2.client.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

public class DataOutputStream extends FilterOutputStream implements DataOutput {
    private final Charset charset;
    private int position;

    public DataOutputStream(OutputStream out, Charset charset) {
        super(out);
        this.charset = charset;
    }

    public DataOutputStream(OutputStream out, Charset charset, int position) {
        this(out, charset);
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

    protected void incCount(int value) {
        int temp = position + value;
        if (temp < 0) {
            temp = Integer.MAX_VALUE;
        }
        position = temp;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);

        incCount(1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);

        incCount(len);
    }

    @Override
    public void writeByte(int b) throws UncheckedIOException {
        try {
            write(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws UncheckedIOException {
        try {
            write(b, off, len);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
