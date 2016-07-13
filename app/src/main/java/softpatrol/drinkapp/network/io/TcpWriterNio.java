package softpatrol.drinkapp.network.io;

import java.nio.ByteBuffer;

/**
 * Created by root on 7/14/16.
 */
public class TcpWriterNio implements ITcpWriter {

    private ByteBuffer bb;

    public TcpWriterNio(int size) {
        bb = ByteBuffer.allocate(size);
    }

    public ITcpWriter writeShort(short s) {
        checkBufferCapacity(2);
        bb.putShort(s);
        return this;
    }

    public ITcpWriter writeInt(int i) {
        checkBufferCapacity(4);
        bb.putInt(i);
        return this;
    }

    public ITcpWriter writeFloat(float f) {
        checkBufferCapacity(4);
        bb.putFloat(f);
        return this;
    }

    public ITcpWriter writeBytes(byte[] bytes) {
        checkBufferCapacity(bytes.length);
        bb.put(bytes);
        return this;
    }

    public ByteWrapper pack() {
        int length = bb.position() + 4;
        bb.putInt(0,length);
        return new ByteWrapper(bb.array(),bb.position());
    }

    private void checkBufferCapacity(int bytesToWrite) {
        // double the limit if missing bytes
        if (bb.remaining() < bytesToWrite) {
            bb.limit(bb.capacity()*2);
        }
    }
}
