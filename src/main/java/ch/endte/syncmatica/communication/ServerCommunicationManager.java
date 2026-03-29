package ch.endte.syncmatica.communication;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import ch.endte.syncmatica.Feature;
import ch.endte.syncmatica.communication.exchange.*;
import ch.endte.syncmatica.data.LocalLitematicState;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.extended_core.PlayerIdentifier;
import ch.endte.syncmatica.network.PacketType;
import io.netty.buffer.Unpooled;

import com.mojang.authlib.GameProfile;

public class ServerCommunicationManager extends CommunicationManager
{
    private final Map<UUID, List<ServerPlacement>> downloadingFile = new HashMap<>();
    private final Map<ExchangeTarget, ServerPlayer> playerMap = new HashMap<>();

    public ServerCommunicationManager() { super(); }

    public GameProfile getGameProfile(final ExchangeTarget exchangeTarget) { return playerMap.get(exchangeTarget).getGameProfile(); }

    @Nullable
    public ExchangeTarget fromExistingPlayer(final ServerPlayer player)
    {
        AtomicReference<ExchangeTarget> newTarget = new AtomicReference<>();

        this.playerMap.forEach(
                (ex, p) ->
                {
                    if (player.getId() == p.getId())
                    {
                        newTarget.set(ex);
                    }
                }
        );

        return newTarget.get();
    }

    public void sendMessage(final ExchangeTarget client, final MessageType msgType, final String identifier)
    {
        if (client.getFeatureSet().hasFeature(Feature.MESSAGE))
        {
            final FriendlyByteBuf newPacketBuf = new FriendlyByteBuf(Unpooled.buffer());
            newPacketBuf.writeUtf(msgType.toString());
            newPacketBuf.writeUtf(identifier);
            client.sendPacket(PacketType.MESSAGE, newPacketBuf, context);
        }
        else if (playerMap.containsKey(client))
        {
            final ServerPlayer player = playerMap.get(client);
            player.sendSystemMessage(Component.nullToEmpty("Syncmatica " + msgType.toString() + " " + identifier), false);
        }
    }

    public void onPlayerJoin(final ExchangeTarget newPlayer, final ServerPlayer player)
    {
        final VersionHandshakeServer hi = new VersionHandshakeServer(newPlayer, context);
        playerMap.put(newPlayer, player);
        final GameProfile profile = player.getGameProfile();
        context.getPlayerIdentifierProvider().updateName(profile.id(), profile.name());
        startExchangeUnchecked(hi);
    }

    public void onPlayerLeave(final ExchangeTarget oldPlayer)
    {
        final Collection<Exchange> potentialMessageTarget = oldPlayer.getExchanges();
        if (potentialMessageTarget != null)
        {
            for (final Exchange target : potentialMessageTarget)
            {
                target.close(false);
                handleExchange(target);
            }
        }
        broadcastTargets.remove(oldPlayer);
        playerMap.remove(oldPlayer);
    }

    @Override
    protected void handle(final ExchangeTarget source, final PacketType type, final FriendlyByteBuf packetBuf)
    {
        if (type.equals(PacketType.REQUEST_LITEMATIC))
        {
            final UUID syncmaticaId = packetBuf.readUUID();
            final ServerPlacement placement = context.getSyncmaticManager().getPlacement(syncmaticaId);
            if (placement == null)
            {
                return;
            }
            final Path toUpload = context.getFileStorage().getLocalLitematic(placement);
            final UploadExchange upload;
            try
            {
                upload = new UploadExchange(placement, toUpload, source, context);
            }
            catch (final FileNotFoundException e)
            {
                // should be fine
                e.printStackTrace();
                return;
            }
            startExchange(upload);
            return;
        }
        if (type.equals(PacketType.REGISTER_METADATA))
        {
            final ServerPlacement placement = receiveMetaData(packetBuf, source);
            if (context.getSyncmaticManager().getPlacement(placement.getId()) != null)
            {
                cancelShare(source, placement);

                return;
            }

            // when the client does not communicate the owner
            final GameProfile profile = playerMap.get(source).getGameProfile();
            final PlayerIdentifier playerIdentifier = context.getPlayerIdentifierProvider().createOrGet(profile);
            if (!placement.getOwner().equals(playerIdentifier))
            {
                placement.setOwner(playerIdentifier);
                placement.setLastModifiedBy(playerIdentifier);
            }

            if (!context.getFileStorage().getLocalState(placement).isLocalFileReady())
            {
                // special edge case because files are transmitted by placement rather than file names/hashes
                if (context.getFileStorage().getLocalState(placement) == LocalLitematicState.DOWNLOADING_LITEMATIC)
                {
                    downloadingFile.computeIfAbsent(placement.getHash(), key -> new ArrayList<>()).add(placement);
                    return;
                }
                try
                {
                    download(placement, source);
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }

                return;
            }

            addPlacement(source, placement);

            return;
        }
        if (type.equals(PacketType.REMOVE_SYNCMATIC))
        {
            final UUID placementId = packetBuf.readUUID();
            final ServerPlacement placement = context.getSyncmaticManager().getPlacement(placementId);
            if (placement != null)
            {
                final Exchange modifier = getModifier(placement);
                if (modifier != null)
                {
                    modifier.close(true);
                    notifyClose(modifier);
                }
                context.getSyncmaticManager().removePlacement(placement);
                for (final ExchangeTarget client : broadcastTargets)
                {
                    final FriendlyByteBuf newPacketBuf = new FriendlyByteBuf(Unpooled.buffer());
                    newPacketBuf.writeUUID(placement.getId());
                    client.sendPacket(PacketType.REMOVE_SYNCMATIC, newPacketBuf, context);
                }
            }
        }
        if (type.equals(PacketType.MODIFY_REQUEST))
        {
            final UUID placementId = packetBuf.readUUID();
            final ModifyExchangeServer modifier = new ModifyExchangeServer(placementId, source, context);
            startExchange(modifier);
        }
    }

