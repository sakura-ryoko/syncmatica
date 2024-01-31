package ch.endte.syncmatica.event;

import ch.endte.syncmatica.interfaces.ISyncmaticaPayloadServerListener;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SyncmaticaPayloadServerHandler implements ISyncmaticaPayloadServerManager
{
    private static final SyncmaticaPayloadServerHandler INSTANCE = new SyncmaticaPayloadServerHandler();
    private final List<ISyncmaticaPayloadServerListener> handlers = new ArrayList<>();
    public static ISyncmaticaPayloadServerManager getInstance() { return INSTANCE; }
    @Override
    public void registerSyncmaticaServerHandler(ISyncmaticaPayloadServerListener handler)
    {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
        }
    }
    @Override
    public void unregisterSyncmaticaServerHandler(ISyncmaticaPayloadServerListener handler)
    {
        this.handlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void receiveSyncmaticaServerPayload(NbtCompound data, ServerPlayNetworking.Context ctx)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadServerListener handler : this.handlers)
            {
                handler.receiveSyncmaticaServerPayload(data, ctx);
            }
        }
    }
    public void sendSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadServerListener handler : this.handlers)
            {
                handler.sendSyncmaticaServerPayload(data, player);
            }
        }
    }
    public void encodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player, SyncmaticaPacketType type)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadServerListener handler : this.handlers)
            {
                handler.encodeSyncmaticaServerPayload(data, player, type);
            }
        }
    }
    public void decodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncmaticaPayloadServerListener handler : this.handlers)
            {
                handler.decodeSyncmaticaServerPayload(data, player);
            }
        }
    }
}
