package softpatrol.drinkapp.network.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by root on 7/1/16.
 */
public class TcpReader {

    private BufferedInputStream ioStream;

    public TcpReader(BufferedInputStream ioStream) {
        this.ioStream = ioStream;
    }

    public short readShort() throws IOException {
        byte[] bytes = new byte[2];
        ioStream.read(bytes,0,2);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getShort();
    }

    /*
    public int readUnsignedShort() throws IOException {
        byte[] bytes = new byte[2];
        ioStream.read(bytes,0,2);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return Short.toUnsignedInt(bb.getShort());
    }

    public Long readUnsignedInt() throws IOException {
        byte[] bytes = new byte[4];
        ioStream.read(bytes,0,4);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return Integer.toUnsignedLong(bb.getInt());
    }
    */

    public int readInt() throws IOException {
        byte[] bytes = new byte[4];
        ioStream.read(bytes,0,4);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getInt();
    }

    public float readFloat() throws IOException {
        byte[] bytes = new byte[4];
        ioStream.read(bytes,0,4);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getFloat();
    }


    public String readAll() throws IOException {
        byte[] bytes = new byte[8192];
        ioStream.read(bytes,0,bytes.length);
        return new String(bytes);
    }


}
