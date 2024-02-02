package ch.endte.syncmatica.communication.exchange;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.data.ServerPlacement;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.features.FeatureSet;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.util.SyncLog;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
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
    public boolean checkPacket(PacketType type, NbtCompound nbt)
    {
        return type.equals(PacketType.NBT_DATA);
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
                SyncLog.info("Denying syncmatica join due to outdated server with local version {} and server version {}", SyncmaticaReference.MOD_VERSION, version);
                close(false);
            }
            else
            {
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
            SyncLog.info("Joining syncmatica server with local version {} and server version {}", SyncmaticaReference.MOD_VERSION, partnerVersion);
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
    public void handle(PacketType type, NbtCompound nbt)
    {
        SyncLog.debug("VersionHandshakeClient#handle(): received nbtData packet.");
    }

    @Override
    public void onFeatureSetReceive()
    {
        // #FIXME
        final PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        newBuf.writeString(SyncmaticaReference.MOD_VERSION);
        getPartner().sendPacket(PacketType.REGISTER_VERSION, newBuf, getContext());
    }

    @Override
    public void init()
    {
        // Not required - just await message from the server
    }
}
