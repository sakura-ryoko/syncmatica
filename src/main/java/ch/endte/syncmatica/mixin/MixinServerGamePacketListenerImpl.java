package ch.endte.syncmatica.mixin;

import java.util.function.Consumer;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.network.actor.IServerPlay;
import ch.endte.syncmatica.network.handler.ServerPlayHandler;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1001)
public abstract class MixinServerGamePacketListenerImpl implements IServerPlay
{
    @Shadow public abstract ServerPlayer getPlayer();

    @Unique
    private ExchangeTarget exTarget = null;
    @Unique
    private ServerCommunicationManager comManager = null;

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void syncmatica$onDisconnected(DisconnectionDetails details, CallbackInfo ci)
    {
        syncmatica$operateComms(sm -> sm.onPlayerLeave(syncmatica$getExchangeTarget()));
    }

    // This exists because of the Communications Manager / Exchange Target system,
    // and FAPI networking is too slow to register the receivers
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$onCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci)
    {
        CustomPacketPayload thisPayload = packet.payload();

        if (thisPayload.type().id().getNamespace().equals(Reference.MOD_ID))
        {
            SyncmaticaPacket.Payload payload = (SyncmaticaPacket.Payload) thisPayload;
            ServerPlayHandler.decodeSyncData(payload.data(), this);

            // Cancel unnecessary processing if a PacketType we own is caught
            if (ci.isCancellable())
                ci.cancel();
        }
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
            exTarget = new ExchangeTarget((ServerGamePacketListenerImpl) (Object) this);
        }
        return exTarget;
    }
}
