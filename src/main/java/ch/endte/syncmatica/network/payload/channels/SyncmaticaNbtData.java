package ch.endte.syncmatica.network.payload.channels;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncmaticaNbtData(NbtCompound data) implements CustomPayload
{
    public static final Id<SyncmaticaNbtData> TYPE = new Id<>(new Identifier("syncmatica", "nbt_data"));
    public static final PacketCodec<PacketByteBuf, SyncmaticaNbtData> CODEC = CustomPayload.codecOf(SyncmaticaNbtData::write, SyncmaticaNbtData::new);

    public SyncmaticaNbtData(PacketByteBuf buf) { this(buf.readNbt()); }

    private void write(PacketByteBuf buf) { buf.writeNbt(data); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
