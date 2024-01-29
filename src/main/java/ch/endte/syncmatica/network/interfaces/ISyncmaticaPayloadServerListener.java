package ch.endte.syncmatica.network.interfaces;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ISyncmaticaPayloadServerListener
{
    // Server
    default void sendSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player) { }
    default void receiveSyncmaticaServerPayload(NbtCompound data, ServerPlayNetworking.Context ctx) { }
    default void encodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player) { }
    default void decodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player) { }
}
