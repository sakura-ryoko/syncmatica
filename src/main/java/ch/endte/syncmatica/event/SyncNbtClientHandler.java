package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.ISyncNbtClientListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class SyncNbtClientHandler implements ISyncNbtClientManager
{
    private static final SyncNbtClientHandler INSTANCE = new SyncNbtClientHandler();
    private final List<ISyncNbtClientListener> handlers = new ArrayList<>();
    public static ISyncNbtClientManager getInstance() { return INSTANCE; }
    @Override
    public void registerSyncNbtClientHandler(ISyncNbtClientListener handler)
    {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }
    @Override
    public void unregisterSyncNbtClientHandler(ISyncNbtClientListener handler)
    {
        this.handlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void sendSyncNbtClient(NbtCompound data)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncNbtClientListener handler : this.handlers)
            {
                handler.sendSyncNbtClient(data);
            }
        }
    }
    public void receiveSyncNbtClient(NbtCompound data, ClientPlayNetworking.Context ctx)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncNbtClientListener handler : this.handlers)
            {
                handler.receiveSyncNbtClient(data, ctx);
            }
        }
    }
}
