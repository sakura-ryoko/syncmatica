package ch.endte.syncmatica.network.handler;

import javax.annotation.Nonnull;
import ch.endte.syncmatica.network.actor.IServerPlay;
import ch.endte.syncmatica.network.payload.SyncData;
import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Network packet senders / receivers (Server Context)
 */
public abstract class ServerPlayHandler
{
    public static void decodeSyncData(@Nonnull SyncData data, @Nonnull ServerPlayNetworkHandler handler)
    {
        IServerPlay iDo = ((IServerPlay) handler);
        iDo.syncmatica$operateComms(sm -> sm.onPacket(iDo.syncmatica$getExchangeTarget(), data.getType(), data.getPacket()));
    }

    public static void decodeSyncData(@Nonnull SyncData data, @Nonnull IServerPlay iDo)
    {
        iDo.syncmatica$operateComms(sm -> sm.onPacket(iDo.syncmatica$getExchangeTarget(), data.getType(), data.getPacket()));
    }

    public static void encodeSyncData(@Nonnull SyncData data, @Nonnull ServerPlayNetworkHandler handler)
    {
        sendSyncPacket(new SyncmaticaPayload(data), handler);
    }

    public static void encodeSyncData(@Nonnull SyncData data, @Nonnull ServerPlayerEntity player)
    {
        sendSyncPacket(new SyncmaticaPayload(data), player);
    }

    public static void receiveSyncPayload(SyncmaticaPayload payload, ServerPlayNetworking.Context context)
    {
        decodeSyncData(payload.data(), context.player().networkHandler);
    }

    public static <T extends CustomPayload> void sendSyncPacket(@Nonnull T payload, @Nonnull ServerPlayerEntity player)
    {
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static <T extends CustomPayload> void sendSyncPacket(@Nonnull T payload, @Nonnull ServerPlayNetworkHandler handler)
    {
        Packet<?> packet = new CustomPayloadS2CPacket(payload);
        if (handler.accepts(packet))
        {
            handler.sendPacket(packet);
        }
    }
}
