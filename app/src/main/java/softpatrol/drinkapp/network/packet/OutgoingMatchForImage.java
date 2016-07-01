package softpatrol.drinkapp.network.packet;

import softpatrol.drinkapp.network.IByteSerialization;
import softpatrol.drinkapp.network.TcpWriter;

/**
 * Created by root on 7/1/16.
 */
public class OutgoingMatchForImage implements IByteSerialization {
    public static final short TAG = 2;


    private int width;
    private int height;

    private byte[] imgData;

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
    public byte[] toByteArray() {
        TcpWriter tw = new TcpWriter();
        tw.writeShort(OutgoingMatchForImage.TAG);
        tw.writeInt(width);
        tw.writeInt(height);
        tw.writeBytes(imgData);
        return tw.toByteArray();
    }
}