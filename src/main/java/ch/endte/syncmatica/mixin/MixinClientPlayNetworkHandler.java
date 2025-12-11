package ch.endte.syncmatica.mixin;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.network.actor.IClientPlay;
import ch.endte.syncmatica.network.handler.ClientPlayHandler;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPacketListener.class, priority = 1001)
public abstract class MixinClientPlayNetworkHandler implements IClientPlay
{
    @Unique
    public ExchangeTarget exTarget = null;
    @Unique
    private ClientCommunicationManager comManager = null;

    // This exists because of the Communications Manager / Exchange Target system,
    // and FAPI networking is too slow to register the receivers
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void syncmatica$handlePacket(CustomPacketPayload packet, CallbackInfo ci)
    {
        if (!Minecraft.getInstance().isSameThread())
        {
            return; //only execute packet on main thread
        }

        if (packet.type().id().getNamespace().equals(Reference.MOD_ID))
        {
            SyncmaticaPacket.Payload payload = (SyncmaticaPacket.Payload) packet;
            ClientPlayHandler.decodeSyncData(payload.data(), (ClientPacketListener) (Object) this);

            // Cancel unnecessary processing if a PacketType we own is caught
            if  (ci.isCancellable())
                ci.cancel();
        }
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
            exTarget = new ExchangeTarget((ClientPacketListener) (Object) this);
        }
        return exTarget;
    }
}
