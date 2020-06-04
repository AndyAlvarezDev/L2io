package l2.client.io;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteUtilTests {
    @Test
    public void compactInt() {
        assertArrayEquals(new byte[]{(byte) 0b00000000}, ByteUtil.compactIntToByteArray(0));
        assertArrayEquals(new byte[]{(byte) 0b00000001}, ByteUtil.compactIntToByteArray(1));
        assertArrayEquals(new byte[]{(byte) 0b10000001}, ByteUtil.compactIntToByteArray(-1));
        assertArrayEquals(new byte[]{(byte) 0b01000000, (byte) 0b00000001}, ByteUtil.compactIntToByteArray(64));
        assertArrayEquals(new byte[]{(byte) 0b11000000, (byte) 0b00000001}, ByteUtil.compactIntToByteArray(-64));
        assertArrayEquals(new byte[]{(byte) 0b01000000, (byte) 0b10000000, (byte) 0b00000001}, ByteUtil.compactIntToByteArray(8192));
        assertArrayEquals(new byte[]{(byte) 0b11000000, (byte) 0b10000000, (byte) 0b00000001}, ByteUtil.compactIntToByteArray(-8192));
    }

    @Test
    public void uuid() {
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = ByteUtil.uuidToBytes(uuid);
        assertEquals(uuid, ByteUtil.uuidFromBytes(uuidBytes));
    }
}
