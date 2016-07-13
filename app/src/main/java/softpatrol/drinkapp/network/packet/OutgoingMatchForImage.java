package softpatrol.drinkapp.network.packet;

import softpatrol.drinkapp.network.io.ByteWrapper;
import softpatrol.drinkapp.network.io.IByteSerialization;
import softpatrol.drinkapp.network.io.ITcpWriter;
import softpatrol.drinkapp.network.io.TcpReader;
import softpatrol.drinkapp.network.io.TcpWriterNio;

/**
 * Created by root on 7/1/16.
 */
public class OutgoingMatchForImage implements IByteSerialization,IPacket<OutgoingMatchForImage> {
    public static final short TAG = 2;


    private int width;
    private int height;

    private final int BUFFER_SIZE = 1024*32;

    private byte[] imgData;


    public OutgoingMatchForImage(int w,int h,byte[] imgData) {
        this.width = w;
        this.height = h;
        this.imgData = imgData;
    }

    public OutgoingMatchForImage() {

    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public ByteWrapper pack() {
        ITcpWriter tw = new TcpWriterNio(BUFFER_SIZE);
        tw.writeShort(OutgoingMatchForImage.TAG);
        tw.writeInt(width);
        tw.writeInt(height);
        tw.writeBytes(imgData);
        return tw.pack();
    }

    @Override
    public short getTag() {
        return OutgoingMatchForImage.TAG;
    }

    @Override
    public OutgoingMatchForImage buildPacketFromReader(TcpReader reader) {
        throw new UnsupportedOperationException("The method is not implemented");
    }
}