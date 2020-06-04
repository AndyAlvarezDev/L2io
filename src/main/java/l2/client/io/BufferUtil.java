package l2.client.io;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;
import java.security.AccessControlException;

import static l2.client.io.ByteUtil.compactIntToByteArray;

/**
 * @deprecated use {@link RandomAccess#randomAccess(ByteBuffer, String, Charset, int)}
 */
@Deprecated
public class BufferUtil {
    private static final Charset UTF16LE = Charset.forName("utf-16le");

    private static Charset defaultCharset = Charset.forName("EUC-KR");

    static {
        try {
            defaultCharset = Charset.forName(System.getProperty("BufferUtil.defaultCharset", "EUC-KR"));
        } catch (AccessControlException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Charset getDefaultCharset() {
        return defaultCharset;
    }

    public static void setDefaultCharset(Charset defaultCharset) {
        BufferUtil.defaultCharset = defaultCharset;
    }

    public static int getCompactInt(ByteBuffer input) throws BufferUnderflowException {
        return ByteUtil.compactIntFromBytes(() -> input.get() & 0xFF);
    }

    public static void putCompactInt(ByteBuffer buffer, int v) throws BufferOverflowException, ReadOnlyBufferException {
        buffer.put(compactIntToByteArray(v));
    }

    public static String getString(ByteBuffer buffer) {
        return getString(buffer, defaultCharset);
    }

    public static String getString(ByteBuffer buffer, Charset charset) throws BufferUnderflowException {
        int len = getCompactInt(buffer);

        if (len == 0)
            return "";

        byte[] bytes = new byte[len > 0 ? len : -2 * len];
        buffer.get(bytes);
        return new String(bytes, 0, bytes.length - (len > 0 ? 1 : 2), len > 0 ? charset : UTF16LE);
    }

    public static void putString(ByteBuffer buffer, String str) throws BufferOverflowException, ReadOnlyBufferException {
        putString(buffer, str, null);
    }

    public static void putString(ByteBuffer buffer, String str, Charset charset) throws BufferOverflowException, ReadOnlyBufferException {
        if (str == null || str.isEmpty())
            putCompactInt(buffer, 0);
        else if (charset != null)
            putBytes(buffer, str, charset);
        else {
            try {
                if (defaultCharset.newEncoder().canEncode(str)) {
                    putBytes(buffer, str, defaultCharset);
                    return;
                }
            } catch (UnsupportedOperationException ignore) {
            }
            putChars(buffer, str);
        }
    }

    public static void putBytes(ByteBuffer buffer, String str) throws BufferOverflowException, ReadOnlyBufferException {
        putBytes(buffer, str, defaultCharset);
    }

    public static void putBytes(ByteBuffer buffer, String str, Charset charset) throws BufferOverflowException, ReadOnlyBufferException {
        if (str == null || str.isEmpty())
            putCompactInt(buffer, 0);
        else {
            byte[] strBytes = (str + '\0').getBytes(charset);
            putCompactInt(buffer, strBytes.length);
            buffer.put(strBytes);
        }
    }

    public static void putChars(ByteBuffer buffer, String str) throws BufferOverflowException, ReadOnlyBufferException {
        if (str == null || str.isEmpty())
            putCompactInt(buffer, 0);
        else {
            byte[] strBytes = (str + '\0').getBytes(UTF16LE);
            putCompactInt(buffer, -strBytes.length);
            buffer.put(strBytes);
        }
    }

    public static String getUTF(ByteBuffer buffer) throws BufferUnderflowException {
        int len = buffer.getInt();

        if (len < 0)
            throw new IllegalStateException("Invalid string length: " + len);

        if (len == 0)
            return "";

        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return new String(bytes, UTF16LE);
    }

    public static void putUTF(ByteBuffer buffer, String str) throws BufferOverflowException, ReadOnlyBufferException {
        if (str == null || str.isEmpty()) {
            buffer.putInt(0);
        } else {
            byte[] bytes = str.getBytes(UTF16LE);
            buffer.putInt(bytes.length);
            buffer.put(bytes);
        }
    }
}
