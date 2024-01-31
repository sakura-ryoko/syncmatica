package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.data.FileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.interfaces.IServerListener;
import ch.endte.syncmatica.network.ClientNetworkPlayInitHandler;
import ch.endte.syncmatica.network.ServerNetworkPlayInitHandler;
import ch.endte.syncmatica.network.packet.PacketProvider;
import ch.endte.syncmatica.network.test.ClientDebugSuite;
import ch.endte.syncmatica.network.test.ServerDebugSuite;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public class ServerListener implements IServerListener
{
    public void onServerStarting(MinecraftServer minecraftServer)
    {
        // Register in case for whatever reason they aren't already
        if (SyncmaticaReference.isServer())
        {
            ServerNetworkPlayInitHandler.registerPlayChannels();
            PacketProvider.registerPayloads();
            ServerDebugSuite.checkGlobalChannels();
        }
        else {
            // MaLiLib Calls for Client Context
            ClientNetworkPlayInitHandler.registerPlayChannels();
            PacketProvider.registerPayloads();
            ClientDebugSuite.checkGlobalChannels();
        }
        SyncLog.debug("MinecraftServerEvents#onServerStarting(): invoked.");

        if (minecraftServer.isDedicated())
            SyncmaticaReference.setDedicatedServer();
        // Process Syncmatica Server Context
        Syncmatica.initServer(
                new ServerCommunicationManager(),
                new FileStorage(),
                new SyncmaticManager(),
                SyncmaticaReference.isDedicatedServer(),
                minecraftServer.getSavePath(WorldSavePath.ROOT).toFile()
        ).startup();
    }
    public void onServerStarted(MinecraftServer minecraftServer)
    {
        if (SyncmaticaReference.isServer())
        {
            ServerNetworkPlayInitHandler.registerReceivers();
            ServerDebugSuite.checkGlobalChannels();
        }
        else
        {
            // MaLiLib Calls for Client Context
            ClientNetworkPlayInitHandler.registerReceivers();
        }
        SyncLog.debug("MinecraftServerEvents#onServerStarted(): invoked.");
    }
    public void onServerStopping(MinecraftServer minecraftServer)
    {
        if (SyncmaticaReference.isServer())
        {
            ServerDebugSuite.checkGlobalChannels();
        }
        else
        {
            // MaLiLib Calls for Client Context
            ClientDebugSuite.checkGlobalChannels();
        }
        SyncLog.debug("MinecraftServerEvents#onServerStopping(): invoked.");
    }
    public void onServerStopped(MinecraftServer minecraftServer)
    {
        if (SyncmaticaReference.isServer())
        {
            ServerNetworkPlayInitHandler.unregisterReceivers();
            ServerDebugSuite.checkGlobalChannels();
        }
        else
        {
            // MaLiLib Calls for Client Context
            ClientNetworkPlayInitHandler.unregisterReceivers();
            ClientDebugSuite.checkGlobalChannels();
        }
        SyncLog.debug("MinecraftServerEvents#onServerStopped(): invoked.");

        // Process Syncmatica Shutdown
        Syncmatica.shutdown();
    }
}
