package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.ISyncNbtServerListener;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class SyncNbtServerHandler implements ISyncNbtServerManager
{
    private static final SyncNbtServerHandler INSTANCE = new SyncNbtServerHandler();
    private final List<ISyncNbtServerListener> handlers = new ArrayList<>();
    public static ISyncNbtServerManager getInstance() { return INSTANCE; }
    @Override
    public void registerSyncNbtServerHandler(ISyncNbtServerListener handler)
    {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }
    @Override
    public void unregisterSyncNbtServerHandler(ISyncNbtServerListener handler)
    {
        this.handlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void receiveSyncNbtServer(NbtCompound data, ServerPlayNetworking.Context ctx)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncNbtServerListener handler : this.handlers)
            {
                handler.receiveSyncNbtServer(data, ctx);
            }
        }
    }
    public void sendSyncNbtServer(NbtCompound data, ServerPlayerEntity player)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncNbtServerListener handler : this.handlers)
            {
                handler.sendSyncNbtServer(data, player);
            }
        }
    }
}
