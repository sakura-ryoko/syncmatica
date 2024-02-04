package ch.endte.syncmatica.network.legacy;

import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.network.payload.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * I really think this code is nifty, but unfortunately, it's not needed if using Fabric Network API
 * to handle the channel registrations for us.  This doesn't even register the channels, only reports
 * on what channels are registered, and would need to be broken down to check for all Syncmatica
 * Payload types, then cast to it before using .sendPacket()
 */
@Deprecated
public class ChannelManager {
    private static final Identifier MINECRAFT_REGISTER = new Identifier("minecraft:register");
    private static final Identifier MINECRAFT_UNREGISTER = new Identifier("minecraft:unregister");
    private static final List<Identifier> serverRegisterChannels = new ArrayList<>();
    private static final List<Identifier> clientRegisterChannels = new ArrayList<>();

    private static List<Identifier> onReadRegisterIdentifier(PacketByteBuf data) {
        List<Identifier> identifiers = new ArrayList<>();
        int start = 0;
        while (data.isReadable()) {
            byte b = data.readByte();
            if (b == 0x00) {
                String string = data.toString(start, data.readerIndex() - start - 1, StandardCharsets.UTF_8);
                string = string.split("/")[0].split("\\\\")[0];
                identifiers.add(new Identifier(string));
                start = data.readerIndex();
            }
        }
        return identifiers;
    }

    public static void onChannelRegisterHandle(ExchangeTarget target, Identifier channel, PacketByteBuf data) {
        //if (channel.equals(PacketType.MINECRAFT_REGISTER.identifier)) {
        if (channel.equals(MINECRAFT_REGISTER)) {
            // 拷贝一份数据, 因为可能其他插件也存在通道
            List<Identifier> identifiers = onReadRegisterIdentifier(new PacketByteBuf(data.copy()));
            // 检查客户端是否已经注册该标识符, 如果已经注册那么向服务端发送注册请求
            PacketByteBuf byteBuf2 = new PacketByteBuf(Unpooled.buffer());
            List<Identifier> registerChannels = target.isClient() ? clientRegisterChannels : serverRegisterChannels;
            for (Identifier identifier : identifiers) {
                if (!registerChannels.contains(identifier) && PacketType.containsIdentifier(identifier)) {
                    LoggerFactory.getLogger("").info(identifier.toString());
                    byte[] bytes = identifier.toString().getBytes(StandardCharsets.UTF_8);
                    byteBuf2.writeBytes(bytes);
                    byteBuf2.writeByte(0x00);
                }
            }
            // 有数据时才有意义发送
            if (byteBuf2.writerIndex() > 0) {
                /**
                 * This would need to get refactored if you want to experiment with this
                 */
                //target.sendPacket(PacketType.MINECRAFT_REGISTER, byteBuf2, null);
                //target.sendPacket(MINECRAFT_REGISTER, byteBuf2, null);
            }
        }
//        else if (channel.equals(MINECRAFT_UNREGISTER)) {
//            //TODO: 待实现，主要不知道数据格式，不过好像不影响使用
//        }
    }

    public static void onDisconnected() {
        clientRegisterChannels.clear();
        serverRegisterChannels.clear();
    }
}
