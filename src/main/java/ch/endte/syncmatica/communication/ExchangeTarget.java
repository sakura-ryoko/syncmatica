package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.exchange.Exchange;
import ch.endte.syncmatica.features.FeatureSet;
import ch.endte.syncmatica.network.ClientNetworkPlayHandler;
import ch.endte.syncmatica.network.ServerNetworkPlayHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.SyncByteBuf;
import ch.endte.syncmatica.network.payload.channels.SyncmaticaNbtData;
import ch.endte.syncmatica.util.PayloadUtils;
import ch.endte.syncmatica.util.SyncLog;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

// since Client/Server PlayNetworkHandler are 2 different classes, but I want to use exchanges
// on both without having to recode them individually, I have an adapter class here

/**
 * Refactor Packets to use NbtCompound instead of PacketByteBuf before they hit Network API
 * Also, we shouldn't be using the " Server/ClientPlayNetworkHandler " to send packets, i.e.
 * "Exchange Target" value.  We'll just use the new Context interface, then call the correct
 * Packet Handler in the new Network API.
 */
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
    public void sendPacket(final PacketType type, final PacketByteBuf packetBuf, final Context context)
    {
        if (context != null) {
            context.getDebugService().logSendPacket(type, persistentName);
        }
        if (type.equals(PacketType.NBT_DATA))
        {
            SyncLog.error("ExchangeTarget#sendPacket(): NBT PacketType rejected.");
            return;
        }
        // #FIXME -- these calls are inverted.
        //  "clientPlayContext" is used to SEND to a player,
        //  while "serverPlayContext" is used to send to the Server.
        //
        if (clientPlayNetworkHandler != null)
        {
            // #FIXME
            //CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new SyncmaticaPayload(id, packetBuf));
            //clientPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, packet type: {}, size in bytes: {}", type.toString(), packetBuf.readableBytes());
            try {
                ClientNetworkPlayHandler.sendSyncPacket(Objects.requireNonNull(PayloadUtils.getPayload(type, (SyncByteBuf) packetBuf)));
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, Caught Null Exception.");
            }

            //NbtCompound payload;
            //payload = PayloadUtils.fromByteBuf(packetBuf, SyncmaticaNbtData.KEY);
            //assert payload != null;
            //payload.putInt("version", PacketType.SYNCMATICA_PROTOCOL_VERSION.hashCode());
            //payload.putString("packetType", type.toString());
            // FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            // ((SyncmaticaPayloadHandler) SyncmaticaPayloadHandler.getInstance()).encodeSyncmaticaPayload(payload);
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //clientPlayContext.client().getNetworkHandler().sendPacket(ClientNetworkingImpl.createC2SPacket(packet));
        }
        if (serverPlayNetworkHandler != null)
        {
            // #FIXME
            //CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new SyncmaticaPayload(id, packetBuf));
            //serverPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, packet type: {}, size in bytes: {} to player: {}", type.toString(), packetBuf.readableBytes(), serverPlayNetworkHandler.getPlayer());
            try {
                ServerNetworkPlayHandler.sendSyncPacket(Objects.requireNonNull(PayloadUtils.getPayload(type, (SyncByteBuf) packetBuf)), serverPlayNetworkHandler.getPlayer());
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, Caught Null Exception.");
            }

            //NbtCompound payload;
            //payload = PayloadUtils.fromByteBuf(packetBuf, SyncmaticaNbtData.KEY);
            //assert payload != null;
            //payload.putInt("version", PacketType.SYNCMATICA_PROTOCOL_VERSION.hashCode());
            //payload.putString("packetType", type.toString());
            //SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, packet type: {}, size in bytes: {}", type.toString(), payload.getSizeInBytes());
            // What player are we sending this to?
            //context.


            // #FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //serverPlayContext.responseSender().sendPacket(packet);
        }
    }
    public void sendPacket(final PacketType type, final NbtCompound data, final Context context)
    {
        if (context != null) {
            context.getDebugService().logSendPacket(type, persistentName);
        }
        if (!type.equals(PacketType.NBT_DATA))
        {
            SyncLog.error("ExchangeTarget#sendPacket(): Non-NBT PacketType rejected.");
            return;
        }
        // #FIXME -- these calls are inverted.
        //  "clientPlayContext" is used to SEND to a player,
        //  while "serverPlayContext" is used to send to the Server.
        //
        if (clientPlayNetworkHandler != null)
        {
            // #FIXME
            //CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new SyncmaticaPayload(id, packetBuf));
            //clientPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, packet type: {}, size in bytes: {}", type.toString(), data.getSizeInBytes());
            try {
                SyncmaticaNbtData payload = new SyncmaticaNbtData(data);
                ClientNetworkPlayHandler.sendSyncPacket(payload);
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, Caught Null Exception.");
            }

            //NbtCompound payload;
            //payload = PayloadUtils.fromByteBuf(packetBuf, SyncmaticaNbtData.KEY);
            //assert payload != null;
            //payload.putInt("version", PacketType.SYNCMATICA_PROTOCOL_VERSION.hashCode());
            //payload.putString("packetType", type.toString());
            // FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            // ((SyncmaticaPayloadHandler) SyncmaticaPayloadHandler.getInstance()).encodeSyncmaticaPayload(payload);
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //clientPlayContext.client().getNetworkHandler().sendPacket(ClientNetworkingImpl.createC2SPacket(packet));
        }
        if (serverPlayNetworkHandler != null)
        {
            // #FIXME
            //CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new SyncmaticaPayload(id, packetBuf));
            //serverPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, packet type: {}, size in bytes: {} to player: {}", type.toString(), data.getSizeInBytes(), serverPlayNetworkHandler.getPlayer());
            try {
                SyncmaticaNbtData payload = new SyncmaticaNbtData(data);
                ServerNetworkPlayHandler.sendSyncPacket(payload, serverPlayNetworkHandler.getPlayer());
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, Caught Null Exception.");
            }

            //NbtCompound payload;
            //payload = PayloadUtils.fromByteBuf(packetBuf, SyncmaticaNbtData.KEY);
            //assert payload != null;
            //payload.putInt("version", PacketType.SYNCMATICA_PROTOCOL_VERSION.hashCode());
            //payload.putString("packetType", type.toString());
            //SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, packet type: {}, size in bytes: {}", type.toString(), payload.getSizeInBytes());
            // What player are we sending this to?
            //context.


            // #FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //serverPlayContext.responseSender().sendPacket(packet);
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
