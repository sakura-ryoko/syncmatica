package ch.endte.syncmatica.network.channels;

import ch.endte.syncmatica.network.payload.SyncByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncConfirmUser(SyncByteBuf byteBuf) implements CustomPayload
{
    public static final Id<SyncConfirmUser> TYPE = new Id<>(new Identifier("syncmatica", "confirm_user"));
    public static final PacketCodec<PacketByteBuf, SyncConfirmUser> CODEC = CustomPayload.codecOf(SyncConfirmUser::write, SyncConfirmUser::new);

    public SyncConfirmUser(PacketByteBuf input)
    {
        this(new SyncByteBuf(input.readBytes(input.readableBytes())));
    }

    private void write(PacketByteBuf output) { output.writeBytes(byteBuf); }
    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
}
