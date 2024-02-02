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
        LOGGER.debug("SyncLog#initLogger(): Logger initialized.");
    }

    public static void debug(String msg, Object... args)
    {
        String wrap1 = "["+SYNC_ID+":DEBUG] " + msg;
        String wrap2 = "["+SYNC_ID+"] " + msg;
        if (enabled)
        {
            if (SYNC_DEBUG)
                LOGGER.info(wrap1, args);
            else
                LOGGER.debug(wrap2, args);
        }
    }

    public static void info(String msg, Object... args)
    {
        String wrap = "["+SYNC_ID+"] " + msg;
        if (enabled)
            LOGGER.info(wrap, args);
    }

    public static void warn(String msg, Object... args)
    {
        String wrap = "["+SYNC_ID+"] " + msg;
        if (enabled)
            LOGGER.warn(wrap, args);
    }

    public static void error(String msg, Object... args)
    {
        String wrap = "["+SYNC_ID+"] " + msg;
        if (enabled)
            LOGGER.error(wrap, args);
    }

    public static void fatal(String msg, Object... args)
    {
        String wrap = "["+SYNC_ID+"] " + msg;
        if (enabled)
            LOGGER.fatal(wrap, args);
    }
}
