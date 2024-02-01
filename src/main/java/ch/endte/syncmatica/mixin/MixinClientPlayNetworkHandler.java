package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.event.SyncPacketClientHandler;
import ch.endte.syncmatica.network.ClientNetworkPlayInitHandler;
import ch.endte.syncmatica.network.payload.channels.SyncmaticaNbtData;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 998)
public abstract class MixinClientPlayNetworkHandler {

    /*
    @Unique
    public ExchangeTarget exTarget = null;

    @Inject(method = "warnOnUnknownPayload", at = @At("HEAD"), cancellable = true)
    private void handlePacket(CustomPayload customPayload, CallbackInfo ci) {
        // ChannelManager.onChannelRegisterHandle(getExchangeTarget(), packet.getChannel(), packet.getData());
        if (!MinecraftClient.getInstance().isOnThread()) {
            return; //only execute packet on main thread
        }
        if (customPayload instanceof SyncmaticaS2CPayload payload) {
            ActorClientPlayNetworkHandler.getInstance().packetEvent((ClientPlayNetworkHandler) (Object) this, payload, ci);
        }
    }

    @Unique
    private ExchangeTarget getExchangeTarget() {
        if (exTarget == null) {
            exTarget = new ExchangeTarget((ClientPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
    */
    @Inject(method = "onGameJoin", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;joinWorld(" +
                    "Lnet/minecraft/client/world/ClientWorld;)V"))
    private void syncmatica_onPreGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        SyncLog.debug("MixinClientPlayNetworkHandler#syncmatica_onPreGameJoin(): invoked");
        if (SyncmaticaReference.isClient())
            ClientNetworkPlayInitHandler.registerPlayChannels();
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void syncmatica_onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        SyncLog.debug("MixinClientPlayNetworkHandler#syncmatica_onPostGameJoin(): invoked");
        if (SyncmaticaReference.isClient())
        {
            ClientNetworkPlayInitHandler.registerReceivers();

            NbtCompound nbt = new NbtCompound();
            nbt.putString(SyncmaticaNbtData.KEY, "hello");
            ((SyncPacketClientHandler) SyncPacketClientHandler.getInstance()).encodeSyncmaticaPayload(nbt, SyncmaticaPacketType.SYNCMATICA_PROTOCOL_VERSION);
        }
    }
}
