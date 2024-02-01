package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.exchange.Exchange;
import ch.endte.syncmatica.features.FeatureSet;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import ch.endte.syncmatica.network.payload.SyncmaticaPayload;
import ch.endte.syncmatica.util.PayloadUtils;
import ch.endte.syncmatica.util.SyncLog;
import fi.dy.masa.malilib.util.StringUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public final ClientPlayNetworking.Context clientPlayContext;
    public final ServerPlayNetworking.Context serverPlayContext;
    private final String persistentName;
    private FeatureSet features;
    private final List<Exchange> ongoingExchanges = new ArrayList<>(); // implicitly relies on priority

    public ExchangeTarget(ClientPlayNetworking.Context clientPlayContext)
    {
        this.clientPlayContext = clientPlayContext;
        this.serverPlayContext = null;
        this.persistentName = StringUtils.getWorldOrServerName();
    }

    public ExchangeTarget(ServerPlayNetworking.Context serverPlayContext)
    {
        this.clientPlayContext = null;
        this.serverPlayContext = serverPlayContext;
        this.persistentName = serverPlayContext.player().getUuidAsString();
    }

    // this application exclusively communicates in CustomPayLoad packets
    // this class handles the sending of either S2C or C2S packets
    public void sendPacket(final SyncmaticaPacketType type, final PacketByteBuf packetBuf, final Context context)
    {
        if (context != null) {
            context.getDebugService().logSendPacket(type, persistentName);
        }
        // #FIXME -- these calls are inverted.
        //  "clientPlayContext" is used to SEND to a player,
        //  while "serverPlayContext" is used to send to the Server.
        //
        if (clientPlayContext != null)
        {
            // #FIXME
            //CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new SyncmaticaPayload(id, packetBuf));
            //clientPlayNetworkHandler.sendPacket(packet);
            NbtCompound payload;
            payload = PayloadUtils.fromByteBuf(packetBuf, SyncmaticaPayload.KEY);
            assert payload != null;
            payload.putInt("version", SyncmaticaPacketType.SYNCMATICA_PROTOCOL_VERSION.hashCode());
            payload.putString("packetType", type.toString());
            SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, packet type: {}, size in bytes: {}", type.toString(), payload.getSizeInBytes());
            // FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            // ((SyncmaticaPayloadHandler) SyncmaticaPayloadHandler.getInstance()).encodeSyncmaticaPayload(payload);
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //clientPlayContext.client().getNetworkHandler().sendPacket(ClientNetworkingImpl.createC2SPacket(packet));
        }
        if (serverPlayContext != null)
        {
            // #FIXME
            //CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new SyncmaticaPayload(id, packetBuf));
            //serverPlayNetworkHandler.sendPacket(packet);
            NbtCompound payload;
            payload = PayloadUtils.fromByteBuf(packetBuf, SyncmaticaPayload.KEY);
            assert payload != null;
            payload.putInt("version", SyncmaticaPacketType.SYNCMATICA_PROTOCOL_VERSION.hashCode());
            payload.putString("packetType", type.toString());
            SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, packet type: {}, size in bytes: {}", type.toString(), payload.getSizeInBytes());
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

    public boolean isServer() { return serverPlayContext != null; }

    public boolean isClient() { return clientPlayContext != null; }
}
