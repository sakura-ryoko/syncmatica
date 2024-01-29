package ch.endte.syncmatica.network.interfaces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class SyncmaticaPayloadClientHandler implements ISyncmaticaPayloadClientManager
{
    private static final SyncmaticaPayloadClientHandler INSTANCE = new SyncmaticaPayloadClientHandler();
    private final List<ISyncmaticaPayloadClientListener> handlers = new ArrayList<>();
    public static ISyncmaticaPayloadClientManager getInstance() { return INSTANCE; }
    @Override
    public void registerSyncmaticaClientHandler(ISyncmaticaPayloadClientListener handler)
    {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }
    @Override
    public void unregisterSyncmaticaClientHandler(ISyncmaticaPayloadClientListener handler)
    {
        this.handlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void receiveSyncmaticaClientPayload(NbtCompound data, ClientPlayNetworking.Context ctx)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadClientListener handler : this.handlers)
            {
                handler.receiveSyncmaticaClientPayload(data, ctx);
            }
        }
    }
    public void sendSyncmaticaClientPayload(NbtCompound data)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadClientListener handler : this.handlers)
            {
                handler.sendSyncmaticaClientPayload(data);
            }
        }
    }
    public void encodeSyncmaticaClientPayload(NbtCompound data)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadClientListener handler : this.handlers)
            {
                handler.encodeSyncmaticaClientPayload(data);
            }
        }
    }
    public void decodeSyncmaticaClientPayload(NbtCompound data)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadClientListener handler : this.handlers)
            {
                handler.decodeSyncmaticaClientPayload(data);
            }
        }
    }
}
