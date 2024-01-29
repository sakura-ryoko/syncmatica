package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.network.legacy.IServerPlayerNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    public void onConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        //IServerPlayerNetworkHandler impl = (IServerPlayerNetworkHandler) player.networkHandler;
        //impl.syncmatica$operateComms(sm -> sm.onPlayerJoin(impl.syncmatica$getExchangeTarget(), player));
    }
}
