package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.features.Feature;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.Message;
import net.minecraft.network.PacketByteBuf;

public class ModifyExchangeClient extends AbstractExchange {

    //bad practice but valid for communication with deprecated systems
    private boolean expectRemove = false;

    private final ServerPlacement placement;
    private final SchematicPlacement litematic;

    public ModifyExchangeClient(final ServerPlacement placement, final ExchangeTarget partner, final Context con) {
        super(partner, con);
        this.placement = placement;
        litematic = LitematicManager.getInstance().schematicFromSyncmatic(placement);
    }

    @Override
    public boolean checkPacket(final SyncmaticaPacketType type, final PacketByteBuf packetBuf) {
        if (type.equals(SyncmaticaPacketType.MODIFY_REQUEST_DENY)
                || type.equals(SyncmaticaPacketType.MODIFY_REQUEST_ACCEPT)
                || (expectRemove && type.equals(SyncmaticaPacketType.REMOVE_SYNCMATIC))) {
            return AbstractExchange.checkUUID(packetBuf, placement.getId());
        }
        return false;
    }

    @Override
    public void handle(final SyncmaticaPacketType type, final PacketByteBuf packetBuf) {
        if (type.equals(SyncmaticaPacketType.MODIFY_REQUEST_DENY)) {
            packetBuf.readUuid();
            close(false);
            if (!litematic.isLocked()) {
                litematic.setOrigin(placement.getPosition(), null);
                litematic.setRotation(placement.getRotation(), null);
                litematic.setMirror(placement.getMirror(), null);
                litematic.toggleLocked();
            }
            ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.SUCCESS, "syncmatica.error.modification_deny"));
        } else if (type.equals(SyncmaticaPacketType.MODIFY_REQUEST_ACCEPT)) {
            packetBuf.readUuid();
            acceptModification();
        } else if (type.equals(SyncmaticaPacketType.REMOVE_SYNCMATIC)) {
            packetBuf.readUuid();
            final ShareLitematicExchange legacyModify = new ShareLitematicExchange(litematic, getPartner(), getContext(), placement);
            getContext().getCommunicationManager().startExchange(legacyModify);
            succeed(); // the adding portion of this is handled by the ShareLitematicExchange
        }
    }

    @Override
    public void init() {
        if (getContext().getCommunicationManager().getModifier(placement) != null) {
            close(false);
            return;
        }
        getContext().getCommunicationManager().setModifier(placement, this);
        if (getPartner().getFeatureSet().hasFeature(Feature.MODIFY)) {
            // #FIXME
            //final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            //buf.writeUuid(placement.getId());
            //getPartner().sendPacket(PacketType.MODIFY_REQUEST.identifier, buf, getContext());
        } else {
            acceptModification();
        }
    }

    private void acceptModification() {
        if (litematic.isLocked()) {
            litematic.toggleLocked();
        }
        ScreenHelper.ifPresent(s -> s.addMessage(Message.MessageType.SUCCESS, "syncmatica.success.modification_accepted"));
        getContext().getSyncmaticManager().updateServerPlacement(placement);
    }

    public void conclude() {
        LitematicManager.getInstance().updateServerPlacement(litematic, placement);
        sendFinish();
        if (!litematic.isLocked()) {
            litematic.toggleLocked();
        }
        getContext().getSyncmaticManager().updateServerPlacement(placement);
    }

    private void sendFinish() {
        if (getPartner().getFeatureSet().hasFeature(Feature.MODIFY)) {
            // #FIXME
            //final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            //buf.writeUuid(placement.getId());
            //getContext().getCommunicationManager().putPositionData(placement, buf, getPartner());
            //getPartner().sendPacket(PacketType.MODIFY_FINISH.identifier, buf, getContext());
            //succeed();
            //getContext().getCommunicationManager().notifyClose(this);
        } else {
            // #FIXME
            //final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            //buf.writeUuid(placement.getId());
            //getPartner().sendPacket(PacketType.REMOVE_SYNCMATIC.identifier, buf, getContext());
            //expectRemove = true;
        }
    }

    @Override
    protected void sendCancelPacket() {
        if (getPartner().getFeatureSet().hasFeature(Feature.MODIFY)) {
            // #FIXME
            //final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            //buf.writeUuid(placement.getId());
            //getContext().getCommunicationManager().putPositionData(placement, buf, getPartner());
            //getPartner().sendPacket(PacketType.MODIFY_FINISH.identifier, buf, getContext());
        }
    }

    @Override
    protected void onClose() {
        if (getContext().getCommunicationManager().getModifier(placement) == this) {
            getContext().getCommunicationManager().setModifier(placement, null);
        }
    }

}
