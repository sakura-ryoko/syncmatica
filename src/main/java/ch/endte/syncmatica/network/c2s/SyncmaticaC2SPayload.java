package ch.endte.syncmatica.network.c2s;

import ch.endte.syncmatica.network.PayloadType;
import ch.endte.syncmatica.network.PayloadTypeRegister;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncmaticaC2SPayload(NbtCompound data) implements CustomPayload
{
    public static final Id<SyncmaticaC2SPayload> TYPE = new Id<>(PayloadTypeRegister.getIdentifier(PayloadType.SYNCMATICA_C2S));
    public static final PacketCodec<PacketByteBuf, SyncmaticaC2SPayload> CODEC = CustomPayload.codecOf(SyncmaticaC2SPayload::write, SyncmaticaC2SPayload::new);
    public static final String KEY = PayloadTypeRegister.getKey(PayloadType.SYNCMATICA_C2S);

    public SyncmaticaC2SPayload(PacketByteBuf buf) { this(buf.readNbt()); }
    private void write(PacketByteBuf buf) { buf.writeNbt(data); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
