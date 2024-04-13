package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.network.packet.ActorClientPlayNetworkHandler;
import ch.endte.syncmatica.network.packet.IClientPlayerNetworkHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.channels.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 998)
public abstract class MixinClientPlayNetworkHandler implements IClientPlayerNetworkHandler
{
    @Shadow public abstract ClientConnection getConnection();

    @Unique
    public ExchangeTarget exTarget = null;
    @Unique
    private ClientCommunicationManager comManager = null;

    /**
     * This is required for "exposing" Custom Payload Packets that are getting obfuscated behind Config/Play channel filters, etc.
     * And it also allows for "OpenToLan" functionality to work, because via the Fabric API, the network handlers are NULL.
     * Perhaps it's a bug in Fabric?
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$handlePacket(CustomPayload packet, CallbackInfo ci)
    {
        /**
         * You can't use packet.getData() here anymore, it no longer exists.
         * You probably can put this under each Payload Type,
         * But to what end if Fabric API can handle this safely & not risk breaking
         * CustomPayload for every mod that uses it?
         */
        //ChannelManager.onChannelRegisterHandle(syncmatica$getExchangeTarget(), packet.getId().id(), packet.getData());
        if (!MinecraftClient.getInstance().isOnThread())
        {
            return; //only execute packet on main thread
        }
        Identifier id = packet.getId().id();
        //SyncLog.debug("MixinClientPlayNetworkHandler#syncmatica$handlePacket(): invoked id {}", id.toString());

        // Let's get this done
        PacketType type =  PacketType.getType(id);
        if (type != null)
        {
            // For all packets, because they probably aren't coming from the normal "PLAY" channel (?)
            // I don't know, it just works...
            if (type.equals(PacketType.NBT_DATA))
            {
                SyncNbtData payload = (SyncNbtData) packet;
                ActorClientPlayNetworkHandler.getInstance().packetNbtEvent(type, payload.data(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.REGISTER_METADATA))
            {
                SyncRegisterMetadata payload = (SyncRegisterMetadata) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.CANCEL_SHARE))
            {
                SyncCancelShare payload = (SyncCancelShare) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.REQUEST_LITEMATIC))
            {
                SyncRequestDownload payload = (SyncRequestDownload) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.SEND_LITEMATIC))
            {
                SyncSendLitematic payload = (SyncSendLitematic) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.RECEIVED_LITEMATIC))
            {
                SyncReceivedLitematic payload = (SyncReceivedLitematic) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.FINISHED_LITEMATIC))
            {
                SyncFinishedLitematic payload = (SyncFinishedLitematic) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.CANCEL_LITEMATIC))
            {
                SyncCancelLitematic payload = (SyncCancelLitematic) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.REMOVE_SYNCMATIC))
            {
                SyncRemoveSyncmatic payload = (SyncRemoveSyncmatic) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.REGISTER_VERSION))
            {
                SyncRegisterVersion payload = (SyncRegisterVersion) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.CONFIRM_USER))
            {
                SyncConfirmUser payload = (SyncConfirmUser) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.FEATURE))
            {
                SyncFeature payload = (SyncFeature) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.FEATURE_REQUEST))
            {
                SyncFeatureRequest payload = (SyncFeatureRequest) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.MODIFY))
            {
                SyncModify payload = (SyncModify) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.MODIFY_REQUEST))
            {
                SyncModifyRequest payload = (SyncModifyRequest) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.MODIFY_REQUEST_DENY))
            {
                SyncModifyRequestDeny payload = (SyncModifyRequestDeny) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.MODIFY_REQUEST_ACCEPT))
            {
                SyncModifyRequestAccept payload = (SyncModifyRequestAccept) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.MODIFY_FINISH))
            {
                SyncModifyFinish payload = (SyncModifyFinish) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.MESSAGE))
            {
                SyncMessage payload = (SyncMessage) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            // Cancel unnecessary processing if a PacketType we own is caught
            if (ci.isCancellable())
                ci.cancel();
        }
        // NO-OP
    }

    @Override
    public void syncmatica$operateComms(final Consumer<ClientCommunicationManager> operation)
    {
        if (comManager == null)
        {
            final Context con = Syncmatica.getContext(Syncmatica.CLIENT_CONTEXT);
            if (con != null)
            {
                comManager = (ClientCommunicationManager) con.getCommunicationManager();
            }
        }
        if (comManager != null)
        {
            operation.accept(comManager);
        }
    }

    @Override
    public ExchangeTarget syncmatica$getExchangeTarget()
    {
        if (exTarget == null)
        {
            exTarget = new ExchangeTarget((ClientPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
}
