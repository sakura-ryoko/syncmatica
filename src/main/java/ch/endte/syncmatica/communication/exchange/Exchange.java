package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.network.PacketType;
import net.minecraft.network.PacketByteBuf;

// an exchange represents a portion of a communication with a specific goal 
// that stretches across multiple packages
// an exchange has exactly 2 participants since one of the participants is implied
// to be the vm holding this exchange only the partner has to be noted
// another thing of note is that an Exchange is only one half of the entire exchange
// thus only encompasses half of the entire behavior of an exchange
// e.g. there could be a class receiveData and a class sendData creating
// the 2 halves of a TRANSMIT_DATA_EXCHANGE


public interface Exchange
{
    // uniquely identifies the partner of this exchange
    ExchangeTarget getPartner();

    // in case an exchange starts another exchange they need to be able to reach for
    // the manager
    Context getContext();

    // looks into the received packet and returns
    // whether this exchange handles the packet or not
    // this test should have no side effects.
    // doesn't handle packets directly
    boolean checkPacket(PacketType type, PacketByteBuf packetBuf);

    // handles the data of this specific packet
    void handle(PacketType type, PacketByteBuf packetBuf);

    // marks an exchange that has terminated
    boolean isFinished();

    // marks a successfully finished exchange
    boolean isSuccessful();

    // marks an external unsuccessful close
    // upon a close the exchange might send a cancel packet for this request
    // if the notifyPartner boolean is set to true
    // otherwise this will not happen
    void close(boolean notifyPartner);

    // initializes the actual Exchange
    void init();
}
