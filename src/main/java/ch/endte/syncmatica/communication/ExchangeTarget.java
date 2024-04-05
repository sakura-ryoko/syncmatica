package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.exchange.Exchange;
import ch.endte.syncmatica.network.client.ClientNetworkPlayHandler;
import ch.endte.syncmatica.network.server.ServerNetworkPlayHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.payload.channels.SyncNbtData;
import ch.endte.syncmatica.util.PayloadUtils;
import ch.endte.syncmatica.util.SyncLog;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// since Client/Server PlayNetworkHandler are 2 different classes, but I want to use exchanges
// on both without having to recode them individually, I have an adapter class here
public class ExchangeTarget
{
    public final ClientPlayNetworkHandler clientPlayNetworkHandler;
    public final ServerPlayNetworkHandler serverPlayNetworkHandler;
    private final String persistentName;
    private FeatureSet features;
    private final List<Exchange> ongoingExchanges = new ArrayList<>(); // implicitly relies on priority

    public ExchangeTarget(ClientPlayNetworkHandler clientPlayContext)
    {
        this.clientPlayNetworkHandler = clientPlayContext;
        this.serverPlayNetworkHandler = null;
        this.persistentName = StringUtils.getWorldOrServerName();
    }

    public ExchangeTarget(ServerPlayNetworkHandler serverPlayContext)
    {
        this.clientPlayNetworkHandler = null;
        this.serverPlayNetworkHandler = serverPlayContext;
        this.persistentName = serverPlayContext.getPlayer().getUuidAsString();
    }

    // this application exclusively communicates in CustomPayLoad packets
    // this class handles the sending of either S2C or C2S packets
    public void sendPacket(final PacketType type, final PacketByteBuf packet, final Context context)
    {
        //SyncLog.debug("ExchangeTarget#sendPacket(): invoked.");
        if (context != null) {
            context.getDebugService().logSendPacket(type, persistentName);
        }
        if (type.equals(PacketType.NBT_DATA))
        {
            SyncLog.error("ExchangeTarget#sendPacket(): NBT PacketType rejected.");
            return;
        }
        final SyncByteBuf buf = PayloadUtils.fromByteBuf(packet);
        CustomPayload payload = PayloadUtils.getPayload(type, buf);

        /**
         * The Fabric API call mode sometimes fails here, because the channels might not be registered in PLAY mode, especially for Single Player.
         */
        if (clientPlayNetworkHandler != null)
        {
            //SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), buf.readableBytes());
            ClientNetworkPlayHandler.sendSyncPacket(payload, clientPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), buf.readableBytes());
            //ClientNetworkPlayHandler.sendSyncPacket(payload);
        }
        if (serverPlayNetworkHandler != null)
        {
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            //SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), buf.readableBytes(), player.getName().getLiteralString());
            ServerNetworkPlayHandler.sendSyncPacket(payload, serverPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), buf.readableBytes(), player.getName().getLiteralString());
            //ServerNetworkPlayHandler.sendSyncPacket(payload, player);
        }
    }

    /**
     * Added an entire NbtCompound packet type chain for future growth.
     * PacketType.NBT_DATA
     * -
     * NBT Packets are as flexible as a PacketByteBuf, and are used by many other mods, such as Carpet and ServUX.
     * Their "Packet Types" are controlled by an Int value by a simple nbt.getInt("packetType") call to avoid the channel
     * registration issue.  Example: CarpetPayload ("carpet:hello") -> NbtCompound nbt.getInt("69") == Carpet "HI" packet,
     * then nbt.getString("version") for the Carpet version number, then the Carpet Client responds with nbt.putInt("420"),
     * and then nbt.putString("version") to reply with its own mod version number.
     * -
     * SyncNbtData.KEY contains a common "key" value for wrapping "data" into a particular field.
     * If the entire Syncmatica Protocol ever gets "overhauled" it should be converted to a NbtCompound data framework
     * using a single play channel, IMO.  With this in place, it *could* in theory get slowly implemented, while preserving
     * backwards compatibility.  It would need a new "PacketType" interface for Nbt packets to get defined, then coding
     * it into the existing checkPacket() and handle() interfaces.
     */
    public void sendPacket(final PacketType type, final NbtCompound data, final Context context)
    {
        //SyncLog.debug("ExchangeTarget#sendPacket(): invoked.");
        if (context != null) {
            context.getDebugService().logSendPacket(type, persistentName);
        }
        if (!type.equals(PacketType.NBT_DATA))
        {
            SyncLog.error("ExchangeTarget#sendPacket(): Non-NBT PacketType rejected.");
            return;
        }

        /**
         * The Fabric API call mode sometimes fails here, because the channels might not be registered yet in PLAY mode, especially for Single Player.
         */
        SyncNbtData payload = new SyncNbtData(data);
        if (clientPlayNetworkHandler != null)
        {
            //SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), data.getSizeInBytes());
            ClientNetworkPlayHandler.sendSyncPacket(payload, clientPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), data.getSizeInBytes());
            //ClientNetworkPlayHandler.sendSyncPacket(payload);
        }
        if (serverPlayNetworkHandler != null)
        {
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            //SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), data.getSizeInBytes(), player.getName().getLiteralString());
            ServerNetworkPlayHandler.sendSyncPacket(payload, serverPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), data.getSizeInBytes(), player.getName().getLiteralString());
            //ServerNetworkPlayHandler.sendSyncPacket(payload, player);
        }
    }

    // removed equals code due to issues with Collection.contains
    public FeatureSet getFeatureSet() { return features; }

    public void setFeatureSet(final FeatureSet f) { features = f; }

    public Collection<Exchange> getExchanges() { return ongoingExchanges; }

    public String getPersistentName() { return persistentName; }

    public boolean isServer() { return serverPlayNetworkHandler != null; }

    public boolean isClient() { return clientPlayNetworkHandler != null; }
}
