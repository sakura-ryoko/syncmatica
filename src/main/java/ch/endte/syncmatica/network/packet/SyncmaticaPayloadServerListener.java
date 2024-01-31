package ch.endte.syncmatica.network.packet;

import ch.endte.syncmatica.interfaces.ISyncmaticaPayloadServerListener;
import ch.endte.syncmatica.network.payload.*;
import ch.endte.syncmatica.network.ServerNetworkPlayHandler;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Copy's my ServUX Network API for Servers -- Sakura
 */
public class SyncmaticaPayloadServerListener implements ISyncmaticaPayloadServerListener
{
    public void sendSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        SyncLog.debug("SyncmaticaPayloadServerListener#sendSyncmaticaServerPayload(): sending payload of size: {} to player: {}", data.getSizeInBytes(), player.getName().getLiteralString());
        SyncmaticaPayload payload = new SyncmaticaPayload(data);
        ServerNetworkPlayHandler.sendSyncmaticaServerPayload(payload, player);
    }
    public void receiveSyncmaticaServerPayload(NbtCompound data, ServerPlayNetworking.Context ctx)
    {
        decodeSyncmaticaServerPayload(data, ctx.player());
    }

    public void encodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player, SyncmaticaPacketType type)
    {
        // Client->Server (C2S) encoder
        NbtCompound nbt = new NbtCompound();
        nbt.copyFrom(data);
        nbt.putString("packetType", type.toString());
        SyncLog.debug("SyncmaticaPayloadServerListener#encodeSyncmaticaServerPayload(): encoding payload of size: {} for player {}", data.getSizeInBytes(), player.getName().getLiteralString());
        sendSyncmaticaServerPayload(nbt, player);
    }

    public void decodeSyncmaticaServerPayload(NbtCompound data, ServerPlayerEntity player)
    {
        // Server->Client (S2C) decoder
        String type = data.getString("packetType");
        // Parse to (SyncmaticaPacketType)
        SyncLog.debug("SyncmaticaPayloadServerListener#decodeSyncmaticaServerPayload(): type: {}, decoding payload of size: {} from player {}", type, data.getSizeInBytes(), player.getName().getLiteralString());
        String hello = data.getString(SyncmaticaPayload.KEY);
        SyncLog.debug("SyncmaticaPayloadServerListener#decodeSyncmaticaServerPayload(): type: {}, received: {}", type, hello);

        // Send a response
        NbtCompound nbt = new NbtCompound();
        nbt.putString(SyncmaticaPayload.KEY, "well hello there.");
        encodeSyncmaticaServerPayload(nbt, player, SyncmaticaPacketType.REGISTER_VERSION);
    }
}
