package ch.endte.syncmatica.network.payload;

import ch.endte.syncmatica.network.PayloadType;
import ch.endte.syncmatica.network.PayloadTypeRegister;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ServuxPayload(NbtCompound data) implements CustomPayload
{
    public static final Id<ServuxPayload> TYPE = new Id<>(PayloadTypeRegister.getIdentifier(PayloadType.SERVUX));
    public static final PacketCodec<PacketByteBuf, ServuxPayload> CODEC = CustomPayload.codecOf(ServuxPayload::write, ServuxPayload::new);
    public static final String KEY = PayloadTypeRegister.getKey(PayloadType.SERVUX);

    public ServuxPayload(PacketByteBuf buf) { this(buf.readNbt()); }

    private void write(PacketByteBuf buf) { buf.writeNbt(data); }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
