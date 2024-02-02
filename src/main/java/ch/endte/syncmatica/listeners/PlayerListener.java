package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.interfaces.IPlayerListener;
import ch.endte.syncmatica.network.ServerNetworkPlayHandler;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.payload.channels.SyncRegisterVersion;
import ch.endte.syncmatica.util.SyncLog;
import io.netty.buffer.Unpooled;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListener implements IPlayerListener
{
    public void onPlayerJoin(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerJoin(): invoked.");
        if (SyncmaticaReference.isServer() || SyncmaticaReference.isDedicatedServer() || SyncmaticaReference.isIntegratedServer())
        {
            // Just to make sure the receivers are activated
        //    ServerNetworkPlayInitHandler.registerReceivers();
            Context server = Syncmatica.getContext(Syncmatica.SERVER_CONTEXT);
            if (server == null || !server.isStarted())
            {
                SyncLog.error("PlayerManagerEvents#onPlayerJoin(): executed without a Server context!");
            }
            else
            {
                // Send hello packet
                //NbtCompound nbt = new NbtCompound();
                //nbt.putString(SyncmaticaNbtData.KEY, "hello");
                //SyncmaticaNbtData payload = new SyncmaticaNbtData(nbt);
                //ServerNetworkPlayHandler.sendSyncPacket(payload, player);

                // Yeet REGISTER_VERSION packet instead now that the Client is *FULLY* joined
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
                SyncLog.error("PlayerManagerEvents#onPlayerJoin(): executed without a Client context!");
            }
            // Just to make sure the receivers are activated
            //    ClientNetworkPlayInitHandler.registerReceivers();
        }
    }
    public void onPlayerLeave(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerLeave(): invoked.");
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
