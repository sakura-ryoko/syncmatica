package ch.endte.syncmatica.network.client;

import javax.annotation.Nonnull;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.channels.*;
import ch.endte.syncmatica.network.packet.ActorClientPlayHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

public class ClientPlayListener
{
    public static void handlePacket(@Nonnull ClientPlayNetworkHandler handler, @Nonnull CustomPayload packet, CallbackInfo ci, @Nonnull PacketType type)
    {
        // Switch logic so we can separate the Payload data types properly.
        switch (type)
        {
            case REGISTER_METADATA ->
            {
                SyncRegisterMetadata payload = (SyncRegisterMetadata) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case CANCEL_SHARE ->
            {
                SyncCancelShare payload = (SyncCancelShare) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case REQUEST_LITEMATIC ->
            {
                SyncRequestDownload payload = (SyncRequestDownload) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case SEND_LITEMATIC ->
            {
                SyncSendLitematic payload = (SyncSendLitematic) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case RECEIVED_LITEMATIC ->
            {
                SyncReceivedLitematic payload = (SyncReceivedLitematic) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case FINISHED_LITEMATIC ->
            {
                SyncFinishedLitematic payload = (SyncFinishedLitematic) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case CANCEL_LITEMATIC ->
            {
                SyncCancelLitematic payload = (SyncCancelLitematic) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case REMOVE_SYNCMATIC ->
            {
                SyncRemoveSyncmatic payload = (SyncRemoveSyncmatic) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case REGISTER_VERSION ->
            {
                SyncRegisterVersion payload = (SyncRegisterVersion) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case CONFIRM_USER ->
            {
                SyncConfirmUser payload = (SyncConfirmUser) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case FEATURE ->
            {
                SyncFeature payload = (SyncFeature) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case FEATURE_REQUEST ->
            {
                SyncFeatureRequest payload = (SyncFeatureRequest) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case MODIFY ->
            {
                SyncModify payload = (SyncModify) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case MODIFY_REQUEST ->
            {
                SyncModifyRequest payload = (SyncModifyRequest) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case MODIFY_REQUEST_DENY ->
            {
                SyncModifyRequestDeny payload = (SyncModifyRequestDeny) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case MODIFY_REQUEST_ACCEPT ->
            {
                SyncModifyRequestAccept payload = (SyncModifyRequestAccept) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case MODIFY_FINISH ->
            {
                SyncModifyFinish payload = (SyncModifyFinish) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            case MESSAGE ->
            {
                SyncMessage payload = (SyncMessage) packet;
                ActorClientPlayHandler.getInstance().packetEvent(type, payload.byteBuf(), handler, ci);
            }
            default -> Syncmatica.LOGGER.warn("ClientPlayListener#handlePacket(): Invalid packet type {} received", type.toString());
        }
        // Cancel unnecessary processing if a PacketType we own is caught
        if  (ci.isCancellable())
            ci.cancel();
    }
}
