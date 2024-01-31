package ch.endte.syncmatica.interfaces;

import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface ISyncmaticaPayloadServerListener
{
    // Server
    default void receiveSyncmaticaServerPayload(NbtCompound data, ServerPlayNetworking.Context ctx) { }
    default void sendSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player) { }
    default void encodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player, SyncmaticaPacketType type) { }
    default void decodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player) { }
}
