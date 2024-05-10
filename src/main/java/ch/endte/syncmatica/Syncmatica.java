package ch.endte.syncmatica;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.network.client.ClientPlayRegister;
import ch.endte.syncmatica.network.packet.ActorClientPlayHandler;
import ch.endte.syncmatica.network.payload.PayloadManager;
import ch.endte.syncmatica.network.server.ServerPlayRegister;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.util.Identifier;

// could probably turn this into a singleton
public class Syncmatica
{
    protected static final String SERVER_PATH = "." + File.separator + "syncmatics";
    protected static final String CLIENT_PATH = "." + File.separator + "schematics" + File.separator + "sync";
    public static final Identifier CLIENT_CONTEXT = new Identifier("syncmatica", "client_context");
    public static final Identifier SERVER_CONTEXT = new Identifier("syncmatica", "server_context");
    public static final UUID syncmaticaId = UUID.fromString("4c1b738f-56fa-4011-8273-498c972424ea");
    protected static Map<Identifier, Context> contexts = null;
    protected static boolean hasMaLiLib = false;
    protected static boolean hasLitematica = false;
    protected static boolean context_init = false;

    /**
     * Tasks to be run at Mod Init, such as register Play Channels
     */
    public static void preInit()
    {
        SyncLog.initLogger();

        if (Reference.isClient())
        {
            // Client preInit()
            hasMaLiLib = Reference.checkForMaLiLib();
            hasLitematica = Reference.checkForLitematica();
            if (hasMaLiLib && hasLitematica)
            {
                PayloadManager.registerPlayChannels();
            }
        }
        else
        {
            // Server preInit()
            PayloadManager.registerPlayChannels();
        }
    }

    public static Context getContext(final Identifier id)
    {
        if (context_init)
            return contexts.get(id);
        else return null;
    }

    static void init(final Context con, final Identifier contextId) {
        SyncLog.debug("Syncmatica#init(): invoked.");
        if (contexts == null) {
            contexts = new HashMap<>();
        }
        if (!contexts.containsKey(contextId)) {
            contexts.put(contextId, con);
        }
        context_init = true;
    }

    public static void shutdown() {
        SyncLog.debug("Syncmatica#shutdown(): invoked.");
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
        context_init = false;
    }

    public static Context initClient(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics) {
        //SyncLog.initLogger();
        SyncLog.debug("Syncmatica#initClient(): invoked.");

        // These just try to verify that the Fabric API Networking is initialized.
        // In my testing, this really isn't useful to put here, it's probably redundant
        hasMaLiLib = Reference.checkForMaLiLib();
        hasLitematica = Reference.checkForLitematica();
        PayloadManager.registerPlayChannels();
        ClientPlayRegister.registerReceivers();

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
        SyncLog.debug("Syncmatica#restartClient(): invoked.");
        final Context oldClient = getContext(CLIENT_CONTEXT);
        if (oldClient != null) {
            if (oldClient.isStarted()) {
                oldClient.shutdown();
            }

            contexts.remove(CLIENT_CONTEXT);
        }

        ActorClientPlayHandler.getInstance().startClient();
    }

    public static Context initServer(final CommunicationManager comms, final IFileStorage fileStorage, final SyncmaticManager schematics, final boolean isIntegratedServer, final File worldPath) {
        //SyncLog.initLogger();
        SyncLog.debug("Syncmatica#initServer(): invoked.");

        // These just try to verify that the Fabric API Networking is initialized.
        // In my testing, this really isn't useful to put here, it's a redundant call
        PayloadManager.registerPlayChannels();
        ServerPlayRegister.registerReceivers();

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
