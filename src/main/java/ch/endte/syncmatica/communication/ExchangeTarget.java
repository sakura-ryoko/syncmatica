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
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// since Client/Server PlayNetworkHandler are 2 different classes, but I want to use exchanges
// on both without having to recode them individually, I have an adapter class here
/**
 * Lots of testing required to figure out which API call works best.
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
    public void sendPacket(final PacketType type, final PacketByteBuf packet, final Context context)
    {
        SyncLog.debug("ExchangeTarget#sendPacket(): invoked.");
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
        // #FIXME -- these calls are inverted.
        //  "clientPlayContext" is used to SEND to a player,
        //  while "serverPlayContext" is used to send to the Server.
        //
        if (clientPlayNetworkHandler != null)
        {
            // #FIXME -- Which method works best?
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), buf.readableBytes());
            ClientNetworkPlayHandler.sendSyncPacket(payload, clientPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), buf.readableBytes());
            //assert payload != null;
            //ClientNetworkPlayHandler.sendSyncPacket(payload);

            // FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //clientPlayContext.client().getNetworkHandler().sendPacket(ClientNetworkingImpl.createC2SPacket(packet));
        }
        if (serverPlayNetworkHandler != null)
        {
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            // #FIXME -- Which method works best?
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), buf.readableBytes(), player.getName().getLiteralString());
            ServerNetworkPlayHandler.sendSyncPacket(payload, serverPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), buf.readableBytes(), player.getName().getLiteralString());
            //assert payload != null;
            //ServerNetworkPlayHandler.sendSyncPacket(payload, player);

            // #FIXME : Internally under Fabric, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            //ServerPlayNetworking.Context serverPlayContext = null;
            //serverPlayContext.responseSender().sendPacket(packet);
        }
    }
    public void sendPacket(final PacketType type, final NbtCompound data, final Context context)
    {
        SyncLog.debug("ExchangeTarget#sendPacket(): invoked.");
        if (context != null) {
            context.getDebugService().logSendPacket(type, persistentName);
        }
        if (!type.equals(PacketType.NBT_DATA))
        {
            SyncLog.error("ExchangeTarget#sendPacket(): Non-NBT PacketType rejected.");
            return;
        }
        SyncmaticaNbtData payload = new SyncmaticaNbtData(data);
        // #FIXME -- these calls are inverted ?
        //  "clientPlayContext" is used to SEND to a player,
        //  while "serverPlayContext" is used to send to the Server.
        //
        if (clientPlayNetworkHandler != null)
        {
            // #FIXME -- Which method works best?
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), data.getSizeInBytes());
            ClientNetworkPlayHandler.sendSyncPacket(payload, clientPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), data.getSizeInBytes());
            //ClientNetworkPlayHandler.sendSyncPacket(payload);

            // FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            // ((SyncmaticaPayloadHandler) SyncmaticaPayloadHandler.getInstance()).encodeSyncmaticaPayload(payload);
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //clientPlayContext.client().getNetworkHandler().sendPacket(ClientNetworkingImpl.createC2SPacket(packet));
        }
        if (serverPlayNetworkHandler != null)
        {
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            // #FIXME
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), data.getSizeInBytes(), player.getName().getLiteralString());
            ServerNetworkPlayHandler.sendSyncPacket(payload, serverPlayNetworkHandler);

            //SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), data.getSizeInBytes(), player.getName().getLiteralString());
            //ServerNetworkPlayHandler.sendSyncPacket(payload, player);

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
