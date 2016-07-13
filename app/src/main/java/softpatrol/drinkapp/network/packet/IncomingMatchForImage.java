package softpatrol.drinkapp.network.packet;


import java.io.IOException;

import softpatrol.drinkapp.network.io.TcpReader;

/**
 * Created by root on 7/1/16.
 */
public class IncomingMatchForImage implements IPacket<IncomingMatchForImage> {

    private int descriptorMatches;
    private int matchId;
    private float matchTime;

    public static final short TAG = 5;

    public IncomingMatchForImage(int descriptorMatches,int matchId,float matchTime) {
        this.descriptorMatches = descriptorMatches;
        this.matchId = matchId;
        this.matchTime = matchTime;
    }

    public IncomingMatchForImage(){}

    public IncomingMatchForImage buildPacketFromReader(TcpReader tcpReader) {
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

    @Override
    public short getTag() {
        return IncomingMatchForImage.TAG;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getDescriptorMatches() {
        return descriptorMatches;
    }

    public void setDescriptorMatches(int descriptorMatches) {
        this.descriptorMatches = descriptorMatches;
    }

    public float getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(float matchTime) {
        this.matchTime = matchTime;
    }
}
