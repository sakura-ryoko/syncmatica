package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.IPlayerListener;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerHandler implements IPlayerManager
{
private static final PlayerHandler INSTANCE = new PlayerHandler();
private final List<IPlayerListener> handlers = new ArrayList<>();
public static IPlayerManager getInstance() { return INSTANCE; }
    @Override
    public void registerPlayerHandler(IPlayerListener handler) {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }

    @Override
    public void unregisterPlayerHandler(IPlayerListener handler)
    {
        this.handlers.remove(handler);
    }
    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onPlayerJoin(ServerPlayerEntity player)
    {
        if (!this.handlers.isEmpty())
        {
            for (IPlayerListener handler : this.handlers)
            {
                handler.onPlayerJoin(player);
            }
        }
    }
    public void onPlayerLeave(ServerPlayerEntity player)
    {
        if (!this.handlers.isEmpty())
        {
            for (IPlayerListener handler : this.handlers)
            {
                handler.onPlayerLeave(player);
            }
        }
    }
}
