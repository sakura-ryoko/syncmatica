package ch.endte.syncmatica;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.network.actor.ActorClientPlayHandler;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.Identifier;

// could probably turn this into a singleton
public class Syncmatica
{
    public static Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    protected static final String SERVER_PATH = "." + File.separator + "syncmatics";
    protected static final String CLIENT_PATH = "." + File.separator + "schematics" + File.separator + "sync";
    public static final Identifier CLIENT_CONTEXT = Identifier.of(Reference.MOD_ID, "client_context");
    public static final Identifier SERVER_CONTEXT = Identifier.of(Reference.MOD_ID, "server_context");
    public static final Identifier NETWORK_ID = Identifier.of(Reference.MOD_ID, "main");
    public static final UUID syncmaticaId = UUID.fromString("4c1b738f-56fa-4011-8273-498c972424ea");
    protected static Map<Identifier, Context> contexts = null;
    protected static boolean context_init = false;

    /**
     * Tasks to be run at Mod Init, such as register Play Channels
     */
    public static void preInit()
    {
        Syncmatica.debug("Syncmatica#preInit(): registering play channel(s)");
        PayloadTypeRegistry.playC2S().register(SyncmaticaPacket.Payload.ID, SyncmaticaPacket.Payload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncmaticaPacket.Payload.ID, SyncmaticaPacket.Payload.CODEC);
        // These need to be registered ASAP at launch.
    }

    /**
     * Streamlined debugging tool via MOD_DEBUG boolean
     * @param msg (message content)
     * @param args (variable args)
     */
    public static void debug(String msg, Object... args)
    {
        if (Reference.MOD_DEBUG)
        {
            LOGGER.info(msg, args);
        }
    }

    public static Context getContext(final Identifier id)
    {
        if (context_init)
            return contexts.get(id);
        else return null;
    }

    static void init(final Context con, final Identifier contextId) {
        Syncmatica.debug("Syncmatica#init()");

        if (contexts == null) {
            contexts = new HashMap<>();
        }
        if (!contexts.containsKey(contextId)) {
            contexts.put(contextId, con);
        }
        context_init = true;
    }

    public static void shutdown() {
        Syncmatica.debug("Syncmatica#shutdown()");

        if (contexts != null) {
            for (final Context con : contexts.values()) {
                if (con.isStarted()) {
                    con.shutdown();
                }
            }
        }
        deinit();
    }

    private static void deinit() {
        Syncmatica.debug("Syncmatica#deinit()");

        contexts = null;
        context_init = false;
    }

    public static Context initClient(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics)
    {
        Syncmatica.debug("Syncmatica#initClient()");

        final Context clientContext = new Context(
                fileStorage,
                comms,
                schematics,
                new File(CLIENT_PATH)
        );
        Syncmatica.init(clientContext, CLIENT_CONTEXT);
        return clientContext;
    }
    public static void restartClient() {
        Syncmatica.debug("Syncmatica#restartClient()");

        final Context oldClient = getContext(CLIENT_CONTEXT);
        if (oldClient != null) {
            if (oldClient.isStarted()) {
                oldClient.shutdown();
            }

            contexts.remove(CLIENT_CONTEXT);
        }

        ActorClientPlayHandler.getInstance().startClient();
    }

    public static Context initServer(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics,
                                     final boolean isIntegratedServer, final File worldPath)
    {
        Syncmatica.debug("Syncmatica#initServer()");

        final Context serverContext = new Context(
                fileStorage,
                comms,
                schematics,
                true,
                new File(SERVER_PATH),
                isIntegratedServer,
                worldPath
        );
        Syncmatica.init(serverContext, SERVER_CONTEXT);
        return serverContext;
    }

    protected Syncmatica() {}
}
