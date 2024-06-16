package ch.endte.syncmatica.communication.exchange;

import java.util.Collection;
import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.FeatureSet;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.network.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class VersionHandshakeServer extends FeatureExchange
{
    private String partnerVersion;
    public VersionHandshakeServer(final ExchangeTarget partner, final Context con) { super(partner, con); }

    @Override
    public boolean checkPacket(final PacketType type, final PacketByteBuf packetBuf)
    {
        return type.equals(PacketType.REGISTER_VERSION)
                || super.checkPacket(type, packetBuf);
    }

    @Override
    public void handle(final PacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(PacketType.REGISTER_VERSION))
        {
            partnerVersion = packetBuf.readString(PACKET_MAX_STRING_SIZE);
            if (!getContext().checkPartnerVersion(partnerVersion))
            {
                Syncmatica.LOGGER.info("Denying syncmatica join due to outdated client with local version {} and client version {} from partner {}", Reference.MOD_VERSION, partnerVersion, getPartner().getPersistentName());
                // same as client - avoid further packets
                close(false);
                return;
            }
            final FeatureSet fs = FeatureSet.fromVersionString(partnerVersion);
            if (fs == null)
            {
                requestFeatureSet();
            }
            else
            {
                getPartner().setFeatureSet(fs);
                onFeatureSetReceive();
            }
        }
        else
        {
            super.handle(type, packetBuf);
        }
    }

    @Override
    public void onFeatureSetReceive()
    {
        Syncmatica.LOGGER.info("Syncmatica client joining with local version {} and client version {}", Reference.MOD_VERSION, partnerVersion);
        final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        final Collection<ServerPlacement> l = getContext().getSyncmaticManager().getAll();
        newBuf.writeInt(l.size());
        for (final ServerPlacement p : l)
        {
            getManager().putMetaData(p, newBuf, getPartner());
        }
        getPartner().sendPacket(PacketType.CONFIRM_USER, newBuf, getContext());
        succeed();
    }

    @Override
    public void init()
    {
        final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        newBuf.writeString(Reference.MOD_VERSION);
        getPartner().sendPacket(PacketType.REGISTER_VERSION, newBuf, getContext());
    }
}
