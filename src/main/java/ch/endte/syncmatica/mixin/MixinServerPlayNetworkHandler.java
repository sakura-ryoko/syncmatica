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
        syncmatica$operateComms(sm -> sm.onPlayerLeave(syncmatica$getExchangeTarget()));
    }
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci)
    {
        CustomPayload thisPayload = packet.payload();
        Identifier id = thisPayload.getId().id();
        PacketType type = PacketType.getType(id);

        // I don't know, it just works...
        if (type != null)
        {
            if (type.equals(PacketType.NBT_DATA))
            {
                SyncNbtData payload = (SyncNbtData) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onNbtPacket(impll.syncmatica$getExchangeTarget(), type, payload.data()));
            }
            else if (type.equals(PacketType.REGISTER_METADATA))
            {
                SyncRegisterMetadata payload = (SyncRegisterMetadata) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.CANCEL_SHARE))
            {
                SyncCancelShare payload = (SyncCancelShare) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.REQUEST_LITEMATIC))
            {
                SyncRequestDownload payload = (SyncRequestDownload) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.SEND_LITEMATIC))
            {
                SyncSendLitematic payload = (SyncSendLitematic) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.RECEIVED_LITEMATIC))
            {
                SyncReceivedLitematic payload = (SyncReceivedLitematic) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.FINISHED_LITEMATIC))
            {
                SyncFinishedLitematic payload = (SyncFinishedLitematic) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.CANCEL_LITEMATIC))
            {
                SyncCancelLitematic payload = (SyncCancelLitematic) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.REMOVE_SYNCMATIC))
            {
                SyncRemoveSyncmatic payload = (SyncRemoveSyncmatic) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.REGISTER_VERSION))
            {
                SyncRegisterVersion payload = (SyncRegisterVersion) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.CONFIRM_USER))
            {
                SyncConfirmUser payload = (SyncConfirmUser) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.FEATURE))
            {
                SyncFeature payload = (SyncFeature) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.FEATURE_REQUEST))
            {
                SyncFeatureRequest payload = (SyncFeatureRequest) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY))
            {
                SyncModify payload = (SyncModify) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_REQUEST))
            {
                SyncModifyRequest payload = (SyncModifyRequest) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_REQUEST_DENY))
            {
                SyncModifyRequestDeny payload = (SyncModifyRequestDeny) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_REQUEST_ACCEPT))
            {
                SyncModifyRequestAccept payload = (SyncModifyRequestAccept) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MODIFY_FINISH))
            {
                SyncModifyFinish payload = (SyncModifyFinish) thisPayload;
                //NetworkThreadUtils.forceMainThread(packet, impl, impl.player.getServerWorld());
                IServerPlayerNetworkHandler impll = this;
                impll.syncmatica$operateComms(sm -> sm.onPacket(impll.syncmatica$getExchangeTarget(), type, payload.byteBuf()));
            }
            else if (type.equals(PacketType.MESSAGE))
            {
                SyncMessage payload = (SyncMessage) thisPayload;
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
