package ch.endte.syncmatica.network.s2c;

import ch.endte.syncmatica.network.PayloadType;
import ch.endte.syncmatica.network.PayloadTypeRegister;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncmaticaS2CPayload(NbtCompound data) implements CustomPayload
{
    public static final Id<SyncmaticaS2CPayload> TYPE = new Id<>(PayloadTypeRegister.getIdentifier(PayloadType.SYNCMATICA_S2C));
    public static final PacketCodec<PacketByteBuf, SyncmaticaS2CPayload> CODEC = CustomPayload.codecOf(SyncmaticaS2CPayload::write, SyncmaticaS2CPayload::new);
    public static final String KEY = PayloadTypeRegister.getKey(PayloadType.SYNCMATICA_S2C);

    public SyncmaticaS2CPayload(PacketByteBuf buf) { this(buf.readNbt()); }
    private void write(PacketByteBuf buf) { buf.writeNbt(data); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
