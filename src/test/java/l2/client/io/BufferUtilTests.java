package l2.client.io;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class BufferUtilTests {
    @Test
    public void compactInt() {
        ByteBuffer buffer = ByteBuffer.allocate(5);

        testCompactInt(buffer, Integer.MIN_VALUE);
        testCompactInt(buffer, 0);
        testCompactInt(buffer, Integer.MAX_VALUE);

        for (int i = 2; i < 32; i += 7) {
            testCompactInt(buffer, 1 << i);
            testCompactInt(buffer, -(1 << i));
        }
    }

    private void testCompactInt(ByteBuffer buffer, int value) {
        buffer.clear();
        BufferUtil.putCompactInt(buffer, value);
        buffer.flip();
        assertEquals(value, BufferUtil.getCompactInt(buffer));
    }

    @Test
    public void string() {
        ByteBuffer buffer = ByteBuffer.allocate(20);

        buffer.clear();
        BufferUtil.putString(buffer, null);
        buffer.flip();
        assertEquals("", BufferUtil.getString(buffer));

        String[] strings = new String[]{
                "",
                "ascii",
                "русский",
                "한국어"
        };

        for (String string : strings) {
            buffer.clear();
            BufferUtil.putString(buffer, string);
            buffer.flip();
            assertEquals(string, BufferUtil.getString(buffer));
        }
    }

    @Test
    public void utf() {
        ByteBuffer buffer = ByteBuffer.allocate(20);

        buffer.clear();
        BufferUtil.putUTF(buffer, null);
        buffer.flip();
        assertEquals("", BufferUtil.getUTF(buffer));

        String[] strings = new String[]{
                "",
                "ascii",
                "русский",
                "한국어"
        };

        for (String string : strings) {
            buffer.clear();
            BufferUtil.putUTF(buffer, string);
            buffer.flip();
            assertEquals(string, BufferUtil.getUTF(buffer));
        }
    }
}
