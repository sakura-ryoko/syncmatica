package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.ServerPlacement;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.FeatureSet;
import ch.endte.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.util.Collection;

public class VersionHandshakeServer extends FeatureExchange {

    private String partnerVersion;

    public VersionHandshakeServer(final ExchangeTarget partner, final Context con) {
        super(partner, con);
    }

    @Override
    public boolean checkPacket(final Identifier id, final PacketByteBuf packetBuf) {
        return id.equals(PacketType.REGISTER_VERSION.identifier)
                || super.checkPacket(id, packetBuf);
    }

    @Override
    public void handle(final Identifier id, final PacketByteBuf packetBuf) {
        if (id.equals(PacketType.REGISTER_VERSION.identifier)) {
            partnerVersion = packetBuf.readString(32767);
            if (!getContext().checkPartnerVersion(partnerVersion)) {
                LogManager.getLogger(VersionHandshakeServer.class).info("Denying syncmatica join due to outdated client with local version {} and client version {}", Syncmatica.VERSION, partnerVersion);
                // same as client - avoid further packets
                close(false);
                return;
            }
            final FeatureSet fs = FeatureSet.fromVersionString(partnerVersion);
            if (fs == null) {
                requestFeatureSet();
            } else {
                getPartner().setFeatureSet(fs);
                onFeatureSetReceive();
            }
        } else {
            super.handle(id, packetBuf);
        }

    }

    @Override
    public void onFeatureSetReceive() {
        LogManager.getLogger(VersionHandshakeServer.class).info("Syncmatica client joining with local version {} and client version {}", Syncmatica.VERSION, partnerVersion);
        // #FIXME
        //final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        //final Collection<ServerPlacement> l = getContext().getSyncmaticManager().getAll();
        //newBuf.writeInt(l.size());
        //for (final ServerPlacement p : l) {
            //getManager().putMetaData(p, newBuf, getPartner());
        //}
        //getPartner().sendPacket(PacketType.CONFIRM_USER.identifier, newBuf, getContext());
        //succeed();
    }

    @Override
    public void init() {
        // #FIXME
        //final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        //newBuf.writeString(Syncmatica.VERSION);
        //getPartner().sendPacket(PacketType.REGISTER_VERSION.identifier, newBuf, getContext());
    }
}
