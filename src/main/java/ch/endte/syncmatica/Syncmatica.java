package ch.endte.syncmatica;

import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.event.PlayerHandler;
import ch.endte.syncmatica.event.ServerHandler;
import ch.endte.syncmatica.listeners.PlayerListener;
import ch.endte.syncmatica.listeners.ServerListener;
import ch.endte.syncmatica.network.ClientNetworkPlayInitHandler;
import ch.endte.syncmatica.network.ServerNetworkPlayInitHandler;
import ch.endte.syncmatica.network.legacy.ActorClientPlayNetworkHandler;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// could probably turn this into a singleton
public class Syncmatica {
    protected static final String SERVER_PATH = "." + File.separator + "syncmatics";
    protected static final String CLIENT_PATH = "." + File.separator + "schematics" + File.separator + "sync";

    // #FIXME -- You really shouldn't be using Identifier's for this.  It works, but just don't pass it into a network interface.
    public static final Identifier CLIENT_CONTEXT = new Identifier("syncmatica", "client_context");
    public static final Identifier SERVER_CONTEXT = new Identifier("syncmatica", "server_context");
    public static final UUID syncmaticaId = UUID.fromString("4c1b738f-56fa-4011-8273-498c972424ea");
    protected static Map<Identifier, Context> contexts = null;
    protected static boolean MOD_INIT = false;
    protected static boolean hasMaLiLib = false;
    protected static boolean hasLitematica = false;
    public static Context getContext(final Identifier id) {
        return contexts.get(id);
    }

    /** This preInit() calls are for early network API initialization / Play Channel registration;
     * and is used to set up the Player / Server Handler interfaces for calling init() and handling
     * incoming packets.
     */
    public static void preInitClient()
    {
        SyncLog.initLogger();
        hasMaLiLib = SyncmaticaReference.checkForMaLiLib();
        hasLitematica = SyncmaticaReference.checkForLitematica();
        if (hasMaLiLib && hasLitematica) {

            SyncLog.debug("Syncmatica#preInitClient(): Register Client Play Channels.");
            ClientNetworkPlayInitHandler.registerPlayChannels();

            preInit();
        }
        // DO NOT init without MaLiLib / Litematica present
    }
    public static void preInitServer()
    {
        SyncLog.initLogger();
        SyncLog.debug("Syncmatica#preInitServer(): Register Server Play Channels.");
        ServerNetworkPlayInitHandler.registerPlayChannels();
        preInit();
    }
    private static void preInit()
    {
        SyncLog.debug("Syncmatica#preInit(): invoked.");

        // ServerListener interface (init() callbacks)
        ServerListener serverListener = new ServerListener();
        ServerHandler.getInstance().registerServerHandler(serverListener);

        // PlayerListener interface (onGameJoin callbacks)
        PlayerListener playerListener = new PlayerListener();
        PlayerHandler.getInstance().registerPlayerHandler(playerListener);
    }
    static void init(final Context con, final Identifier contextId) {
        if (contexts == null) {
            contexts = new HashMap<>();
        }
        if (!contexts.containsKey(contextId)) {
            contexts.put(contextId, con);
        }
    }

    public static void shutdown() {
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
        contexts = null;
    }

    public static Context initClient(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics) {
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
        final Context oldClient = getContext(CLIENT_CONTEXT);
        if (oldClient != null) {
            if (oldClient.isStarted()) {
                oldClient.shutdown();
            }

            contexts.remove(CLIENT_CONTEXT);
        }

        // #FIXME Perhaps?
        ActorClientPlayNetworkHandler.getInstance().startClient();
    }
    public static Context initServer(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics, final boolean isIntegratedServer, final File worldPath) {
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
    protected Syncmatica() { }
}
