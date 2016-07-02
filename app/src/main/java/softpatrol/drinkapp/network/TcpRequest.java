package softpatrol.drinkapp.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.network.packet.IncomingError;
import softpatrol.drinkapp.network.packet.IncomingMatchForImage;

/**
 * Created by root on 7/1/16.
 */
public class TcpRequest extends AsyncTask<IByteSerialization, Void, Void> {

    private String ip;
    private int port;
    private ITcpResponse response;
    private static final Map<Short,IPacket> builderRegistry = new HashMap<Short,IPacket>();

    static {
        builderRegistry.put(IncomingError.TAG,new IncomingError());
        builderRegistry.put(IncomingMatchForImage.TAG,new IncomingMatchForImage());
    }

    public TcpRequest(ITcpResponse response,Context ctx) {
        this.response = response;
        String ip = ctx.getResources().getString(R.string.server_vision_ip);
        int port = ctx.getResources().getInteger(R.integer.server_vision_port);
    }

    public TcpRequest(ITcpResponse response,String ip,int port) {
        this.response = response;
        this.port = port;
        this.ip = ip;
    }

    @Override
    protected Void doInBackground(IByteSerialization... iByteSerializations) {
        try {
            Socket clientSocket = new Socket(ip,port);


            byte[] bb = iByteSerializations[0].toByteArray();


            clientSocket.getOutputStream().write(iByteSerializations[0].toByteArray());
            clientSocket.getOutputStream().flush();

            TcpReader tr = new TcpReader(new BufferedInputStream(clientSocket.getInputStream()));
            int packetLength = tr.readInt();
            short tag = tr.readShort();
            IPacket packetBuilder = builderRegistry.get(tag);

            if (packetBuilder == null) {
                response.response(null);
            } else {
                IPacket packet = packetBuilder.build(tr);
                response.response(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.response(null);
        return null;
    }
}
