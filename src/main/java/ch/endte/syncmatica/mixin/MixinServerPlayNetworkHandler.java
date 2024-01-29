package ch.endte.syncmatica.mixin;

import java.util.function.Consumer;

import ch.endte.syncmatica.network.legacy.ChannelManager;
import ch.endte.syncmatica.network.legacy.IServerPlayerNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

@Deprecated
@Mixin(value = ServerPlayNetworkHandler.class, priority = 998)
public abstract class MixinServerPlayNetworkHandler implements IServerPlayerNetworkHandler {
    /*
    @Unique
    private ExchangeTarget exTarget = null;

    @Unique
    private ServerCommunicationManager comManager = null;

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    @Deprecated
    public void onDisconnected(final Text reason, final CallbackInfo ci) {
        ChannelManager.onDisconnected();
        syncmatica$operateComms(sm -> sm.onPlayerLeave(syncmatica$getExchangeTarget()));
    }

    @Deprecated
    public void syncmatica$operateComms(final Consumer<ServerCommunicationManager> operation) {
        if (comManager == null) {
            final Context con = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (con != null) {
                comManager = (ServerCommunicationManager) con.getCommunicationManager();
            }
        }
        if (comManager != null) {
            operation.accept(comManager);
        }
    }

    @Deprecated
    public ExchangeTarget syncmatica$getExchangeTarget() {
        if (exTarget == null) {
            exTarget = new ExchangeTarget((ServerPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
     */
}
