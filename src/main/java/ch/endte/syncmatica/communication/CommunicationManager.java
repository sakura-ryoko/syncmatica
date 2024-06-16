package ch.endte.syncmatica.communication;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
import ch.endte.syncmatica.util.SyncmaticaUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public abstract class CommunicationManager
{
    protected int PACKET_MAX_STRING_SIZE = PacketByteBuf.DEFAULT_MAX_STRING_LENGTH;
    protected final Collection<ExchangeTarget> broadcastTargets;

    // TODO: Refactor this bs
    protected final Map<UUID, Boolean> downloadState;
    protected final Map<UUID, Exchange> modifyState;

    protected Context context;

    protected static final BlockRotation[] rotOrdinals = BlockRotation.values();
    protected static final BlockMirror[] mirOrdinals = BlockMirror.values();

    protected CommunicationManager()
    {
        broadcastTargets = new ArrayList<>();
        downloadState = new HashMap<>();
        modifyState = new HashMap<>();
    }

    public boolean handlePacket(final PacketType type) { return PacketType.containsType(type); }

    public void onPacket(final ExchangeTarget source, final PacketType type, final PacketByteBuf packetBuf)
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
    protected abstract void handle(ExchangeTarget source, PacketType type, PacketByteBuf packetBuf);

    // will get called for every finished exchange (successful or not)
    protected abstract void handleExchange(Exchange exchange);

    public void sendMetaData(final ServerPlacement metaData, final ExchangeTarget target)
    {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        putMetaData(metaData, buf, target);
        target.sendPacket(PacketType.REGISTER_METADATA, buf, context);
    }

    public void putMetaData(final ServerPlacement metaData, final PacketByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        buf.writeUuid(metaData.getId());

        buf.writeString(SyncmaticaUtil.sanitizeFileName(metaData.getName()));
        buf.writeUuid(metaData.getHash());

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.CORE_EX))
        {
            buf.writeUuid(metaData.getOwner().uuid);
            buf.writeString(metaData.getOwner().getName());
            buf.writeUuid(metaData.getLastModifiedBy().uuid);
            buf.writeString(metaData.getLastModifiedBy().getName());
        }

        putPositionData(metaData, buf, exchangeTarget);
    }

    public void putPositionData(final ServerPlacement metaData, final PacketByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        buf.writeBlockPos(metaData.getPosition());
        buf.writeString(metaData.getDimension());
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
                buf.writeString(subPlacement.name);
                buf.writeBlockPos(subPlacement.position);
                buf.writeInt(subPlacement.rotation.ordinal());
                buf.writeInt(subPlacement.mirror.ordinal());
            }
        }
    }

    public ServerPlacement receiveMetaData(final PacketByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        final UUID id = buf.readUuid();

        final String fileName = SyncmaticaUtil.sanitizeFileName(buf.readString(PACKET_MAX_STRING_SIZE));
        final UUID hash = buf.readUuid();

        PlayerIdentifier owner = PlayerIdentifier.MISSING_PLAYER;
        PlayerIdentifier lastModifiedBy = PlayerIdentifier.MISSING_PLAYER;

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.CORE_EX))
        {
            final PlayerIdentifierProvider provider = context.getPlayerIdentifierProvider();
            owner = provider.createOrGet(
                    buf.readUuid(),
                    buf.readString(PACKET_MAX_STRING_SIZE)
            );
            lastModifiedBy = provider.createOrGet(
                    buf.readUuid(),
                    buf.readString(PACKET_MAX_STRING_SIZE)
            );
        }

        final ServerPlacement placement = new ServerPlacement(id, fileName, hash, owner);
        placement.setLastModifiedBy(lastModifiedBy);

        receivePositionData(placement, buf, exchangeTarget);

        return placement;
    }

    public void receivePositionData(final ServerPlacement placement, final PacketByteBuf buf, final ExchangeTarget exchangeTarget)
    {
        final BlockPos pos = buf.readBlockPos();
        final String dimensionId = buf.readString(PACKET_MAX_STRING_SIZE);
        final BlockRotation rot = rotOrdinals[buf.readInt()];
        final BlockMirror mir = mirOrdinals[buf.readInt()];
        placement.move(dimensionId, pos, rot, mir);

        if (exchangeTarget.getFeatureSet().hasFeature(Feature.CORE_EX))
        {
            final SubRegionData subRegionData = placement.getSubRegionData();
            subRegionData.reset();
            final int limit = buf.readInt();
            for (int i = 0; i < limit; i++)
            {
                subRegionData.modify(
                        buf.readString(PACKET_MAX_STRING_SIZE),
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
        final File toDownload = context.getFileStorage().createLocalLitematic(syncmatic);
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
