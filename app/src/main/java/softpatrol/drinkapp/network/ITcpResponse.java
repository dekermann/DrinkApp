package softpatrol.drinkapp.network;

import softpatrol.drinkapp.network.packet.IPacket;

/**
 * Created by root on 7/1/16.
 */
public interface ITcpResponse {

    void response(IPacket packet);
}
