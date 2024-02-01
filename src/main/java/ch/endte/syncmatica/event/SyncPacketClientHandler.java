package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.ISyncPacketClientListener;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class SyncPacketClientHandler implements ISyncPacketClientManager
{
    private static final SyncPacketClientHandler INSTANCE = new SyncPacketClientHandler();
    private final List<ISyncPacketClientListener> handlers = new ArrayList<>();
    public static ISyncPacketClientManager getInstance() { return INSTANCE; }
    @Override
    public void registerSyncPacketClientHandler(ISyncPacketClientListener handler)
    {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }
    @Override
    public void unregisterSyncPacketClientHandler(ISyncPacketClientListener handler)
    {
        this.handlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void sendSyncPacketClient(SyncByteBuf data)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncPacketClientListener handler : this.handlers)
            {
                handler.sendSyncPacketClient(data);
            }
        }
    }
    public void receiveSyncPacketClient(SyncByteBuf data, ClientPlayNetworking.Context ctx)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncPacketClientListener handler : this.handlers)
            {
                handler.receiveSyncPacketClient(data, ctx);
            }
        }
    }
}
