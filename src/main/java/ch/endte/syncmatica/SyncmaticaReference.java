package ch.endte.syncmatica;

import ch.endte.syncmatica.util.SyncLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

/**
 * Main (Generic) Reference Calls --
 * Should use the "Context" versions of some of these for their respective context.
 * These are used to control what channels get registered, and various "preInit" items
 * Using the Syncmatica.startup() as a wrapper doesn't seem to work.
 */
public class SyncmaticaReference
{
    public static final String MOD_ID = "syncmatica";
    public static final String MOD_VERSION = getModVersion();       // Get this value from the fabric.mod.json :)
    private static final EnvType MOD_ENV = FabricLoader.getInstance().getEnvironmentType();
    public static final boolean MOD_DEBUG = true;
    private static boolean SINGLE_PLAYER = false;
    private static boolean DEDICATED_SERVER = false;
    private static boolean INTEGRATED_SERVER = false;
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
