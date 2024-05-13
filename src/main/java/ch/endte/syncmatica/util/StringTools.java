package ch.endte.syncmatica.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class StringTools
{
    public static String getHexString(byte[] bytes) {
        List<String> list = new ArrayList<>();
        for (byte b : bytes) {
            list.add(String.format("%02x", b));
        }
        return String.join(" ", list);
    }

    public static String getModVersion(String modid)
    {
        final Optional<ModContainer> CONTAINER = FabricLoader.getInstance().getModContainer(modid);
        if (CONTAINER.isPresent())
        {
            return CONTAINER.get().getMetadata().getVersion().getFriendlyString();
        }
        else return "?";
    }
}
