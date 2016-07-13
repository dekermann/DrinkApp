package softpatrol.drinkapp.network.io;

/**
 * Created by root on 7/14/16.
 */
public class ByteWrapper {

    public byte[] bytes;
    public int pos;

    public ByteWrapper(byte[] bytes,int pos) {
        this.bytes = bytes;
        this.pos = pos;
    }
}
