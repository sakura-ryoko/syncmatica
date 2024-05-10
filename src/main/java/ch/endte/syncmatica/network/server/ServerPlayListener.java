package ch.endte.syncmatica.network.server;

import ch.endte.syncmatica.network.channels.*;
import ch.endte.syncmatica.network.packet.IServerPlay;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.util.SyncLog;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.packet.CustomPayload;

public class ServerPlayListener
{
    public static void handlePacket(IServerPlay impl, CustomPayload thisPayload, PacketType type, CallbackInfo ci)
    {
        switch (type)
        {
            case NBT_DATA ->
            {
                SyncNbtData payload = (SyncNbtData) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onNbtPacket(impl.syncmatica$getExchangeTarget(), type, payload.data()));
            }
            case REGISTER_METADATA ->
            {
                SyncRegisterMetadata payload = (SyncRegisterMetadata) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case CANCEL_SHARE ->
            {
                SyncCancelShare payload = (SyncCancelShare) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case REQUEST_LITEMATIC ->
            {
                SyncRequestDownload payload = (SyncRequestDownload) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case SEND_LITEMATIC ->
            {
                SyncSendLitematic payload = (SyncSendLitematic) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case RECEIVED_LITEMATIC ->
            {
                SyncReceivedLitematic payload = (SyncReceivedLitematic) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case FINISHED_LITEMATIC ->
            {
                SyncFinishedLitematic payload = (SyncFinishedLitematic) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case CANCEL_LITEMATIC ->
            {
                SyncCancelLitematic payload = (SyncCancelLitematic) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case REMOVE_SYNCMATIC ->
            {
                SyncRemoveSyncmatic payload = (SyncRemoveSyncmatic) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case REGISTER_VERSION ->
            {
                SyncRegisterVersion payload = (SyncRegisterVersion) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case CONFIRM_USER ->
            {
                SyncConfirmUser payload = (SyncConfirmUser) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case FEATURE ->
            {
                SyncFeature payload = (SyncFeature) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case FEATURE_REQUEST ->
            {
                SyncFeatureRequest payload = (SyncFeatureRequest) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case MODIFY ->
            {
                SyncModify payload = (SyncModify) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case MODIFY_REQUEST ->
            {
                SyncModifyRequest payload = (SyncModifyRequest) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case MODIFY_REQUEST_DENY ->
            {
                SyncModifyRequestDeny payload = (SyncModifyRequestDeny) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case MODIFY_REQUEST_ACCEPT ->
            {
                SyncModifyRequestAccept payload = (SyncModifyRequestAccept) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case MODIFY_FINISH ->
            {
                SyncModifyFinish payload = (SyncModifyFinish) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            case MESSAGE ->
            {
                SyncMessage payload = (SyncMessage) thisPayload;
                impl.syncmatica$operateComms(sm -> sm.onPacket(impl.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            default -> SyncLog.warn("ServerPlayListener#handlePacket(): Invalid packet type {} received", type.toString());
        }
        // Cancel unnecessary processing if a PacketType we own is caught
        if (ci.isCancellable())
            ci.cancel();

    }
}
