package ch.endte.syncmatica.network.payload;

import ch.endte.syncmatica.network.payload.channels.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * This is a Simplified version of my channel registration code that calls the Fabric API Networking.
 */
public class PacketTypeRegister
{
    // This is how it looks in the static context per a MOD,
    // Where each channel must each include its own Custom Payload Records.
    // --> The send/receive handlers can be made into an interface method
    private static boolean playRegistered = false;
    public static <T extends CustomPayload> void registerPlayChannel(CustomPayload.Id<T> id, PacketCodec<PacketByteBuf, T> codec)
    {
        SyncLog.debug("PacketTypeRegister#registerPlayChannel(): register {} // {}:{}", id.hashCode(), id.id().getNamespace(), id.id().getPath());
        PayloadTypeRegistry.configurationC2S().register(id, codec);
        PayloadTypeRegistry.configurationS2C().register(id, codec);
        PayloadTypeRegistry.playC2S().register(id, codec);
        PayloadTypeRegistry.playS2C().register(id, codec);
        // Both directions need to get registered.  The "configuration" mode might not be needed
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