    @Override
    protected void handleExchange(final Exchange exchange)
    {
        if (exchange instanceof DownloadExchange)
        {
            final ServerPlacement p = ((DownloadExchange) exchange).getPlacement();

            if (exchange.isSuccessful())
            {
                addPlacement(exchange.getPartner(), p);
                if (downloadingFile.containsKey(p.getHash()))
                {
                    for (final ServerPlacement placement : downloadingFile.get(p.getHash()))
                    {
                        addPlacement(exchange.getPartner(), placement);
                    }
                }
            }
            else
            {
                cancelShare(exchange.getPartner(), p);
                if (downloadingFile.containsKey(p.getHash()))
                {
                    for (final ServerPlacement placement : downloadingFile.get(p.getHash()))
                    {
                        cancelShare(exchange.getPartner(), placement);
                    }
                }
            }

            downloadingFile.remove(p.getHash());
            return;
        }
        if (exchange instanceof VersionHandshakeServer && exchange.isSuccessful())
        {
            broadcastTargets.add(exchange.getPartner());
        }
        if (exchange instanceof ModifyExchangeServer && exchange.isSuccessful())
        {
            final ServerPlacement placement = ((ModifyExchangeServer) exchange).getPlacement();
            for (final ExchangeTarget client : broadcastTargets)
            {
                if (client.getFeatureSet().hasFeature(Feature.MODIFY))
                {
                    // client supports modify so just send modify
                    final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeUUID(placement.getId());
                    putPositionData(placement, buf, client);
                    if (client.getFeatureSet().hasFeature(Feature.CORE_EX))
                    {
                        buf.writeUUID(placement.getLastModifiedBy().uuid);
                        buf.writeUtf(placement.getLastModifiedBy().getName());
                    }
                    client.sendPacket(PacketType.MODIFY, buf, context);
                }
                else
                {
                    // client doesn't support modification so
                    // send data and then
                    final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeUUID(placement.getId());
                    client.sendPacket(PacketType.REMOVE_SYNCMATIC, buf, context);
                    final FriendlyByteBuf buf2 = new FriendlyByteBuf(Unpooled.buffer());
                    putMetaData(placement, buf2, client);
                    client.sendPacket(PacketType.REGISTER_METADATA, buf2, context);
                }
            }
        }
    }

    public void addPlacement(final ExchangeTarget t, final ServerPlacement placement)
    {
        if (context.getSyncmaticManager().getPlacement(placement.getId()) != null)
        {
            cancelShare(t, placement);
            return;
        }
        context.getSyncmaticManager().addPlacement(placement);
        for (final ExchangeTarget target : broadcastTargets)
        {
            sendMetaData(placement, target);
        }
    }

    private void cancelShare(final ExchangeTarget source, final ServerPlacement placement)
    {
        final FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        packetByteBuf.writeUUID(placement.getId());
        source.sendPacket(PacketType.CANCEL_SHARE, packetByteBuf, context);
    }
}
