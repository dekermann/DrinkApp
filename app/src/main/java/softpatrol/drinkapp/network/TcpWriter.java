package softpatrol.drinkapp.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/1/16.
 *
 * Assumemes MSB
 */
public class TcpWriter implements IByteSerialization {

    private List<Byte> bytesBuffer;

    public TcpWriter() {
        bytesBuffer = new ArrayList<Byte>();
    }

    public TcpWriter writeShort(short s) {
        byte[] shortbytes = ByteBuffer.allocate(2).putShort(s).array();
        appendToByteBuffer(shortbytes);
        return this;
    }

    public TcpWriter writeInt(int i) {
        byte[] intBytes = ByteBuffer.allocate(4).putInt(i).array();
        appendToByteBuffer(intBytes);
        return this;
    }

    public TcpWriter writeFloat(float f) {
        byte[] floatBytes = ByteBuffer.allocate(4).putFloat(f).array();
        appendToByteBuffer(floatBytes);
        return this;
    }

    public TcpWriter writeBytes(byte[] bytes) {
        appendToByteBuffer(bytes);
        return this;
    }

    /*
    * TODO: I die on the inside...
     */
    private void appendToByteBuffer(byte[] bytes) {
        for (byte b : bytes) {
            bytesBuffer.add(b);
        }
    }

    /*
    * TODO: I die on the inside...
     */
    private void prependToByteBuffer(byte[] bytes) {
        int order = 0;
        for (byte b : bytes) {
            bytesBuffer.add(order++,b);
        }
    }

    @Override
    public byte[] toByteArray() {
        int length = bytesBuffer.size() + 4;
        byte[] intBytes = ByteBuffer.allocate(4).putInt(length).array();

        byte[] total = new byte[length];

        for (int j = 0;j < intBytes.length;j++) {
            total[j] = intBytes[j];
        }

        int dataStart = intBytes.length;

        for (int i = dataStart;i < total.length;i++) {
            total[i] = bytesBuffer.get(i-dataStart);
        }
        return total;
    }
}
