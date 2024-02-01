package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.features.FeatureSet;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public abstract class FeatureExchange extends AbstractExchange
{
    protected FeatureExchange(final ExchangeTarget partner, final Context con) { super(partner, con); }

    @Override
    public boolean checkPacket(final SyncmaticaPacketType type, final PacketByteBuf packetBuf)
    {
        return type.equals(SyncmaticaPacketType.FEATURE_REQUEST)
                || type.equals(SyncmaticaPacketType.FEATURE);
    }

    @Override
    public void handle(final SyncmaticaPacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(SyncmaticaPacketType.FEATURE_REQUEST))
        {
            sendFeatures();
        } else if (type.equals(SyncmaticaPacketType.FEATURE))
        {
            final FeatureSet fs = FeatureSet.fromString(packetBuf.readString(PACKET_MAX_STRING_SIZE));
            getPartner().setFeatureSet(fs);
            onFeatureSetReceive();
        }
    }

    protected void onFeatureSetReceive() { succeed(); }

    public void requestFeatureSet()
    {
        // #FIXME
        getPartner().sendPacket(SyncmaticaPacketType.FEATURE_REQUEST, new PacketByteBuf(Unpooled.buffer()), getContext());
    }

    private void sendFeatures()
    {
        // #FIXME
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        final FeatureSet fs = getContext().getFeatureSet();
        buf.writeString(fs.toString(), PACKET_MAX_STRING_SIZE);
        getPartner().sendPacket(SyncmaticaPacketType.FEATURE, buf, getContext());
    }
}
