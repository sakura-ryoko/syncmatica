package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.network.packet.IServerPlayerNetworkHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.channels.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 998)
public abstract class MixinServerPlayNetworkHandler implements IServerPlayerNetworkHandler
{
    @Unique
    private ExchangeTarget exTarget = null;
    @Unique
    private ServerCommunicationManager comManager = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void syncmatica$onConnect(MinecraftServer server, ClientConnection clientConnection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci)
    {
        syncmatica$operateComms(sm -> sm.onPlayerJoin(syncmatica$getExchangeTarget(), player));
    }
    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void syncmatica$onDisconnected(final Text reason, final CallbackInfo ci)
    {
        //ChannelManager.onDisconnected();
        syncmatica$operateComms(sm -> sm.onPlayerLeave(syncmatica$getExchangeTarget()));
    }
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci)
    {
        //SyncLog.debug("MixinServerPlayNetworkHandler#syncmatica$onCustomPayload(): invoked");

        CustomPayload thisPayload = packet.payload();
        Identifier id = thisPayload.getId().id();
        PacketType type = PacketType.getType(id);
        //SyncLog.debug("MixinServerPlayNetworkHandler#syncmatica$onCustomPayload(): id {} // type {}", id.toString(), type);

        if (type != null)
        {
            // For all packets, because they probably aren't coming from the normal "PLAY" channel (?)
            // I don't know, it just works...
            if (type.equals(PacketType.NBT_DATA))
            {
                SyncNbtData payload = (SyncNbtData) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.data().getSizeInBytes());
                //NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onNbtPacket(impll.syncmatica$getExchangeTarget(), type, payload.data()));
            }
            else if (type.equals(PacketType.REGISTER_METADATA))
            {
                SyncRegisterMetadata payload = (SyncRegisterMetadata) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.CANCEL_SHARE))
            {
                SyncCancelShare payload = (SyncCancelShare) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.REQUEST_LITEMATIC))
            {
                SyncRequestDownload payload = (SyncRequestDownload) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.SEND_LITEMATIC))
            {
                SyncSendLitematic payload = (SyncSendLitematic) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.RECEIVED_LITEMATIC))
            {
                SyncReceivedLitematic payload = (SyncReceivedLitematic) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.FINISHED_LITEMATIC))
            {
                SyncFinishedLitematic payload = (SyncFinishedLitematic) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.CANCEL_LITEMATIC))
            {
                SyncCancelLitematic payload = (SyncCancelLitematic) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.REMOVE_SYNCMATIC))
            {
                SyncRemoveSyncmatic payload = (SyncRemoveSyncmatic) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.REGISTER_VERSION))
            {
                SyncRegisterVersion payload = (SyncRegisterVersion) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.CONFIRM_USER))
            {
                SyncConfirmUser payload = (SyncConfirmUser) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.FEATURE))
            {
                SyncFeature payload = (SyncFeature) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.FEATURE_REQUEST))
            {
                SyncFeatureRequest payload = (SyncFeatureRequest) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY))
            {
                SyncModify payload = (SyncModify) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_REQUEST))
            {
                SyncModifyRequest payload = (SyncModifyRequest) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_REQUEST_DENY))
            {
                SyncModifyRequestDeny payload = (SyncModifyRequestDeny) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_REQUEST_ACCEPT))
            {
                SyncModifyRequestAccept payload = (SyncModifyRequestAccept) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_FINISH))
            {
                SyncModifyFinish payload = (SyncModifyFinish) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MESSAGE))
            {
                SyncMessage payload = (SyncMessage) thisPayload;
                //SyncLog.debug("ServerCommonNetworkHandler#syncmatica$onCustomPayload(): packet type: {}, size: {}", type.getId().toString(), payload.byteBuf().readableBytes());
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            // Cancel unnecessary processing if a PacketType we own is caught
            if (ci.isCancellable())
                ci.cancel();
        }
        // NO-OP
    }
    @Unique
    public void syncmatica$operateComms(final Consumer<ServerCommunicationManager> operation)
    {
        if (comManager == null)
        {
            final Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (con != null)
            {
                comManager = (ServerCommunicationManager) con.getCommunicationManager();
            }
        }
        if (comManager != null)
        {
            operation.accept(comManager);
        }
    }

    @Unique
    public ExchangeTarget syncmatica$getExchangeTarget()
    {
        if (exTarget == null)
        {
            exTarget = new ExchangeTarget((ServerPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
}
