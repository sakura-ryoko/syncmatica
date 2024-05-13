package ch.endte.syncmatica;

import ch.endte.syncmatica.util.StringTools;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Main (Generic) Reference Calls --
 * Should use the "Context" versions of some of these for their respective context.
 * These are used to help control what contexts get registered and when (Server versus Client)
 */
public class Reference
{
    public static final String MOD_ID = "syncmatica";
    public static final String MOD_NAME = "Syncmatica";
    public static final String MOD_VERSION = StringTools.getModVersion(MOD_ID); // No more manually typing in the version # :)
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
    public static boolean isIntegratedServer()
    {
        return INTEGRATED_SERVER;
    }
    public static boolean isOpenToLan()
    {
        return OPEN_TO_LAN;
    }

    public static void setDedicatedServer(boolean toggle)
    {
        DEDICATED_SERVER = toggle;
    }
    public static void setIntegratedServer(boolean toggle)
    {
        INTEGRATED_SERVER = toggle;
    }
    public static void setOpenToLan(boolean toggle)
    {
        OPEN_TO_LAN = toggle;
    }
}
