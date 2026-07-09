package ch.endte.syncmatica.communication;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Feature;
import ch.endte.syncmatica.communication.exchange.DownloadExchange;
import ch.endte.syncmatica.communication.exchange.Exchange;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.extended_core.PlayerIdentifier;
import ch.endte.syncmatica.extended_core.PlayerIdentifierProvider;
import ch.endte.syncmatica.extended_core.SubRegionData;
import ch.endte.syncmatica.extended_core.SubRegionPlacementModification;
import ch.endte.syncmatica.network.PacketType;
import io.netty.buffer.Unpooled;

public abstract class CommunicationManager
{
    protected int PACKET_MAX_STRING_SIZE = FriendlyByteBuf.MAX_STRING_LENGTH;
    protected final Collection<ExchangeTarget> broadcastTargets;

    // TODO: Refactor this bs
    protected final Map<UUID, Boolean> downloadState;
    protected final Map<UUID, Exchange> modifyState;

    protected Context context;

    protected static final Rotation[] rotOrdinals = Rotation.values();
    protected static final Mirror[] mirOrdinals = Mirror.values();

    protected CommunicationManager()
    {
        broadcastTargets = new ArrayList<>();
        downloadState = new HashMap<>();
        modifyState = new HashMap<>();
    }

    public boolean handlePacket(final PacketType type) { return PacketType.containsType(type); }

    public void onPacket(final ExchangeTarget source, final PacketType type, final FriendlyByteBuf packetBuf)
    {
        context.getDebugService().logReceivePacket(type);
        Exchange handler = null;
        final Collection<Exchange> potentialMessageTarget = source.getExchanges();
        if (potentialMessageTarget != null)
        {
            for (final Exchange target : potentialMessageTarget)
            {
                if (target.checkPacket(type, packetBuf))
                {
                    target.handle(type, packetBuf);
                    handler = target;
                    break;
                }
            }
        }
        if (handler == null)
        {
            handle(source, type, packetBuf);
        }
        else if (handler.isFinished())
        {
            notifyClose(handler);
        }
    }

    // will get called for every packet not handled by an exchange
    protected abstract void handle(ExchangeTarget source, PacketType type, FriendlyByteBuf packetBuf);

    // will get called for every finished exchange (successful or not)
    protected abstract void handleExchange(Exchange exchange);

    public void sendMetaData(final ServerPlacement metaData, final ExchangeTarget target)
    {
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        putMetaData(metaData, buf, target);
        target.sendPacket(PacketType.REGISTER_METADATA, buf, context);
    }

