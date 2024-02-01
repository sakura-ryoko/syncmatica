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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        final SyncByteBuf buf = (SyncByteBuf) packetBuf;
        // #FIXME -- these calls are inverted.
        //  "clientPlayContext" is used to SEND to a player,
        //  while "serverPlayContext" is used to send to the Server.
        //
        if (clientPlayNetworkHandler != null)
        {
            // #FIXME -- Which method works best?
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Client Context, packet type: {}, size in bytes: {}", type.toString(), buf.readableBytes());
            CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(PayloadUtils.getPayload(type, buf));
            //CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new SyncmaticaPayload(id, packetBuf));
            clientPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Client Context, packet type: {}, size in bytes: {}", type.toString(), buf.readableBytes());
            try {
                ClientNetworkPlayHandler.sendSyncPacket(Objects.requireNonNull(PayloadUtils.getPayload(type, buf)));
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, Caught Null Exception.");
            }

            // FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            // ((SyncmaticaPayloadHandler) SyncmaticaPayloadHandler.getInstance()).encodeSyncmaticaPayload(payload);
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //clientPlayContext.client().getNetworkHandler().sendPacket(ClientNetworkingImpl.createC2SPacket(packet));
        }
        if (serverPlayNetworkHandler != null)
        {
            // #FIXME -- Which method works best?
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.toString(), buf.readableBytes(), serverPlayNetworkHandler.getPlayer());
            CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(PayloadUtils.getPayload(type, buf));
            //CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new SyncmaticaPayload(id, packetBuf));
            serverPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.toString(), buf.readableBytes(), serverPlayNetworkHandler.getPlayer());
            try {
                ServerNetworkPlayHandler.sendSyncPacket(Objects.requireNonNull(PayloadUtils.getPayload(type, buf)), serverPlayNetworkHandler.getPlayer());
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, Caught Null Exception.");
            }
            // What player are we sending this to?

            // #FIXME : Internally under Fabric, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //ServerPlayNetworking.Context serverPlayContext = null;
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
        // #FIXME -- these calls are inverted ?
        //  "clientPlayContext" is used to SEND to a player,
        //  while "serverPlayContext" is used to send to the Server.
        //
        if (clientPlayNetworkHandler != null)
        {
            // #FIXME -- Which method works best?
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Client Context, packet type: {}, size in bytes: {}", type.toString(), data.getSizeInBytes());
            CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(PayloadUtils.getPayload(type, data));
            //CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new SyncmaticaPayload(id, packetBuf));
            clientPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Client Context, packet type: {}, size in bytes: {}", type.toString(), data.getSizeInBytes());
            try {
                SyncmaticaNbtData payload = new SyncmaticaNbtData(data);
                ClientNetworkPlayHandler.sendSyncPacket(payload);
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, Caught Null Exception.");
            }

            // FIXME : Internally, this is sort of how it functions when calling .send(), which obfuscates the Network Handler interface
            // ((SyncmaticaPayloadHandler) SyncmaticaPayloadHandler.getInstance()).encodeSyncmaticaPayload(payload);
            //SyncmaticaPayload packet = new SyncmaticaPayload(payload);
            //clientPlayContext.client().getNetworkHandler().sendPacket(ClientNetworkingImpl.createC2SPacket(packet));
        }
        if (serverPlayNetworkHandler != null)
        {
            // #FIXME
            SyncLog.debug("ExchangeTarget#sendPacket(): [ORIG] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.toString(), data.getSizeInBytes(), serverPlayNetworkHandler.getPlayer());
            CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(PayloadUtils.getPayload(type, data));
            //CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new SyncmaticaPayload(id, packetBuf));
            serverPlayNetworkHandler.sendPacket(packet);

            SyncLog.debug("ExchangeTarget#sendPacket(): [TEST] in Server Context, packet type: {}, size in bytes: {} to player: {}", type.toString(), data.getSizeInBytes(), serverPlayNetworkHandler.getPlayer());
            try {
                SyncmaticaNbtData payload = new SyncmaticaNbtData(data);
                ServerNetworkPlayHandler.sendSyncPacket(payload, serverPlayNetworkHandler.getPlayer());
            } catch (Exception e) {
                SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, Caught Null Exception.");
            }
            // What player are we sending this to?

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
