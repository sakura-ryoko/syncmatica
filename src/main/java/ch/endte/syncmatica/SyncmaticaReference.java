package ch.endte.syncmatica;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class SyncmaticaReference
{
    // Added this to quickly remove Compiler Warnings.
    private static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(Syncmatica.MOD_ID).get();
    public static final String MOD_VERSION = MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();
    private static final EnvType MOD_ENV = FabricLoader.getInstance().getEnvironmentType();
    private static boolean SINGLE_PLAYER = false;

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
    public static void setSinglePlayer()
    {
        SINGLE_PLAYER = true;
    }
}
