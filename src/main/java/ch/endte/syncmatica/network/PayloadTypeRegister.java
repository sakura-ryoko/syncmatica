package ch.endte.syncmatica.network;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.network.payload.*;
import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PayloadTypeRegister
{
    // This is how it looks in the static context per a MOD, which must each include its own Custom Payload Records.
    // --> The send/receive handlers can be made into an interface.
    private static final Map<PayloadType, PayloadCodec> TYPES = new HashMap<>();
    public static Identifier getIdentifier(PayloadType type)
    {
        return TYPES.get(type).getId();
    }
    public static String getKey(PayloadType type)
    {
        return TYPES.get(type).getKey();
    }
    private static boolean typesRegistered = false;
    private static boolean playRegistered = false;
    public static void registerDefaultType(PayloadType type, String key, String namespace)
    {
        if (!TYPES.containsKey(type))
        {
            PayloadCodec codec = new PayloadCodec(type, key, namespace);
            TYPES.put(type, codec);
            SyncLog.debug("PayloadTypeRegister#registerDefaultType(): Successfully registered new Payload id: {} // {}:{}", codec.getId().hashCode(), codec.getId().getNamespace(), codec.getId().getPath());
        }
    }
    public static void registerType(PayloadType type, String key, String namespace, String path)
    {
        if (!TYPES.containsKey(type))
        {
            PayloadCodec codec = new PayloadCodec(type, key, namespace, path);
            TYPES.put(type, codec);
            SyncLog.debug("PayloadTypeRegister#registerDefaultType(): Successfully registered new Payload id: {} // {}:{}", codec.getId().hashCode(), codec.getId().getNamespace(), codec.getId().getPath());
        }
    }
    public static void registerTypes(String name)
    {
        // Don't invoke more than once
        if (typesRegistered)
            return;
        SyncLog.debug("PayloadTypeRegister#registerDefaultTypes(): executing.");

        String namespace = name;
        if (namespace.isEmpty())
            namespace = SyncmaticaReference.MOD_ID;

        //registerDefaultType(PayloadType.STRING, "string", namespace);
        //registerDefaultType(PayloadType.DATA, "data", namespace);
        // For Carpet "hello" packet (NbtCompound type)
        //registerType(fi.dy.masa.malilib.network.PayloadType.CARPET_HELLO, "hello", "carpet", "hello");
        //registerType(fi.dy.masa.malilib.network.PayloadType.SERVUX, "structure_bounding_boxes", "servux", "structures");
        registerType(PayloadType.SYNCMATICA, "syncmatic", "syncmatica", "syncmatics");

        typesRegistered = true;
    }
    public static <T extends CustomPayload> void registerPlayChannel(CustomPayload.Id<T> id, PacketCodec<PacketByteBuf, T> codec)
    {
        PayloadTypeRegistry.playC2S().register(id, codec);
        PayloadTypeRegistry.playS2C().register(id, codec);
    }
    public static void registerPlayChannels()
    {
        // Don't invoke more than once
        if (playRegistered)
            return;
        SyncLog.debug("PayloadTypeRegister#registerPlayChannels(): registering play channels.");
        //registerPlayChannel(DataPayload.TYPE, DataPayload.CODEC);
        //registerPlayChannel(StringPayload.TYPE, StringPayload.CODEC);
        //registerPlayChannel(fi.dy.masa.malilib.network.payload.CarpetPayload.TYPE, fi.dy.masa.malilib.network.payload.CarpetPayload.CODEC);
        //registerPlayChannel(fi.dy.masa.malilib.network.payload.ServuxPayload.TYPE, ServuxPayload.CODEC);
        registerPlayChannel(SyncmaticaPayload.TYPE, SyncmaticaPayload.CODEC);

        playRegistered = true;
    }
}
