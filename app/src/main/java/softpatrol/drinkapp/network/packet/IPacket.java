package softpatrol.drinkapp.network.packet;

import softpatrol.drinkapp.network.io.TcpReader;

/**
 * Created by root on 7/1/16.
 */
public interface IPacket<E extends IPacket> {

    short getTag();
    E buildPacketFromReader(TcpReader reader);
}
