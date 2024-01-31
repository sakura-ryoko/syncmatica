package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.features.FeatureSet;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;

public class VersionHandshakeClient extends FeatureExchange {

    private String partnerVersion;

    public VersionHandshakeClient(final ExchangeTarget partner, final Context con) {
        super(partner, con);
    }

    @Override
    public boolean checkPacket(final SyncmaticaPacketType type, final PacketByteBuf packetBuf) {
        return type.equals(SyncmaticaPacketType.CONFIRM_USER)
                || type.equals(SyncmaticaPacketType.REGISTER_VERSION)
                || super.checkPacket(type, packetBuf);
    }

    @Override
    public void handle(final SyncmaticaPacketType type, final PacketByteBuf packetBuf) {
        if (type.equals(SyncmaticaPacketType.REGISTER_VERSION)) {
            final String version = packetBuf.readString(32767);
            if (!getContext().checkPartnerVersion(version)) {
                // any further packets are risky so no further packets should get send
                LogManager.getLogger(VersionHandshakeClient.class).info("Denying syncmatica join due to outdated server with local version {} and server version {}", SyncmaticaReference.MOD_VERSION, version);
                close(false);
            } else {
                partnerVersion = version;
                final FeatureSet fs = FeatureSet.fromVersionString(version);
                if (fs == null) {
                    requestFeatureSet();
                } else {
                    getPartner().setFeatureSet(fs);
                    onFeatureSetReceive();
                }
            }
        } else if (type.equals(SyncmaticaPacketType.CONFIRM_USER)) {
            final int placementCount = packetBuf.readInt();
            for (int i = 0; i < placementCount; i++) {
                final ServerPlacement p = getManager().receiveMetaData(packetBuf, getPartner());
                getContext().getSyncmaticManager().addPlacement(p);
            }
            LogManager.getLogger(VersionHandshakeClient.class).info("Joining syncmatica server with local version {} and server version {}", SyncmaticaReference.MOD_VERSION, partnerVersion);
            LitematicManager.getInstance().commitLoad();
            getContext().startup();
            succeed();
        } else {
            super.handle(type, packetBuf);
        }
    }

    @Override
    public void onFeatureSetReceive() {
        // #FIXME
        //final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        //newBuf.writeString(Syncmatica.VERSION);
        //getPartner().sendPacket(PacketType.REGISTER_VERSION.identifier, newBuf, getContext());
    }

    @Override
    public void init() {
        // Not required - just await message from the server
    }

}
