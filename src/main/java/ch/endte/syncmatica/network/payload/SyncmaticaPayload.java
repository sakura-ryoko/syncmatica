package ch.endte.syncmatica.network.payload;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncmaticaPayload(NbtCompound data) implements CustomPayload
{
    public static final Id<SyncmaticaPayload> TYPE = new Id<>(PayloadTypeRegister.getIdentifier(PayloadType.SYNCMATICA));
    public static final PacketCodec<PacketByteBuf, SyncmaticaPayload> CODEC = CustomPayload.codecOf(SyncmaticaPayload::write, SyncmaticaPayload::new);
    public static final String KEY = PayloadTypeRegister.getKey(PayloadType.SYNCMATICA);

    public SyncmaticaPayload(PacketByteBuf buf) { this(buf.readNbt()); }

    private void write(PacketByteBuf buf) { buf.writeNbt(data); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
