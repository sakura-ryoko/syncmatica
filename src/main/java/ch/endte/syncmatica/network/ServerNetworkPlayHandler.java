package ch.endte.syncmatica.network;

import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * canSend()
 * Wraps: canSend(player.networkHandler, payload.getId().id());
 * --> Wraps Internally as:
 * `--> ServerNetworkingImpl.getAddon(player.networkHandler).getSendableChannels().contains(payload.getId().id());
 * send()
 * Wraps internally as:
 * --> player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(payload));
 */
public abstract class ServerNetworkPlayHandler
{
    // Simple unified "sendPacket()" method
    // --> It doesn't care what payload/packet type it is.
    public static <T extends CustomPayload> void sendSyncPacket(T payload, ServerPlayerEntity player)
    {
        // Client-bound packet sent by the Server
        if (ServerPlayNetworking.canSend(player, payload.getId()))
        {
            ServerPlayNetworking.send(player, payload);
            SyncLog.debug("ServerNetworkPlayHandler#sendSyncPacket(): sending payload id: {} to player: {}", payload.getId(), player.getName().getLiteralString());
        }
    }

    // Server-bound packet sent by a Client
    public static void receiveSyncPacket(PacketType type, SyncByteBuf data, ServerPlayNetworkHandler handler, ServerPlayerEntity player)
    {
        SyncLog.debug("ServerNetworkPlayHandler#receiveCancelShare(): received payload id: {}, size in bytes {}", type.toString(), data.readableBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveCancelShare(): payload.readString(): {}", data.readString(256));
    }
    public static void receiveSyncNbt(PacketType type, NbtCompound data, ServerPlayNetworkHandler handler, ServerPlayerEntity player)
    {
        SyncLog.debug("ServerNetworkPlayHandler#receiveCancelShare(): received payload id: {}, size in bytes {}", type.toString(), data.getSizeInBytes());
        SyncLog.debug("ServerNetworkPlayHandler#receiveCancelShare(): payload.readString(): {}", data.getString(SyncmaticaNbtData.KEY));
    }
    public static void receiveCancelShare(CancelShare data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CANCEL_SHARE, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveConfirmUser(ConfirmUser data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.CONFIRM_USER, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveFeature(Feature data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveFeatureRequest(FeatureRequest data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FEATURE_REQUEST, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveFinishedLitematic(FinishedLitematic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.FINISHED_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveMessage(Message data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MESSAGE, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModify(Modify data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyFinish(ModifyFinish data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_FINISH, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyRequest(ModifyRequest data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyRequestAccept(ModifyRequestAccept data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_ACCEPT, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveModifyRequestDeny(ModifyRequestDeny data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.MODIFY_REQUEST_DENY, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveReceivedLitematic(ReceivedLitematic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.RECEIVED_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveRegisterMetadata(RegisterMetadata data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_METADATA, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveRegisterVersion(RegisterVersion data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REGISTER_VERSION, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveRemoveSyncmatic(RemoveSyncmatic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REMOVE_SYNCMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }
    public static void receiveRequestDownload(RequestDownload data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.REQUEST_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveSendLitematic(SendLitematic data, ServerPlayNetworking.Context context)
    {
        receiveSyncPacket(PacketType.SEND_LITEMATIC, data.byteBuf(), context.player().networkHandler, context.player());
    }

    public static void receiveSyncNbtData(SyncmaticaNbtData data, ServerPlayNetworking.Context context)
    {
        receiveSyncNbt(PacketType.NBT_DATA, data.data(), context.player().networkHandler, context.player());
    }
}
