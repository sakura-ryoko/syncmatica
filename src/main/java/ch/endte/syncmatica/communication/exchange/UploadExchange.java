package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import java.io.*;

// uploading part of transmit data exchange
// pairs with Download Exchange

public class UploadExchange extends AbstractExchange
{
    // The maximum buffer size for CustomPayloadPackets is actually 32767
    // so 32768 is a bad value to send - thus adjusted it to 16384 - exactly halved
    private static final int BUFFER_SIZE = 16384;
    private final ServerPlacement toUpload;
    private final InputStream inputStream;
    private final byte[] buffer = new byte[BUFFER_SIZE];

    public UploadExchange(final ServerPlacement syncmatic, final File uploadFile, final ExchangeTarget partner, final Context con) throws FileNotFoundException
    {
        super(partner, con);
        toUpload = syncmatic;
        inputStream = new FileInputStream(uploadFile);
    }

    @Override
    public boolean checkPacket(final SyncmaticaPacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(SyncmaticaPacketType.RECEIVED_LITEMATIC)
                || type.equals(SyncmaticaPacketType.CANCEL_LITEMATIC))
        {
            return checkUUID(packetBuf, toUpload.getId());
        }
        return false;
    }

    @Override
    public void handle(final SyncmaticaPacketType type, final PacketByteBuf packetBuf)
    {

        packetBuf.readUuid(); // uncertain if the data has to be consumed
        if (type.equals(SyncmaticaPacketType.RECEIVED_LITEMATIC))
        {
            send();
        }
        if (type.equals(SyncmaticaPacketType.CANCEL_LITEMATIC))
        {
            close(false);
        }
    }

    private void send()
    {
        // might fail when an empty file is attempted to be transmitted
        final int bytesRead;
        try
        {
            bytesRead = inputStream.read(buffer);
        }
        catch (final IOException e)
        {
            close(true);
            e.printStackTrace();
            return;
        }
        if (bytesRead == -1)
        {
            sendFinish();
        }
        else
        {
            sendData(bytesRead);
        }
    }

    private void sendData(final int bytesRead)
    {
        // #FIXME
        final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeUuid(toUpload.getId());
        packetByteBuf.writeInt(bytesRead);
        packetByteBuf.writeBytes(buffer, 0, bytesRead);
        getPartner().sendPacket(SyncmaticaPacketType.SEND_LITEMATIC, packetByteBuf, getContext());
    }

    private void sendFinish()
    {
        // #FIXME
        final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeUuid(toUpload.getId());
        getPartner().sendPacket(SyncmaticaPacketType.FINISHED_LITEMATIC, packetByteBuf, getContext());
        succeed();
    }

    @Override
    public void init() { send(); }

    @Override
    protected void onClose()
    {
        try
        {
            inputStream.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendCancelPacket()
    {
        // #FIXME
        final PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeUuid(toUpload.getId());
        getPartner().sendPacket(SyncmaticaPacketType.CANCEL_LITEMATIC, packetByteBuf, getContext());
    }
}
