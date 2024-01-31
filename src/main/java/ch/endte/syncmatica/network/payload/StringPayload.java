package ch.endte.syncmatica.network.payload;

import ch.endte.syncmatica.network.PayloadType;
import ch.endte.syncmatica.network.PayloadTypeRegister;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record StringPayload(String data) implements CustomPayload
{
    public static final Id<StringPayload> TYPE = new Id<>(PayloadTypeRegister.getIdentifier(PayloadType.STRING));
    public static final PacketCodec<PacketByteBuf, StringPayload> CODEC = CustomPayload.codecOf(StringPayload::write, StringPayload::new);
    public static final String KEY = PayloadTypeRegister.getKey(PayloadType.STRING);
    public StringPayload(PacketByteBuf buf) { this(buf.readString()); }
    public void write(PacketByteBuf buf) { buf.writeString(this.data); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
