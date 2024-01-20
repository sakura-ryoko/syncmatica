package ch.endte.syncmatica.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

public record SyncmaticaPayload(SyncPacket syncInfo) implements CustomPayload {
    public static final String SYNCMATICA_CHANNEL = "syncmatica";
    public static final PacketCodec<PacketByteBuf, SyncmaticaPayload> PACKET_CODEC;
    public static final CustomPayload.Id<SyncmaticaPayload> PAYLOAD_TYPE;

    // PacketCodec.decoder interface
    private SyncmaticaPayload(PacketByteBuf buf) {
        this(new SyncPacket(buf));
    }
    public SyncmaticaPayload(SyncPacket syncInfo) { this.syncInfo = syncInfo; }

    // PacketCodec.encoder interface
    public void write(PacketByteBuf buf) { this.syncInfo.write(buf); }
    public CustomPayload.Id<SyncmaticaPayload> getId() { return PAYLOAD_TYPE; }
    public SyncPacket syncInfo() { return this.syncInfo; }
    public UUID getUUID() { return this.syncInfo.uuid; }
    public Identifier getIdentifier() { return this.syncInfo.identifier; }
    public PacketByteBuf getPacket() { return this.syncInfo.packet; }
    public static PacketCodec<PacketByteBuf, SyncmaticaPayload> createCodec() {
        return CustomPayload.codecOf(SyncmaticaPayload::write, SyncmaticaPayload::new);
    }
    private static CustomPayload.Id<SyncmaticaPayload> createChannel() {
        return CustomPayload.id(SYNCMATICA_CHANNEL);
    }
    public record SyncPacket(UUID uuid, Identifier identifier, PacketByteBuf packet) {
        public SyncPacket(PacketByteBuf buf) {
            this(buf.readUuid(), buf.readIdentifier(), buf);
        }
        public SyncPacket(UUID uuid, Identifier identifier, PacketByteBuf packet) {
            this.uuid = uuid;
            this.identifier = identifier;
            this.packet = packet;
        }
        public void write(PacketByteBuf buf) {
            buf.writeUuid(this.uuid);
            buf.writeIdentifier(this.identifier);
            buf.writeBytes(new PacketByteBuf(buf));
        }
    }
    static {
        PACKET_CODEC = SyncmaticaPayload.createCodec();
        PAYLOAD_TYPE = SyncmaticaPayload.createChannel();
    }
}
