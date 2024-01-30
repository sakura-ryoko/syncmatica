package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.MessageType;
import ch.endte.syncmatica.communication.PacketType;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DownloadExchange extends AbstractExchange {

    private final ServerPlacement toDownload;
    private final OutputStream outputStream;
    private final MessageDigest md5;
    private final File downloadFile;
    private int bytesSent;

    public DownloadExchange(final ServerPlacement syncmatic, final File downloadFile, final ExchangeTarget partner, final Context context) throws IOException, NoSuchAlgorithmException {
        super(partner, context);
        this.downloadFile = downloadFile;
        final OutputStream os = new FileOutputStream(downloadFile); //NOSONAR
        toDownload = syncmatic;
        md5 = MessageDigest.getInstance("MD5");
        outputStream = new DigestOutputStream(os, md5);
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.SEND_LITEMATIC.identifier)
                || id.equals(PacketType.FINISHED_LITEMATIC.identifier)
                || id.equals(PacketType.CANCEL_LITEMATIC.identifier)) {
            return checkUUID(packetBuf, toDownload.getId());
        }
        return false;
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        packetBuf.readUuid(); //skips the UUID
        if (id.equals(PacketType.SEND_LITEMATIC.identifier)) {
            final int size = packetBuf.readInt();
            bytesSent += size;
            if (getContext().isServer() && getContext().getQuotaService().isOverQuota(getPartner(), bytesSent)) {
                close(true);
                ((ServerCommunicationManager) getContext().getCommunicationManager()).sendMessage(
                        getPartner(),
                        MessageType.ERROR,
                        "syncmatica.error.cancelled_transmit_exceed_quota"
                );
            }
            try {
                packetBuf.readBytes(outputStream, size);
            } catch (final IOException e) {
                close(true);
                e.printStackTrace();
                return;
            }
            // #FIXME
            //final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            //packetByteBuf.writeUuid(toDownload.getId());
            //getPartner().sendPacket(PacketType.RECEIVED_LITEMATIC.identifier, packetByteBuf, getContext());
            return;
        }
        if (id.equals(PacketType.FINISHED_LITEMATIC.identifier)) {
            try {
                outputStream.flush();
            } catch (final IOException e) {
                close(false);
                e.printStackTrace();
                return;
            }
            final UUID downloadHash = UUID.nameUUIDFromBytes(md5.digest());
            if (downloadHash.equals(toDownload.getHash())) {
                succeed();
            } else {
                // no need to notify partner since exchange is closed on partner side
                close(false);
            }
            return;
        }
        if (id.equals(PacketType.CANCEL_LITEMATIC.identifier)) {
            close(false);
        }
    }

    @Override
    public void init() {
        // #FIXME
        //final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        //packetByteBuf.writeUuid(toDownload.getId());
        //getPartner().sendPacket(PacketType.REQUEST_LITEMATIC.identifier, packetByteBuf, getContext());
    }

    @Override
    protected void onClose() {
        getManager().setDownloadState(toDownload, false);
        if (getContext().isServer() && isSuccessful()) {
            getContext().getQuotaService().progressQuota(getPartner(), bytesSent);
        }
        try {
            outputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        if (!isSuccessful() && downloadFile.exists()) {
            downloadFile.delete(); // NOSONAR
        }
    }

    @Override
    protected void sendCancelPacket() {
        // #FIXME
        //final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        //packetByteBuf.writeUuid(toDownload.getId());
        //getPartner().sendPacket(PacketType.CANCEL_LITEMATIC.identifier, packetByteBuf, getContext());
    }

    public ServerPlacement getPlacement() {
        return toDownload;
    }

}
