package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.ISyncPacketServerListener;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class SyncPacketServerHandler implements ISyncPacketServerManager
{
    private static final SyncPacketServerHandler INSTANCE = new SyncPacketServerHandler();
    private final List<ISyncPacketServerListener> handlers = new ArrayList<>();
    public static ISyncPacketServerManager getInstance() { return INSTANCE; }
    @Override
    public void registerSyncPacketServerHandler(ISyncPacketServerListener handler)
    {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }
    @Override
    public void unregisterSyncPacketServerHandler(ISyncPacketServerListener handler)
    {
        this.handlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void receiveSyncPacketServer(SyncByteBuf data, ServerPlayNetworking.Context ctx)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncPacketServerListener handler : this.handlers)
            {
                handler.receiveSyncPacketServer(data, ctx);
            }
        }
    }
    public void sendSyncPacketServer(SyncByteBuf data, ServerPlayerEntity player)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncPacketServerListener handler : this.handlers)
            {
                handler.sendSyncPacketServer(data, player);
            }
        }
    }
}
