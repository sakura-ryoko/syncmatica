package ch.endte.syncmatica.network.legacy;

import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.PacketType;
import ch.endte.syncmatica.util.StringTools;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ChannelManager {
    private static final Identifier MINECRAFT_REGISTER = new Identifier("minecraft:register");
    private static final Identifier MINECRAFT_UNREGISTER = new Identifier("minecraft:unregister");
    private static final List<Identifier> serverRegisterChannels = new ArrayList<>();
    private static final List<Identifier> clientRegisterChannels = new ArrayList<>();

    @Deprecated
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

    @Deprecated
    public static void onChannelRegisterHandle(ExchangeTarget target, Identifier channel, PacketByteBuf data) {
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
                target.sendPacket(MINECRAFT_REGISTER, byteBuf2, null);
            }
        }
//        else if (channel.equals(MINECRAFT_UNREGISTER)) {
//            //TODO: 待实现，主要不知道数据格式，不过好像不影响使用
//        }
    }


    @Deprecated
    public static void onDisconnected() {
        clientRegisterChannels.clear();
        serverRegisterChannels.clear();
    }
}
