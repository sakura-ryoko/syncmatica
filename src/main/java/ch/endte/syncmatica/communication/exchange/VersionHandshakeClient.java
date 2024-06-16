package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.FeatureSet;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.network.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class VersionHandshakeClient extends FeatureExchange
{
    private String partnerVersion;
    public VersionHandshakeClient(final ExchangeTarget partner, final Context con) { super(partner, con); }

    @Override
    public boolean checkPacket(final PacketType type, final PacketByteBuf packetBuf)
    {
        return type.equals(PacketType.CONFIRM_USER)
                || type.equals(PacketType.REGISTER_VERSION)
                || super.checkPacket(type, packetBuf);
    }

    @Override
    public void handle(final PacketType type, final PacketByteBuf packetBuf)
    {
        if (type.equals(PacketType.REGISTER_VERSION))
        {
            final String version = packetBuf.readString(PACKET_MAX_STRING_SIZE);
            if (!getContext().checkPartnerVersion(version))
            {
                // any further packets are risky so no further packets should get send
                Syncmatica.LOGGER.warn("Denying syncmatica join due to outdated server with local version {} and server version {}", Reference.MOD_VERSION, version);
                close(false);
            }
            else
            {
                Syncmatica.LOGGER.info("Accepting version {} from partner {}", version, getPartner().getPersistentName());
                partnerVersion = version;
                final FeatureSet fs = FeatureSet.fromVersionString(version);
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
        }
        else if (type.equals(PacketType.CONFIRM_USER))
        {
            final int placementCount = packetBuf.readInt();
            for (int i = 0; i < placementCount; i++)
            {
                final ServerPlacement p = getManager().receiveMetaData(packetBuf, getPartner());
                getContext().getSyncmaticManager().addPlacement(p);
            }
            Syncmatica.LOGGER.info("Joining syncmatica server with local version {}", Reference.MOD_VERSION);
            LitematicManager.getInstance().commitLoad();
            getContext().startup();
            succeed();
        }
        else
        {
            super.handle(type, packetBuf);
        }
    }

    @Override
    public void onFeatureSetReceive()
    {
        final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        newBuf.writeString(Reference.MOD_VERSION);
        getPartner().sendPacket(PacketType.REGISTER_VERSION, newBuf, getContext());
    }

    @Override
    public void init()
    {
        // Not required - just await message from the server
    }
}
