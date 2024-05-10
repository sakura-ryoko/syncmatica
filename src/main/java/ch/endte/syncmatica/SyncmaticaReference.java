package ch.endte.syncmatica;

import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.MinecraftVersion;

import java.util.Optional;

/**
 * Main (Generic) Reference Calls --
 * Should use the "Context" versions of some of these for their respective context.
 * These are used to control what channels get registered, and various "preInit" items.
 */
public class SyncmaticaReference
{
    public static final String MOD_ID = "syncmatica";
    public static final String MOD_NAME = "Syncmatica";
    public static final String MOD_VERSION = getModVersion();       // Get this value from the fabric.mod.json :)
    public static final String MC_VERSION = MinecraftVersion.CURRENT.getName();
    public static final String MOD_TYPE = "fabric";
    public static final String MOD_STRING = MOD_ID+"-"+MOD_TYPE+"-"+MC_VERSION+"-"+MOD_VERSION;
    private static final EnvType MOD_ENV = FabricLoader.getInstance().getEnvironmentType();
    public static final boolean MOD_DEBUG = true;

    private static boolean SINGLE_PLAYER = false;
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
    public static boolean isSinglePlayer()
    {
        return SINGLE_PLAYER;
    }
    public static void setSinglePlayer(boolean toggle)
    {
        SINGLE_PLAYER = toggle;
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

    protected static boolean checkForMaLiLib()
    {
        final Optional<ModContainer> CONTAINER = FabricLoader.getInstance().getModContainer("malilib");
        if (CONTAINER.isPresent())
        {
            String ver =  CONTAINER.get().getMetadata().getVersion().getFriendlyString();
            SyncLog.info("MaLiLib {} has been found.", ver);
            return true;
        }
        else
        {
            SyncLog.error("checkForMaLiLib(): MaLiLib has NOT been found.");
            return false;
        }
    }

    protected static boolean checkForLitematica()
    {
        final Optional<ModContainer> CONTAINER = FabricLoader.getInstance().getModContainer("litematica");
        if (CONTAINER.isPresent())
        {
            String ver =  CONTAINER.get().getMetadata().getVersion().getFriendlyString();
            SyncLog.info("Litematica {} has been found.", ver);
            return true;
        }
        else
        {
            SyncLog.error("checkForLitematica(): Litematica has NOT been found.");
            return false;
        }
    }
}
