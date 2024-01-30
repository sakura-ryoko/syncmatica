package ch.endte.syncmatica.interfaces;

import net.minecraft.server.MinecraftServer;

public interface IServerListener
{
    default void onServerStarting(MinecraftServer server) {}
    default void onServerStarted(MinecraftServer server) {}
    default void onServerStopping(MinecraftServer server) {}
    default void onServerStopped(MinecraftServer server) {}
}
