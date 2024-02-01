package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.features.FeatureSet;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import ch.endte.syncmatica.util.SyncLog;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;

public class VersionHandshakeServer extends FeatureExchange
{
    private String partnerVersion;
    public VersionHandshakeServer(final ExchangeTarget partner, final Context con) { super(partner, con); }

    @Override
    public boolean checkPacket(final SyncmaticaPacketType type, final PacketByteBuf packetBuf)
    {
        return type.equals(SyncmaticaPacketType.REGISTER_VERSION)
                || super.checkPacket(type, packetBuf);
    }

    @Override
    public void handle(final SyncmaticaPacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(SyncmaticaPacketType.REGISTER_VERSION))
        {
            partnerVersion = packetBuf.readString(PACKET_MAX_STRING_SIZE);
            if (!getContext().checkPartnerVersion(partnerVersion))
            {
                SyncLog.info("Denying syncmatica join due to outdated client with local version {} and client version {}", SyncmaticaReference.MOD_VERSION, partnerVersion);
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
        SyncLog.info("Syncmatica client joining with local version {} and client version {}", SyncmaticaReference.MOD_VERSION, partnerVersion);
        // #FIXME
        final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        final Collection<ServerPlacement> l = getContext().getSyncmaticManager().getAll();
        newBuf.writeInt(l.size());
        for (final ServerPlacement p : l)
        {
            getManager().putMetaData(p, newBuf, getPartner());
        }
        getPartner().sendPacket(SyncmaticaPacketType.CONFIRM_USER, newBuf, getContext());
        succeed();
    }

    @Override
    public void init()
    {
        // #FIXME
        final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        newBuf.writeString(SyncmaticaReference.MOD_VERSION);
        getPartner().sendPacket(SyncmaticaPacketType.REGISTER_VERSION, newBuf, getContext());
    }
}
