package ch.endte.syncmatica.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;


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
}
