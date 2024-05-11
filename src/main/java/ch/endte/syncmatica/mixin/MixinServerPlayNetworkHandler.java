package ch.endte.syncmatica.mixin;

import java.util.function.Consumer;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.network.packet.IServerPlay;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.server.ServerPlayListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 998)
public abstract class MixinServerPlayNetworkHandler implements IServerPlay
{
    @Shadow public abstract ServerPlayerEntity getPlayer();

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
        PacketType type = PacketType.getType(thisPayload.getId().id());

        // I don't know, it just works...
        if (type != null)
        {
            //NetworkThreadUtils.forceMainThread(packet, this, this.getPlayer().getServerWorld());
            ServerPlayListener.handlePacket(this, thisPayload, type, ci);
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
            exTarget = new ExchangeTarget((ServerPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
}
