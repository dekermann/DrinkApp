package softpatrol.drinkapp.network.packet;


import java.io.IOException;

import softpatrol.drinkapp.network.TcpReader;

/**
 * Created by root on 7/1/16.
 */
public class IncomingMatchForImage {

    private int descriptorMatches;
    private int matchId;
    private float matchTime;

    public static final short TAG = 5;

    public IncomingMatchForImage(int descriptorMatches,int matchId,float matchTime) {
        this.descriptorMatches = descriptorMatches;
        this.matchId = matchId;
        this.matchTime = matchTime;
    }

    public static IncomingMatchForImage build(TcpReader tcpReader) {
        try {
            int descriptorMatches = tcpReader.readInt();
            int matchId = tcpReader.readInt();
            float matchTime = tcpReader.readFloat();
            return new IncomingMatchForImage(descriptorMatches,matchId,matchTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
