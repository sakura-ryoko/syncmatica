package ch.endte.syncmatica.interfaces;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ISyncNbtServerListener
{
    // Server
    default void receiveSyncNbtServer(NbtCompound data, ServerPlayNetworking.Context ctx) { }
    default void sendSyncNbtServer(NbtCompound data, ServerPlayerEntity player) { }
}
