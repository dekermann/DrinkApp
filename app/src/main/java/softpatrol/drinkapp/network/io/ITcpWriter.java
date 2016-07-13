package softpatrol.drinkapp.network.io;

/**
 * Created by root on 7/14/16.
 */
public interface ITcpWriter extends IByteSerialization {

    ITcpWriter writeShort(short s);
    ITcpWriter writeInt(int i);
    ITcpWriter writeFloat(float f);
    ITcpWriter writeBytes(byte[] bytes);
}
