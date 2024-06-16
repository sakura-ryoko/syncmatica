package ch.endte.syncmatica.communication.exchange;

import java.io.File;
import java.io.FileNotFoundException;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.data.RedirectFileStorage;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.network.PacketType;
import net.minecraft.network.PacketByteBuf;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;

public class ShareLitematicExchange extends AbstractExchange
{
    private final SchematicPlacement schematicPlacement;
    private final ServerPlacement toShare;
    private final File toUpload;

    public ShareLitematicExchange(final SchematicPlacement schematicPlacement, final ExchangeTarget partner, final Context con) { this(schematicPlacement, partner, con, null); }

    public ShareLitematicExchange(final SchematicPlacement schematicPlacement, final ExchangeTarget partner, final Context con, final ServerPlacement p)
    {
        super(partner, con);
        this.schematicPlacement = schematicPlacement;
        toShare = p == null ? LitematicManager.getInstance().syncmaticFromSchematic(schematicPlacement) : p;
        toUpload = schematicPlacement.getSchematicFile();
    }

    @Override
    public boolean checkPacket(final PacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(PacketType.REQUEST_LITEMATIC)
                || type.equals(PacketType.REGISTER_METADATA)
                || type.equals(PacketType.CANCEL_SHARE))
        {
            return AbstractExchange.checkUUID(packetBuf, toShare.getId());
        }
        return false;
    }

    @Override
    public void handle(final PacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(PacketType.REQUEST_LITEMATIC))
        {
            packetBuf.readUuid();
            final UploadExchange upload;
            try
            {
                upload = new UploadExchange(toShare, toUpload, getPartner(), getContext());
            }
            catch (final FileNotFoundException e)
            {
                e.printStackTrace();

                return;
            }
            getManager().startExchange(upload);
            return;
        }
        if (type.equals(PacketType.REGISTER_METADATA))
        {
            final RedirectFileStorage redirect = (RedirectFileStorage) getContext().getFileStorage();
            redirect.addRedirect(toUpload);
            LitematicManager.getInstance().renderSyncmatic(toShare, schematicPlacement, false);
            getContext().getSyncmaticManager().addPlacement(toShare);
            return;
        }
        if (type.equals(PacketType.CANCEL_SHARE))
        {
            close(false);
        }
    }

    @Override
    public void init()
    {
        if (toShare == null)
        {
            close(false);
            return;
        }
        ((ClientCommunicationManager) getManager()).setSharingState(toShare, true);
        getContext().getSyncmaticManager().updateServerPlacement(toShare);
        getManager().sendMetaData(toShare, getPartner());
    }

    @Override
    public void onClose() { ((ClientCommunicationManager) getManager()).setSharingState(toShare, false); }
}