    public void putMetaData(final ServerPlacement metaData, final FriendlyByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        buf.writeUUID(metaData.getId());
//        buf.writeString(SyncmaticaUtil.sanitizeFileName(metaData.getFileName()));
        buf.writeUtf(metaData.getCleanFileName());
        buf.writeUUID(metaData.getHash());

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.DISPLAY_NAME))
        {
            buf.writeUtf(metaData.getName());
        }

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.CORE_EX))
        {
            buf.writeUUID(metaData.getOwner().uuid);
            buf.writeUtf(metaData.getOwner().getName());
            buf.writeUUID(metaData.getLastModifiedBy().uuid);
            buf.writeUtf(metaData.getLastModifiedBy().getName());
        }

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.VERSION)) {
            buf.writeVarInt(metaData.getLitematicVersion());
            buf.writeVarInt(metaData.getDataVersion());
        }

        putPositionData(metaData, buf, exchangeTarget);
    }

    public void putPositionData(final ServerPlacement metaData, final FriendlyByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        buf.writeBlockPos(metaData.getPosition());
        buf.writeUtf(metaData.getDimension());
        // one of the rare use cases for ordinal
        // transmitting the information of a non-modifying enum to another
        // instance of this application with no regard to the persistence
        // of the ordinal values over time
        buf.writeInt(metaData.getRotation().ordinal());
        buf.writeInt(metaData.getMirror().ordinal());

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.CORE_EX))
        {
            if (metaData.getSubRegionData().getModificationData() == null)
            {
                buf.writeInt(0);

                return;
            }

            final Collection<SubRegionPlacementModification> regionData = metaData.getSubRegionData().getModificationData().values();
            buf.writeInt(regionData.size());

            for (final SubRegionPlacementModification subPlacement : regionData)
            {
                buf.writeUtf(subPlacement.name);
                buf.writeBlockPos(subPlacement.position);
                buf.writeInt(subPlacement.rotation.ordinal());
                buf.writeInt(subPlacement.mirror.ordinal());
            }
        }
    }

    public ServerPlacement receiveMetaData(final FriendlyByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        final UUID id = buf.readUUID();

//        final String fileName = SyncmaticaUtil.sanitizeFileName(buf.readString(PACKET_MAX_STRING_SIZE));
        final String fileName = buf.readUtf(PACKET_MAX_STRING_SIZE);
        final UUID hash = buf.readUUID();

        PlayerIdentifier owner = PlayerIdentifier.MISSING_PLAYER;
        PlayerIdentifier lastModifiedBy = PlayerIdentifier.MISSING_PLAYER;

        String displayName;
        if (exchangeTarget.getFeatureSet().hasFeature(Feature.DISPLAY_NAME))
        {
            displayName = buf.readUtf(PACKET_MAX_STRING_SIZE);
        }
        else
        {
            displayName = fileName;
        }

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.CORE_EX))
        {
            final PlayerIdentifierProvider provider = context.getPlayerIdentifierProvider();
            owner = provider.createOrGet(
                    buf.readUUID(),
                    buf.readUtf(PACKET_MAX_STRING_SIZE)
            );
            lastModifiedBy = provider.createOrGet(
                    buf.readUUID(),
                    buf.readUtf(PACKET_MAX_STRING_SIZE)
            );
        }

        ServerPlacement placement;
        int litematicVersion;
        int dataVersion;

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.VERSION)) {
            litematicVersion = buf.readVarInt();
            dataVersion = buf.readVarInt();
            placement = new ServerPlacement(id, ServerPlacement.normalizeFileName(fileName), displayName, hash, owner, litematicVersion, dataVersion);
        } else {
            placement = new ServerPlacement(id, ServerPlacement.normalizeFileName(fileName), displayName, hash, owner);
        }

        placement.setLastModifiedBy(lastModifiedBy);

        receivePositionData(placement, buf, exchangeTarget);

        return placement;
    }

    public void receivePositionData(final ServerPlacement placement, final FriendlyByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        final BlockPos pos = buf.readBlockPos();
        final String dimensionId = buf.readUtf(PACKET_MAX_STRING_SIZE);
        final Rotation rot = rotOrdinals[buf.readInt()];
        final Mirror mir = mirOrdinals[buf.readInt()];
        placement.move(dimensionId, pos, rot, mir);

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.CORE_EX))
        {
            final SubRegionData subRegionData = placement.getSubRegionData();
            subRegionData.reset();
            final int limit = buf.readInt();
            for (int i = 0; i < limit; i++)
            {
                subRegionData.modify(
                        buf.readUtf(PACKET_MAX_STRING_SIZE),
                        buf.readBlockPos(),
                        rotOrdinals[buf.readInt()],
                        mirOrdinals[buf.readInt()]
                );
            }
        }
    }

    public void download(final ServerPlacement syncmatic, final ExchangeTarget source) throws NoSuchAlgorithmException, IOException
    {
        if (!context.getFileStorage().getLocalState(syncmatic).isReadyForDownload())
        {
            // forgot a negation here
            throw new IllegalArgumentException(syncmatic.toString() + " is not ready for download local state is: " + context.getFileStorage().getLocalState(syncmatic).toString());
        }
        final Path toDownload = context.getFileStorage().createLocalLitematic(syncmatic);
        final Exchange downloadExchange = new DownloadExchange(syncmatic, toDownload, source, context);
        setDownloadState(syncmatic, true);
        startExchange(downloadExchange);
    }

    public void setDownloadState(final ServerPlacement syncmatic, final boolean b) { downloadState.put(syncmatic.getHash(), b); }

    public boolean getDownloadState(final ServerPlacement syncmatic) { return downloadState.getOrDefault(syncmatic.getHash(), false); }

    public void setModifier(final ServerPlacement syncmatic, final Exchange exchange) { modifyState.put(syncmatic.getHash(), exchange); }

    public Exchange getModifier(final ServerPlacement syncmatic) { return modifyState.get(syncmatic.getHash()); }

    public void startExchange(final Exchange newExchange)
    {
        if (!broadcastTargets.contains(newExchange.getPartner()))
        {
            throw new IllegalArgumentException(newExchange.getPartner().toString() + " is not a valid ExchangeTarget");
        }
        startExchangeUnchecked(newExchange);
    }

    protected void startExchangeUnchecked(final Exchange newExchange)
    {
        newExchange.getPartner().getExchanges().add(newExchange);
        newExchange.init();
        if (newExchange.isFinished())
        {
            notifyClose(newExchange);
        }
    }

    public void setContext(final Context con)
    {
        if (context == null)
        {
            context = con;
        }
        else
        {
            throw new Context.DuplicateContextAssignmentException("Duplicate Context Assignment");
        }
    }

    public void notifyClose(final Exchange e)
    {
        e.getPartner().getExchanges().remove(e);
        handleExchange(e);
    }
}
