package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.interfaces.IPlayerListener;
import ch.endte.syncmatica.network.ClientNetworkPlayInitHandler;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListener implements IPlayerListener
{
    public void onPlayerJoin(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerJoin(): invoked.");
        if (SyncmaticaReference.isClient())
        {
            // Make sure these are registered ...
            ClientNetworkPlayInitHandler.registerReceivers();
        }
        if (SyncmaticaReference.isServer() || SyncmaticaReference.isIntegratedServer())
        {
            // Send hello packet
            //NbtCompound nbt = new NbtCompound();
            //nbt.putString(SyncmaticaNbtData.KEY, "hello");
            //SyncmaticaNbtData payload = new SyncmaticaNbtData(nbt);
            //ServerNetworkPlayHandler.sendSyncPacket(payload, player);

            // Yeet REGISTER_VERSION packet instead now that the Client is *FULLY* joined
            //SyncByteBuf buf = new SyncByteBuf(Unpooled.buffer());
            //buf.writeString(SyncmaticaReference.MOD_VERSION);
            //RegisterVersion payload = new RegisterVersion(buf);
            //ServerNetworkPlayHandler.sendSyncPacket(payload, player);
        }
    }
    public void onPlayerLeave(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerLeave(): invoked.");
    }
}
