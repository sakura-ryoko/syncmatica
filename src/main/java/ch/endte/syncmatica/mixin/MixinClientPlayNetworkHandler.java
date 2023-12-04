package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.mixin_actor.ActorClientPlayNetworkHandler;
import ch.endte.syncmatica.network.SyncmaticaPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = ClientPlayNetworkHandler.class, priority = 998)
public abstract class MixinClientPlayNetworkHandler {

    @Unique
    public ExchangeTarget exTarget = null;

    @Inject(method = "warnOnUnknownPayload", at = @At("HEAD"), cancellable = true)
    private void handlePacket(CustomPayload customPayload, CallbackInfo ci) {
        // ChannelManager.onChannelRegisterHandle(getExchangeTarget(), packet.getChannel(), packet.getData());
        if (!MinecraftClient.getInstance().isOnThread()) {
            return; //only execute packet on main thread
        }
        if (customPayload instanceof SyncmaticaPayload payload) {
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
}
