package ch.endte.syncmatica.network.legacy;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of Bunny_i's ChannelManager
 */
@Deprecated
public class StringTools {
    public static String getHexString(byte[] bytes) {
        List<String> list = new ArrayList<>();
        for (byte b : bytes) {
            list.add(String.format("%02x", b));
        }
        return String.join(" ", list);
    }
}
