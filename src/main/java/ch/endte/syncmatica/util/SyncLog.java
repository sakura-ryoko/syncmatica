package ch.endte.syncmatica.util;

import ch.endte.syncmatica.SyncmaticaReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Introduces a common logging format that can be used across the entire mod.
 * Could possibly be merged into DebugService, but I simply use this as a general
 * debugger.
 */
public class SyncLog
{
    private static Logger LOGGER;
    private static boolean enabled;
    private static final String SYNC_ID = SyncmaticaReference.MOD_ID;
    private static final boolean SYNC_DEBUG = SyncmaticaReference.MOD_DEBUG;
    private static final boolean WRAP_ID = false;

    public static void initLogger()
    {
        LOGGER = LogManager.getLogger(SYNC_ID);
        enabled = true;
        LOGGER.debug("SyncLog#initLogger(): Logger initialized.");
    }

    public static void debug(String msg, Object... args)
    {
        String wrapDebug;
        if (WRAP_ID)
            wrapDebug = "[" + SYNC_ID + ":DEBUG] " + msg;
        else
            wrapDebug = "[DEBUG] " + msg;
        if (enabled)
        {
            if (SYNC_DEBUG)
                LOGGER.info(wrapDebug, args);
            else
                LOGGER.debug(wrap(msg), args);
        }
    }

    public static void info(String msg, Object... args)
    {
        if (enabled)
            LOGGER.info(wrap(msg), args);
    }

    public static void warn(String msg, Object... args)
    {
        if (enabled)
            LOGGER.warn(wrap(msg), args);
    }

    public static void error(String msg, Object... args)
    {
        if (enabled)
            LOGGER.error(wrap(msg), args);
    }

    public static void fatal(String msg, Object... args)
    {
        if (enabled)
            LOGGER.fatal(wrap(msg), args);
    }

    private static String wrap(String msg)
    {
        if (WRAP_ID)
            return "["+SYNC_ID+"] " + msg;
        else
            return msg;
    }
}
