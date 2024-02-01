package ch.endte.syncmatica.network.payload.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfirmUser(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<ConfirmUser> TYPE = new Id<>(new Identifier("syncmatica", "confirm_user"));
    public static final PacketCodec<PacketByteBuf, ConfirmUser> CODEC = CustomPayload.codecOf(ConfirmUser::write, ConfirmUser::new);

    public ConfirmUser(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
