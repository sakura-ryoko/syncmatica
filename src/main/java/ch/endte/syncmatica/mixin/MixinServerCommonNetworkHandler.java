package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.network.packet.IServerPlayerNetworkHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public class MixinServerCommonNetworkHandler
{
    /**
     * This is required for "exposing" Custom Payload Packets that are getting obfuscated behind Config/Play channel filters, etc.
     * And it also allows for "OpenToLan" functionality to work, because via the Fabric API, the network handlers are NULL.
     * Perhaps it's a bug in Fabric?
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci)
    {
        SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): invoked");
        Object thiss = this;
        if (thiss instanceof ServerPlayNetworkHandler impl)
        {
            CustomPayload thisPayload = packet.payload();
            PacketType type = PacketType.getType(thisPayload.getId().id());

            if (type != null)
            {
                // For all packets, because they probably aren't coming from the normal "PLAY" channel (?)
                // I don't know, it just works...
                if (type.equals(PacketType.NBT_DATA))
                {
                    SyncNbtData payload = (SyncNbtData) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.data().getSizeInBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onNbtPacket(impll.syncmatica$getExchangeTarget(), type, payload.data()));
                }
                else if (type.equals(PacketType.REGISTER_METADATA))
                {
                    SyncRegisterMetadata payload = (SyncRegisterMetadata) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.CANCEL_SHARE))
                {
                    SyncCancelShare payload = (SyncCancelShare) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.REQUEST_LITEMATIC))
                {
                    SyncRequestDownload payload = (SyncRequestDownload) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.SEND_LITEMATIC))
                {
                    SyncSendLitematic payload = (SyncSendLitematic) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.RECEIVED_LITEMATIC))
                {
                    SyncReceivedLitematic payload = (SyncReceivedLitematic) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.FINISHED_LITEMATIC))
                {
                    SyncFinishedLitematic payload = (SyncFinishedLitematic) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.CANCEL_LITEMATIC))
                {
                    SyncCancelLitematic payload = (SyncCancelLitematic) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.REMOVE_SYNCMATIC))
                {
                    SyncRemoveSyncmatic payload = (SyncRemoveSyncmatic) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.REGISTER_VERSION))
                {
                    SyncRegisterVersion payload = (SyncRegisterVersion) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.CONFIRM_USER))
                {
                    SyncConfirmUser payload = (SyncConfirmUser) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.FEATURE))
                {
                    SyncFeature payload = (SyncFeature) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.FEATURE_REQUEST))
                {
                    SyncFeatureRequest payload = (SyncFeatureRequest) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.MODIFY))
                {
                    SyncModify payload = (SyncModify) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.MODIFY_REQUEST))
                {
                    SyncModifyRequest payload = (SyncModifyRequest) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.MODIFY_REQUEST_DENY))
                {
                    SyncModifyRequestDeny payload = (SyncModifyRequestDeny) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.MODIFY_REQUEST_ACCEPT))
                {
                    SyncModifyRequestAccept payload = (SyncModifyRequestAccept) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.MODIFY_FINISH))
                {
                    SyncModifyFinish payload = (SyncModifyFinish) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                else if (type.equals(PacketType.MESSAGE))
                {
                    SyncMessage payload = (SyncMessage) thisPayload;
                    //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                    NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                    IServerPlayerNetworkHandler impll = ((IServerPlayerNetworkHandler) impl);
                    impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
                }
                // Cancel unnecessary processing if a PacketType we own is caught
                if (ci.isCancellable())
                    ci.cancel();
            }
            // NO-OP
        }
    }
}
