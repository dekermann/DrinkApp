package softpatrol.drinkapp.network;

/**
 * Created by root on 7/1/16.
 */
public interface IPacket<E extends IPacket> {

    short getTag();
    E build(TcpReader reader);
}
