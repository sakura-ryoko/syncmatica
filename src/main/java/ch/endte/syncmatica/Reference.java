package ch.endte.syncmatica;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

/**
 * Main (Generic) Reference Calls --
 * Should use the "Context" versions of some of these for their respective context.
 * These are used to control what channels get registered and when.
 */
public class Reference
{
    public static final String MOD_ID = "syncmatica";
    public static final String MOD_NAME = "Syncmatica";
    public static final String MOD_VERSION = getModVersion();       // Get this value from the fabric.mod.json :)
    private static final EnvType MOD_ENV = FabricLoader.getInstance().getEnvironmentType();
    public static final boolean MOD_DEBUG = false;

    private static boolean DEDICATED_SERVER = false;
    private static boolean INTEGRATED_SERVER = false;
    private static boolean OPEN_TO_LAN = false;
    public static boolean isClient()
    {
        return MOD_ENV == EnvType.CLIENT;
    }
    public static boolean isServer()
    {
        return MOD_ENV == EnvType.SERVER;
    }
    public static boolean isDedicatedServer()
    {
        return DEDICATED_SERVER;
    }
    public static void setDedicatedServer(boolean toggle)
    {
        DEDICATED_SERVER = toggle;
    }
    public static boolean isIntegratedServer()
    {
        return INTEGRATED_SERVER;
    }
    public static void setIntegratedServer(boolean toggle)
    {
        INTEGRATED_SERVER = toggle;
    }
    public static boolean isOpenToLan()
    {
        return OPEN_TO_LAN;
    }
    public static void setOpenToLan(boolean toggle)
    {
        OPEN_TO_LAN = toggle;
    }

    protected static String getModVersion()
    {
        final Optional<ModContainer> CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID);
        if (CONTAINER.isPresent())
        {
            return CONTAINER.get().getMetadata().getVersion().getFriendlyString();
        }
        else return "?";
    }
}
