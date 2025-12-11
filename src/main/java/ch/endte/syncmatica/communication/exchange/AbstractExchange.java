package ch.endte.syncmatica.communication.exchange;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;

public abstract class AbstractExchange implements Exchange
{
    protected int PACKET_MAX_STRING_SIZE = FriendlyByteBuf.MAX_STRING_LENGTH;
    private boolean success = false;
    private boolean finished = false;
    private final ExchangeTarget partner;
    private final Context context;
    protected AbstractExchange(final ExchangeTarget partner, final Context con)
    {
        this.partner = partner;
        context = con;
    }
    @Override
    public ExchangeTarget getPartner() {
        return partner;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isSuccessful() {
        return success;
    }

    @Override
    public void close(final boolean notifyPartner)
    {
        finished = true;
        success = false;
        onClose();
        if (notifyPartner)
        {
            sendCancelPacket();
        }
    }

    public CommunicationManager getManager() { return context.getCommunicationManager(); }

    protected void sendCancelPacket() { }

    protected void onClose() { }

    protected void succeed()
    {
        finished = true;
        success = true;
        // Ctrl+C Ctrl+V and forget to adapt the success state - typical
        onClose();
    }

    protected static boolean checkUUID(final FriendlyByteBuf sourceBuf, final UUID targetId)
    {
        final int r = sourceBuf.readerIndex();
        final UUID sourceId = sourceBuf.readUUID();
        sourceBuf.readerIndex(r);
        return sourceId.equals(targetId);
    }
}
