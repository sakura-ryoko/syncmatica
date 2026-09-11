package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.actor.IServerPlay;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList
{
    public MixinPlayerList() { super(); }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void syncmatica$eventOnPlayerJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci)
    {
        Syncmatica.debug("MixinPlayerManager#onPlayerJoin(): player {}", player.getName().tryCollapseToString());

        IServerPlay handler = (IServerPlay) player.connection;
        handler.syncmatica$operateComms(sm -> sm.onPlayerJoin(handler.syncmatica$getExchangeTarget(), player));
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void syncmatica$eventOnPlayerLeave(ServerPlayer player, CallbackInfo ci)
    {
        // Something we need to do here?
    }
}
