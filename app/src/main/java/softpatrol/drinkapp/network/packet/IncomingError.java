package softpatrol.drinkapp.network.packet;


import java.io.IOException;

import softpatrol.drinkapp.network.TcpReader;

/**
 * Created by root on 7/1/16.
 */
public class IncomingError {

    public static final short TAG = 666;

    private short errorCode;
    private String msg;


    public IncomingError(short errorCode, String msg) {
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public IncomingError() {}

    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static IncomingError build(TcpReader tcpReader) {
        try {
            short errorCode = tcpReader.readShort();
            String msg = tcpReader.readAll();
            return new IncomingError(errorCode,msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
