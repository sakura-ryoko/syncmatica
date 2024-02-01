package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ModifyRequestAccept(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<ModifyRequestAccept> TYPE = new Id<>(new Identifier("syncmatica", "modify_request_accept"));
    public static final PacketCodec<PacketByteBuf, ModifyRequestAccept> CODEC = CustomPayload.codecOf(ModifyRequestAccept::write, ModifyRequestAccept::new);

    public ModifyRequestAccept(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
