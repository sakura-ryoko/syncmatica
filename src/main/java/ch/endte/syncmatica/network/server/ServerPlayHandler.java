package ch.endte.syncmatica.network.server;

import ch.endte.syncmatica.network.packet.IServerPlay;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.channels.*;
import ch.endte.syncmatica.util.PayloadUtils;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Main Fabric API Networking-based packet senders / receivers (Server Context)
 */
public abstract class ServerPlayHandler
{
    public static <T extends CustomPayload> void sendSyncPacket(T payload, ServerPlayerEntity player)
    {
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
            // networkHandler method
            //player.networkHandler.sendPacket(new CustomPayloadC2SPacket(payload));
            SyncLog.debug("ServerPlayHandler#sendSyncPacket(): [API] sending payload id: {} to player: {}", payload.getId().id().toString(), player.getName().getLiteralString());
        }
        else
          SyncLog.warn("ServerPlayHandler#sendSyncPacket(): [API] can't send packet (not accepted).");
    }

    public static <T extends CustomPayload> void sendSyncPacket(T payload, ServerPlayNetworkHandler handler)
    {
        Packet<?> packet = new CustomPayloadS2CPacket(payload);
        ServerPlayerEntity player = handler.getPlayer();
        if (handler.accepts(packet))
        {
            handler.sendPacket(packet);
            SyncLog.debug("ServerPlayHandler#sendSyncPacket(): [Handler] sending payload id: {} to player: {}", payload.getId().id().toString(), player.getName().getLiteralString());
        }
        else
          SyncLog.warn("ServerPlayHandler#sendSyncPacket(): [Handler] can't send packet (not accepted).");
    }

    public static void receiveSyncPacket(PacketType type, SyncByteBuf data, ServerPlayNetworkHandler handler, ServerPlayerEntity player)
    {
        PacketByteBuf out = PayloadUtils.fromSyncBuf(data);
        SyncLog.debug("ServerPlayHandler#receiveSyncPacket(): received payload id: {}, size in bytes {}", type.getId().toString(), out.readableBytes());

        if (handler == null)
        {
            SyncLog.warn("ServerPlayHandler#receiveSyncPacket(): ignored because handler is null.");
        }
        else
        {
            IServerPlay iDo = ((IServerPlay) handler);
            iDo.syncmatica$operateComms(sm -> sm.onPacket(iDo.syncmatica$getExchangeTarget(), type, out));
        }
    }

    public static void receiveSyncNbt(PacketType type, NbtCompound data, ServerPlayNetworkHandler handler, ServerPlayerEntity player)
    {
        SyncLog.debug("ServerPlayHandler#receiveSyncNbt(): received payload id: {}, size in bytes {}", type.getId().toString(), data.getSizeInBytes());
        SyncLog.debug("ServerPlayHandler#receiveSyncNbt(): payload.readString(): {}", data.getString(SyncNbtData.KEY));

        if (handler == null)
        {
            SyncLog.warn("ServerPlayHandler#receiveSyncPacket(): ignored because handler is null.");
        }
        else
        {
            IServerPlay iDo = ((IServerPlay) handler);
            iDo.syncmatica$operateComms(sm -> sm.onNbtPacket(iDo.syncmatica$getExchangeTarget(), type, data));
        }
    }

    public static void receiveCancelShare(SyncCancelShare data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_SHARE, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveCancelLitematic(SyncCancelLitematic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveConfirmUser(SyncConfirmUser data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CONFIRM_USER, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveFeature(SyncFeature data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveFeatureRequest(SyncFeatureRequest data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE_REQUEST, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveFinishedLitematic(SyncFinishedLitematic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FINISHED_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveMessage(SyncMessage data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MESSAGE, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModify(SyncModify data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyFinish(SyncModifyFinish data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_FINISH, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyRequest(SyncModifyRequest data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyRequestAccept(SyncModifyRequestAccept data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_ACCEPT, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyRequestDeny(SyncModifyRequestDeny data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_DENY, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveReceivedLitematic(SyncReceivedLitematic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.RECEIVED_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveRegisterMetadata(SyncRegisterMetadata data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_METADATA, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveRegisterVersion(SyncRegisterVersion data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_VERSION, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveRemoveSyncmatic(SyncRemoveSyncmatic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REMOVE_SYNCMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }
    public static void receiveRequestDownload(SyncRequestDownload data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REQUEST_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveSendLitematic(SyncSendLitematic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.SEND_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveSyncNbtData(SyncNbtData data, ServerPlayNetworking.Context context)
    {
        receiveSyncNbt(PacketType.NBT_DATA, data.data(), context.player().networkHandler, context.player());
    }
}
