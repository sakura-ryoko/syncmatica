package ch.endte.syncmatica.util;

import ch.endte.syncmatica.SyncmaticaReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Introduces a common logging format.
 */
public class SyncLog
{
    private static Logger LOGGER;
    private static boolean enabled;
    private static final String SYNC_ID = SyncmaticaReference.MOD_ID;
    private static final boolean SYNC_DEBUG = SyncmaticaReference.MOD_DEBUG;
    public static void initLogger()
    {
        LOGGER = LogManager.getLogger(SYNC_ID);
        enabled = true;
        LOGGER.debug("Logger initialized.");
    }

    public static void debug(String msg, Object... args)
    {
        if (enabled)
        {
            if (SYNC_DEBUG)
                LOGGER.info("[{}:DEBUG] " + msg, SYNC_ID, args);
            else
                LOGGER.debug("[{}] "+ msg, SYNC_ID, args);
        }
    }

    public static void info(String msg, Object... args)
    {
        if (enabled)
            LOGGER.info("[{}] " + msg, SYNC_ID, args);
    }

    public static void warn(String msg, Object... args)
    {
        if (enabled)
            LOGGER.warn("[{}] " + msg, SYNC_ID, args);
    }

    public static void error(String msg, Object... args)
    {
        if (enabled)
            LOGGER.error("[{}] " + msg, SYNC_ID, args);
    }

    public static void fatal(String msg, Object... args)
    {
        if (enabled)
            LOGGER.fatal("[{}] " + msg, SYNC_ID, args);
    }
}
