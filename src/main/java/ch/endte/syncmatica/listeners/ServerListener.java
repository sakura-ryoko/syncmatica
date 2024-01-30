package ch.endte.syncmatica.listeners;

import ch.endte.syncmatica.interfaces.IServerListener;
import ch.endte.syncmatica.network.ServerNetworkPlayInitHandler;
import ch.endte.syncmatica.network.packet.PacketProvider;
import ch.endte.syncmatica.network.test.ServerDebugSuite;
import ch.endte.syncmatica.service.DebugService;
import net.minecraft.server.MinecraftServer;

public class ServerListener implements IServerListener
{
    public void onServerStarting(MinecraftServer minecraftServer)
    {
        // Register in case for whatever reason they aren't already
        ServerNetworkPlayInitHandler.registerPlayChannels();
        PacketProvider.registerPayloads();
        ServerDebugSuite.checkGlobalChannels();
        DebugService.printDebug("MinecraftServerEvents#onServerStarting(): invoked.");
    }
    public void onServerStarted(MinecraftServer minecraftServer)
    {
        ServerNetworkPlayInitHandler.registerReceivers();
        ServerDebugSuite.checkGlobalChannels();
        DebugService.printDebug("MinecraftServerEvents#onServerStarted(): invoked.");
    }
    public void onServerStopping(MinecraftServer minecraftServer)
    {
        ServerDebugSuite.checkGlobalChannels();
        DebugService.printDebug("MinecraftServerEvents#onServerStopping(): invoked.");
    }
    public void onServerStopped(MinecraftServer minecraftServer)
    {
        ServerNetworkPlayInitHandler.unregisterReceivers();
        ServerDebugSuite.checkGlobalChannels();
        DebugService.printDebug("MinecraftServerEvents#onServerStopped(): invoked.");
    }
}
