package ch.endte.syncmatica.interfaces;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ISyncPacketServerListener
{
    // Server
    default void receiveSyncPacketServer(SyncByteBuf data, ServerPlayNetworking.Context ctx) { }
    default void sendSyncPacketServer(SyncByteBuf data, ServerPlayerEntity player) { }
}
