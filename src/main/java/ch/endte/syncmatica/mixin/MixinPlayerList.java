package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.network.handler.ServerPlayHandler;
import ch.endte.syncmatica.network.PacketType;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
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

        if (Reference.isServer() || Reference.isDedicatedServer() || Reference.isIntegratedServer() || Reference.isOpenToLan())
        {
            Context server = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (server != null && server.isStarted())
            {
                Syncmatica.debug("syncmatica$eventOnPlayerJoin: yeet");
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeUtf(Reference.MOD_VERSION);

                ServerPlayHandler.encodeSyncData(new SyncmaticaPacket(PacketType.REGISTER_VERSION.getId(), buf), player);
            }
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void syncmatica$eventOnPlayerLeave(ServerPlayer player, CallbackInfo ci)
    {
        // Something we need to do here?
    }
}
