package ch.endte.syncmatica.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.exchange.Exchange;
import ch.endte.syncmatica.network.handler.ClientPlayHandler;
import ch.endte.syncmatica.network.handler.ServerPlayHandler;
import ch.endte.syncmatica.network.PacketType;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import fi.dy.masa.malilib.util.StringUtils;

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
    /**
     * The Fabric API call mode sometimes fails here, because the channels might not be registered in PLAY mode, especially for Single Player.
     */
    public void sendPacket(final PacketType type, final PacketByteBuf byteBuf, final Context context)
    {
        //SyncLog.debug("ExchangeTarget#sendPacket(): invoked.");
        if (context != null) {
            context.getDebugService().logSendPacket(type, persistentName);
        }
        final SyncmaticaPacket newPacket = new SyncmaticaPacket(type.getId(), byteBuf);

        if (newPacket.getType() == null)
        {
            Syncmatica.LOGGER.error("ExchangeTarget#sendPacket(): error, PacketType {} resulted in a null Payload", type.toString());
            return;
        }
        if (clientPlayNetworkHandler != null)
        {
            //SyncLog.debug("ExchangeTarget#sendPacket(): in Client Context, packet type: {}, size in bytes: {}", type.getId().toString(), buf.readableBytes());
            ClientPlayHandler.encodeSyncData(newPacket, clientPlayNetworkHandler);
        }
        if (serverPlayNetworkHandler != null)
        {
            //ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            //SyncLog.debug("ExchangeTarget#sendPacket(): in Server Context, packet type: {}, size in bytes: {} to player: {}", type.getId().toString(), buf.readableBytes(), player.getName().getLiteralString());
            ServerPlayHandler.encodeSyncData(newPacket, serverPlayNetworkHandler);
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
