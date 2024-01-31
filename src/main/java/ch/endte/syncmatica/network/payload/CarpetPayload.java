package ch.endte.syncmatica.network.payload;

import ch.endte.syncmatica.network.PayloadType;
import ch.endte.syncmatica.network.PayloadTypeRegister;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record CarpetPayload(NbtCompound data) implements CustomPayload
{
    public static final Id<CarpetPayload> TYPE = new Id<>(PayloadTypeRegister.getIdentifier(PayloadType.CARPET_HELLO));
    public static final PacketCodec<PacketByteBuf, CarpetPayload> CODEC = CustomPayload.codecOf(CarpetPayload::write, CarpetPayload::new);
    public static final String KEY = PayloadTypeRegister.getKey(PayloadType.CARPET_HELLO);

    public CarpetPayload(PacketByteBuf buf) { this(buf.readNbt()); }
    private void write(PacketByteBuf buf) { buf.writeNbt(data); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
