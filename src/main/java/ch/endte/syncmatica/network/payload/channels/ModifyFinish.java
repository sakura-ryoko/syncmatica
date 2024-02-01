package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ModifyFinish(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<ModifyFinish> TYPE = new Id<>(new Identifier("syncmatica", "modify_finish"));
    public static final PacketCodec<PacketByteBuf, ModifyFinish> CODEC = CustomPayload.codecOf(ModifyFinish::write, ModifyFinish::new);

    public ModifyFinish(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
