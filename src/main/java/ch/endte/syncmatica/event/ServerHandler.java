package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.IServerListener;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler implements IServerManager
{
private static final ServerHandler INSTANCE = new ServerHandler();
private final List<IServerListener> handlers = new ArrayList<>();
public static IServerManager getInstance() { return INSTANCE; }
    @Override
    public void registerServerHandler(IServerListener handler) {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }

    @Override
    public void unregisterServerHandler(IServerListener handler)
    {
        this.handlers.remove(handler);
    }
    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onServerStarting(MinecraftServer server)
    {
        if (!this.handlers.isEmpty())
        {
            for (IServerListener handler : this.handlers)
            {
                handler.onServerStarting(server);
            }
        }
    }
    public void onServerStarted(MinecraftServer server)
    {
        if (!this.handlers.isEmpty())
        {
            for (IServerListener handler : this.handlers)
            {
                handler.onServerStarted(server);
            }
        }
    }
    public void onServerStopping(MinecraftServer server)
    {
        if (!this.handlers.isEmpty())
        {
            for (IServerListener handler : this.handlers)
            {
                handler.onServerStopping(server);
            }
        }
    }
    public void onServerStopped(MinecraftServer server)
    {
        if (!this.handlers.isEmpty())
        {
            for (IServerListener handler : this.handlers)
            {
                handler.onServerStopped(server);
            }
        }
    }
}
