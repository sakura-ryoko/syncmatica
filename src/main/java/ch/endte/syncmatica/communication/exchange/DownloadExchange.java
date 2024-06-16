package ch.endte.syncmatica.communication.exchange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.MessageType;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.network.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class DownloadExchange extends AbstractExchange
{
    private final ServerPlacement toDownload;
    private final OutputStream outputStream;
    private final MessageDigest md5;
    private final File downloadFile;
    private int bytesSent;

    public DownloadExchange(final ServerPlacement syncmatic, final File downloadFile, final ExchangeTarget partner, final Context context) throws IOException, NoSuchAlgorithmException
    {
        super(partner, context);
        this.downloadFile = downloadFile;
        final OutputStream os = new FileOutputStream(downloadFile); //NOSONAR
        toDownload = syncmatic;
        md5 = MessageDigest.getInstance("MD5");
        outputStream = new DigestOutputStream(os, md5);
    }

    @Override
    public boolean checkPacket(final PacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(PacketType.SEND_LITEMATIC)
                || type.equals(PacketType.FINISHED_LITEMATIC)
                || type.equals(PacketType.CANCEL_LITEMATIC))
        {
            return checkUUID(packetBuf, toDownload.getId());
        }
        return false;
    }

    @Override
    public void handle(final PacketType type, final PacketByteBuf packetBuf)
    {
        packetBuf.readUuid(); //skips the UUID
        if (type.equals(PacketType.SEND_LITEMATIC))
        {
            final int size = packetBuf.readInt();
            bytesSent += size;
            if (getContext().isServer() && getContext().getQuotaService().isOverQuota(getPartner(), bytesSent))
            {
                close(true);
                ((ServerCommunicationManager) getContext().getCommunicationManager()).sendMessage(
                        getPartner(),
                        MessageType.ERROR,
                        "syncmatica.error.cancelled_transmit_exceed_quota"
                );
            }
            try
            {
                packetBuf.readBytes(outputStream, size);
            }
            catch (final IOException e)
            {
                close(true);
                e.printStackTrace();
                return;
            }
            final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf.writeUuid(toDownload.getId());
            getPartner().sendPacket(PacketType.RECEIVED_LITEMATIC, packetByteBuf, getContext());
            return;
        }
        if (type.equals(PacketType.FINISHED_LITEMATIC))
        {
            try
            {
                outputStream.flush();
            }
            catch (final IOException e)
            {
                close(false);
                e.printStackTrace();
                return;
            }
            final UUID downloadHash = UUID.nameUUIDFromBytes(md5.digest());
            if (downloadHash.equals(toDownload.getHash()))
            {
                succeed();
            }
            else
            {
                // no need to notify partner since exchange is closed on partner side
                close(false);
            }
            return;
        }
        if (type.equals(PacketType.CANCEL_LITEMATIC))
        {
            close(false);
        }
    }

    @Override
    public void init()
    {
        final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeUuid(toDownload.getId());
        getPartner().sendPacket(PacketType.REQUEST_LITEMATIC, packetByteBuf, getContext());
    }

    @Override
    protected void onClose()
    {
        getManager().setDownloadState(toDownload, false);
        if (getContext().isServer() && isSuccessful())
        {
            getContext().getQuotaService().progressQuota(getPartner(), bytesSent);
        }
        try
        {
            outputStream.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        if (!isSuccessful() && downloadFile.exists())
        {
            try
            {
                if (!downloadFile.delete())
                    Syncmatica.LOGGER.error("DownloadExchange#onClose(): failed to delete file: {}", downloadFile.toString());
            }
            catch (Exception ignored) {}
            // NO-OP
        }
    }

    @Override
    protected void sendCancelPacket()
    {
        final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeUuid(toDownload.getId());
        getPartner().sendPacket(PacketType.CANCEL_LITEMATIC, packetByteBuf, getContext());
    }

    public ServerPlacement getPlacement() { return toDownload; }
}
