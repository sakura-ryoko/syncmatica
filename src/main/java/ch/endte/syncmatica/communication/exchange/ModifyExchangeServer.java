package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.extended_core.PlayerIdentifier;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class ModifyExchangeServer extends AbstractExchange {

    private final ServerPlacement placement;
    UUID placementId;

    public ModifyExchangeServer(final UUID placeId, final ExchangeTarget partner, final Context con) {
        super(partner, con);
        placementId = placeId;
        placement = con.getSyncmaticManager().getPlacement(placementId);
    }

    @Override
    public boolean checkPacket(final SyncmaticaPacketType type, final PacketByteBuf packetBuf) {
        return type.equals(SyncmaticaPacketType.MODIFY_FINISH) && checkUUID(packetBuf, placement.getId());
    }

    @Override
    public void handle(final SyncmaticaPacketType type, final PacketByteBuf packetBuf) {
        packetBuf.readUuid(); // consume uuid
        if (type.equals(SyncmaticaPacketType.MODIFY_FINISH)) {
            getContext().getCommunicationManager().receivePositionData(placement, packetBuf, getPartner());

            final PlayerIdentifier identifier = getContext().getPlayerIdentifierProvider().createOrGet(
                    getPartner()
            );
            placement.setLastModifiedBy(identifier);
            getContext().getSyncmaticManager().updateServerPlacement(placement);
            succeed();
        }
    }

    @Override
    public void init() {
        if (getPlacement() == null || getContext().getCommunicationManager().getModifier(placement) != null) {
            close(true); // equivalent to deny
        } else {
            accept();
        }
    }

    private void accept() {
        // #FIXME
        //final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        //buf.writeUuid(placement.getId());
        //getPartner().sendPacket(PacketType.MODIFY_REQUEST_ACCEPT.identifier, buf, getContext());
        //getContext().getCommunicationManager().setModifier(placement, this);
    }

    @Override
    protected void sendCancelPacket() {
        // #FIXME
        //final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        //buf.writeUuid(placementId);
        //getPartner().sendPacket(PacketType.MODIFY_REQUEST_DENY.identifier, buf, getContext());
    }

    public ServerPlacement getPlacement() {
        return placement;
    }

    @Override
    protected void onClose() {
        if (getContext().getCommunicationManager().getModifier(placement) == this) {
            getContext().getCommunicationManager().setModifier(placement, null);
        }
    }

}
