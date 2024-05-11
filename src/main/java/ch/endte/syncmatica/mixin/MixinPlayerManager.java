package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.network.channels.SyncRegisterVersion;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.server.ServerPlayHandler;
import io.netty.buffer.Unpooled;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class MixinPlayerManager
{
    public MixinPlayerManager() { super(); }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void syncmatica$eventOnPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci)
    {
        Syncmatica.debug("MixinPlayerManager#onPlayerJoin(): invoked.");

        if (Reference.isServer() || Reference.isDedicatedServer() || Reference.isIntegratedServer())
        {
            Context server = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (server == null || !server.isStarted())
            {
                Syncmatica.LOGGER.warn("MixinPlayerManager#onPlayerJoin(): executed without a Server context!");
            }
            else
            {
                // This needs to happen & get squashed anyway during .sendSyncPacket() in order for things to work
                SyncByteBuf buf = new SyncByteBuf(Unpooled.buffer());
                buf.writeString(Reference.MOD_VERSION);
                SyncRegisterVersion payload = new SyncRegisterVersion(buf);
                ServerPlayHandler.sendSyncPacket(payload, player);
            }
        }
        if (Reference.isClient() || Reference.isSinglePlayer())
        {
            Context client = Syncmatica.getContext(Syncmatica.CLIENT_CONTEXT);
            if (client == null || !client.isStarted())
            {
                Syncmatica.LOGGER.warn("MixinPlayerManager#onPlayerJoin(): executed without a Client context!");
            }
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void syncmatica$eventOnPlayerLeave(ServerPlayerEntity player, CallbackInfo ci)
    {
        Syncmatica.debug("MixinPlayerManager#onPlayerLeave(): invoked.");
        // Something we need to do here?
    }
}
