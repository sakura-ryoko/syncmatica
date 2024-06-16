package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.FeatureSet;
import ch.endte.syncmatica.network.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public abstract class FeatureExchange extends AbstractExchange
{
    protected FeatureExchange(final ExchangeTarget partner, final Context con) { super(partner, con); }

    @Override
    public boolean checkPacket(final PacketType type, final PacketByteBuf packetBuf)
    {
        return type.equals(PacketType.FEATURE_REQUEST)
                || type.equals(PacketType.FEATURE);
    }

    @Override
    public void handle(final PacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(PacketType.FEATURE_REQUEST))
        {
            sendFeatures();
        } else if (type.equals(PacketType.FEATURE))
        {
            final FeatureSet fs = FeatureSet.fromString(packetBuf.readString(PACKET_MAX_STRING_SIZE));
            getPartner().setFeatureSet(fs);
            onFeatureSetReceive();
        }
    }

    protected void onFeatureSetReceive() { succeed(); }

    public void requestFeatureSet()
    {
        getPartner().sendPacket(PacketType.FEATURE_REQUEST, new PacketByteBuf(Unpooled.buffer()), getContext());
    }

    private void sendFeatures()
    {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        final FeatureSet fs = getContext().getFeatureSet();
        buf.writeString(fs.toString(), PACKET_MAX_STRING_SIZE);
        getPartner().sendPacket(PacketType.FEATURE, buf, getContext());
    }
}
