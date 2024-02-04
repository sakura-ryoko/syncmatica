package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.ServerNetworkPlayHandler;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.payload.channels.SyncRegisterVersion;
import ch.endte.syncmatica.util.SyncLog;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager
{
    /**
     * Callbacks for IPlayerManager (maybe remove/Simplify)
     */
    public MixinPlayerManager() { super(); }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void syncmatica$eventOnPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci)
    {
        SyncLog.debug("MixinPlayerManager#onPlayerJoin(): invoked.");

        if (SyncmaticaReference.isServer() || SyncmaticaReference.isDedicatedServer() || SyncmaticaReference.isIntegratedServer())
        {
            Context server = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (server == null || !server.isStarted())
            {
                SyncLog.error("MixinPlayerManager#onPlayerJoin(): executed without a Server context!");
            }
            else
            {
                // Yeet a REGISTER_VERSION packet
                // #FIXME -This needs to happen & get squashed anyways during .sendSyncPacket() in order for things to work
                // #TODO --->  But Why Fabric?  Remove this and you break Syncmatica from working...
                SyncByteBuf buf = new SyncByteBuf(Unpooled.buffer());
                buf.writeString(SyncmaticaReference.MOD_VERSION);
                SyncRegisterVersion payload = new SyncRegisterVersion(buf);
                ServerNetworkPlayHandler.sendSyncPacket(payload, player);
            }
        }
        if (SyncmaticaReference.isClient() || SyncmaticaReference.isSinglePlayer())
        {
            Context client = Syncmatica.getContext(Syncmatica.CLIENT_CONTEXT);
            if (client == null || !client.isStarted())
            {
                SyncLog.error("MixinPlayerManager#onPlayerJoin(): executed without a Client context!");
            }
            //else
            //{
                // Yeet a REGISTER_VERSION packet
                // #FIXME -This needs to happen & get squashed anyways during .sendSyncPacket() in order for things to work
                // #TODO --->  But Why Fabric?  Remove this and you break Syncmatica from working...
                //SyncByteBuf buf = new SyncByteBuf(Unpooled.buffer());
                //buf.writeString(SyncmaticaReference.MOD_VERSION);
                //SyncRegisterVersion payload = new SyncRegisterVersion(buf);
                //ClientNetworkPlayHandler.sendSyncPacket(payload);
            //}
        }
    }
    @Inject(method = "remove", at = @At("HEAD"))
    private void syncmatica$eventOnPlayerLeave(ServerPlayerEntity player, CallbackInfo ci)
    {
        SyncLog.debug("MixinPlayerManager#onPlayerLeave(): invoked.");

        //if (SyncmaticaReference.isServer() || SyncmaticaReference.isDedicatedServer() || SyncmaticaReference.isIntegratedServer())
        //{
        //
        //}
        //if (SyncmaticaReference.isClient() || SyncmaticaReference.isSinglePlayer())
        //{
        //
        //}
    }
}
