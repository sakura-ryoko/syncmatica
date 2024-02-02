package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.features.FeatureSet;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.util.SyncLog;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public abstract class FeatureExchange extends AbstractExchange
{
    protected FeatureExchange(final ExchangeTarget partner, final Context con) { super(partner, con); }

    @Override
    public boolean checkPacket(final PacketType type, final PacketByteBuf packetBuf)
    {
        SyncLog.debug("FeatureExchange#checkPacket(): received byteBuf packet.");
        return type.equals(PacketType.FEATURE_REQUEST)
                || type.equals(PacketType.FEATURE);
    }
    @Override
    public boolean checkPacket(final PacketType type, final NbtCompound nbt)
    {
        SyncLog.debug("FeatureExchange#checkPacket(): received nbtData packet.");
        return type.equals(PacketType.NBT_DATA);
    }

    @Override
    public void handle(final PacketType type, final PacketByteBuf packetBuf)
    {
        SyncLog.debug("FeatureExchange#handle(): received byteBuf packet.");
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

    @Override
    public void handle(final PacketType type, final NbtCompound nbt)
    {
        SyncLog.debug("FeatureExchange#handle(): received nbtData packet.");
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
