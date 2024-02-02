package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.interfaces.IPlayerListener;
import ch.endte.syncmatica.network.ClientNetworkPlayInitHandler;
import ch.endte.syncmatica.network.ServerNetworkPlayHandler;
import ch.endte.syncmatica.network.payload.channels.SyncmaticaNbtData;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListener implements IPlayerListener
{
    public void onPlayerJoin(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerJoin(): invoked.");
        if (SyncmaticaReference.isClient())
        {
            // Make sure these are loaded ...
            ClientNetworkPlayInitHandler.registerPlayChannels();
            ClientNetworkPlayInitHandler.registerReceivers();
        }
        else {
            // Send hello packet
            NbtCompound nbt = new NbtCompound();
            nbt.putString(SyncmaticaNbtData.KEY, "hello");
            SyncmaticaNbtData payload = new SyncmaticaNbtData(nbt);
            ServerNetworkPlayHandler.sendSyncPacket(payload, player);
        }
    }
    public void onPlayerLeave(ServerPlayerEntity player)
    {
        SyncLog.debug("PlayerManagerEvents#onPlayerLeave(): invoked.");
    }
}
