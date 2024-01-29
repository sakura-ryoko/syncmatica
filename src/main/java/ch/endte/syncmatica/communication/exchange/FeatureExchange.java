package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.FeatureSet;
import ch.endte.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class FeatureExchange extends AbstractExchange {

    protected FeatureExchange(final ExchangeTarget partner, final Context con) {
        super(partner, con);
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        return id.equals(PacketType.FEATURE_REQUEST.identifier)
                || id.equals(PacketType.FEATURE.identifier);
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.FEATURE_REQUEST.identifier)) {
            sendFeatures();
        } else if (id.equals(PacketType.FEATURE.identifier)) {
            final FeatureSet fs = FeatureSet.fromString(packetBuf.readString(32767));
            getPartner().setFeatureSet(fs);
            onFeatureSetReceive();
        }
    }

    protected void onFeatureSetReceive() {
        succeed();
    }

    public void requestFeatureSet() {
        // #FIXME
        //getPartner().sendPacket(PacketType.FEATURE_REQUEST.identifier, new PacketByteBuf(Unpooled.buffer()), getContext());
    }

    private void sendFeatures() {
        // #FIXME
        //final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        //final FeatureSet fs = getContext().getFeatureSet();
        //buf.writeString(fs.toString(), 32767);
        //getPartner().sendPacket(PacketType.FEATURE.identifier, buf, getContext());
    }
}
