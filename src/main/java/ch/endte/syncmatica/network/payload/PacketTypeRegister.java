package ch.endte.syncmatica.network.payload;

import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * This is a Simplified version of my channel registration code that calls the Fabric API Networking to handle it.
 */
public class PacketTypeRegister
{
    /**
     * This is how it looks in the static context per a MOD,
     * Where each channel must each include its own Custom Payload Records.
     * The send/receive handlers can be made into an interface method
     */
    private static boolean playRegistered = false;

    public static <T extends CustomPayload> void registerPlayChannel(CustomPayload.Id<T> id, PacketCodec<PacketByteBuf, T> codec)
    {
        // Checks with Fabric APIs IMPL layer (I don't think they are confident with their code yet)
        if (PayloadTypeRegistryImpl.PLAY_S2C.get(id) != null || PayloadTypeRegistryImpl.PLAY_C2S.get(id) != null)
        {
            // This just saved Minecraft from crashing, your welcome.
            SyncLog.error("registerPlayChannel(): blocked duplicate Play Channel registration attempt for: {}.", id.id().toString());
        }
        else
        {
            PayloadTypeRegistry.playC2S().register(id, codec);
            PayloadTypeRegistry.playS2C().register(id, codec);
            // Both directions S2C / C2S need to get registered.  The "configuration" channel might not be needed, though
        }
    }
    public static void registerPlayChannels()
    {
        // Don't invoke more than once
        if (playRegistered)
            return;

        SyncLog.debug("PacketTypeRegister#registerPlayChannels(): registering play channels.");
        registerPlayChannel(SyncCancelShare.TYPE, SyncCancelShare.CODEC);
        registerPlayChannel(SyncCancelLitematic.TYPE, SyncCancelLitematic.CODEC);
        registerPlayChannel(SyncConfirmUser.TYPE, SyncConfirmUser.CODEC);
        registerPlayChannel(SyncFeature.TYPE, SyncFeature.CODEC);
        registerPlayChannel(SyncFeatureRequest.TYPE, SyncFeatureRequest.CODEC);
        registerPlayChannel(SyncFinishedLitematic.TYPE, SyncFinishedLitematic.CODEC);
        registerPlayChannel(SyncMessage.TYPE, SyncMessage.CODEC);
        registerPlayChannel(SyncModify.TYPE, SyncModify.CODEC);
        registerPlayChannel(SyncModifyFinish.TYPE, SyncModifyFinish.CODEC);
        registerPlayChannel(SyncModifyRequest.TYPE, SyncModifyRequest.CODEC);
        registerPlayChannel(SyncModifyRequestAccept.TYPE, SyncModifyRequestAccept.CODEC);
        registerPlayChannel(SyncModifyRequestDeny.TYPE, SyncModifyRequestDeny.CODEC);
        registerPlayChannel(SyncReceivedLitematic.TYPE, SyncReceivedLitematic.CODEC);
        registerPlayChannel(SyncRegisterMetadata.TYPE, SyncRegisterMetadata.CODEC);
        registerPlayChannel(SyncRegisterVersion.TYPE, SyncRegisterVersion.CODEC);
        registerPlayChannel(SyncRemoveSyncmatic.TYPE, SyncRemoveSyncmatic.CODEC);
        registerPlayChannel(SyncRequestDownload.TYPE, SyncRequestDownload.CODEC);
        registerPlayChannel(SyncSendLitematic.TYPE, SyncSendLitematic.CODEC);
        registerPlayChannel(SyncNbtData.TYPE, SyncNbtData.CODEC);

        playRegistered = true;
    }
}
