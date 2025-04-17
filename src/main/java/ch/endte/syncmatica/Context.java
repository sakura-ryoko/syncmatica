package ch.endte.syncmatica;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import ch.endte.syncmatica.communication.CommunicationManager;
import ch.endte.syncmatica.communication.FeatureSet;
import ch.endte.syncmatica.data.IFileStorage;
import ch.endte.syncmatica.data.SyncmaticManager;
import ch.endte.syncmatica.extended_core.PlayerIdentifierProvider;
import ch.endte.syncmatica.network.SyncmaticaPacket;
import ch.endte.syncmatica.network.handler.ClientPlayHandler;
import ch.endte.syncmatica.network.handler.ServerPlayHandler;
import ch.endte.syncmatica.service.DebugService;
import ch.endte.syncmatica.service.IService;
import ch.endte.syncmatica.service.JsonConfiguration;
import ch.endte.syncmatica.service.QuotaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class Context
{
    private final IFileStorage files;
    private final CommunicationManager comMan;
    private final SyncmaticManager synMan;
    private FeatureSet fs = null;
    private final boolean server;
    private final boolean integratedServer;
    private final Path litematicFolder;
    private final Path worldFolder;
    private boolean isStarted = false;
    private final QuotaService quota;
    private final DebugService debugService;
    private final PlayerIdentifierProvider playerIdentifierProvider;
    private static boolean registerS2C = false;
    private static boolean registerC2S = false;

    public Context(
            final IFileStorage fs,
            final CommunicationManager comMan,
            final SyncmaticManager synMan,
            final Path litematicFolder
    ) {
        this(fs, comMan, synMan, false, litematicFolder, false, null);
    }

    public Context(
            final IFileStorage fs,
            final CommunicationManager comMan,
            final SyncmaticManager synMan,
            final boolean isServer,
            final Path litematicFolder,
            final boolean integrated,
            final Path worldFolder
    ) {
        files = fs;
        fs.setContext(this);
        this.comMan = comMan;
        comMan.setContext(this);
        this.synMan = synMan;
        synMan.setContext(this);
        server = isServer;
        if (isServer)
        {
            quota = new QuotaService();
        }
        else
        {
            quota = null;
        }
        playerIdentifierProvider = new PlayerIdentifierProvider(this);
        debugService = new DebugService();
        this.litematicFolder = litematicFolder;
//        if (!litematicFolder.exists())
        if (!Files.exists(litematicFolder))
        {
            try
            {
//                if (!litematicFolder.mkdirs())
                Files.createDirectory(litematicFolder);
            }
            catch (Exception e)
            {
                Syncmatica.LOGGER.fatal("Context(): Fatal error creating litematic Folder.  Exception: {}", e.getLocalizedMessage());
            }
        }
        integratedServer = integrated;
        this.worldFolder = worldFolder;
        loadConfiguration();
    }

    public PlayerIdentifierProvider getPlayerIdentifierProvider() {
        return playerIdentifierProvider;
    }

    public IFileStorage getFileStorage() {
        return files;
    }

    public CommunicationManager getCommunicationManager() {
        return comMan;
    }

    public SyncmaticManager getSyncmaticManager() {
        return synMan;
    }

    public QuotaService getQuotaService() {
        return quota;
    }

    public DebugService getDebugService() {
        return debugService;
    }

    public FeatureSet getFeatureSet() {
        if (fs == null) {
            generateFeatureSet();
        }
        return fs;
    }

    public boolean isServer() {
        return server;
    }

    public boolean isClient() {
        return !server;
    }

    public boolean isIntegratedServer() {
        return integratedServer;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public Path getLitematicFolder() {
        return litematicFolder;
    }

    private void generateFeatureSet() {
        fs = new FeatureSet(Arrays.asList(Feature.values()));
    }

    public void startup() {
        Syncmatica.debug("Context#startup()");
        registerReceivers();
        startupServices();
        isStarted = true;
        synMan.startup();
    }

    public void shutdown() {
        Syncmatica.debug("Context#shutdown()");
        shutdownServices();
        unregisterReceivers();
        isStarted = false;
        synMan.shutdown();
    }

    public void registerReceivers()
    {
        if (this.isServer() && !registerC2S)
        {
            if (Reference.isServer() || Reference.isIntegratedServer() || Reference.isOpenToLan())
            {
                Syncmatica.debug("Context#registerReceivers(): [SERVER] -> registerSyncmaticaHandler");
                ServerPlayNetworking.registerGlobalReceiver(SyncmaticaPacket.Payload.ID, ServerPlayHandler::receiveSyncPayload);
                registerC2S = true;
            }
            else
            {
                Syncmatica.LOGGER.error("Context#registerReceivers(): isServer() Exception");
            }
        }
        else
        {
            if (Reference.isClient() && !registerS2C)
            {
                Syncmatica.debug("Context#registerReceivers(): [CLIENT] -> registerSyncmaticaHandler");
                ClientPlayNetworking.registerGlobalReceiver(SyncmaticaPacket.Payload.ID, ClientPlayHandler::receiveSyncPayload);
                registerS2C = true;
            }
            else
            {
                Syncmatica.LOGGER.error("Context#registerReceivers(): isClient() Exception");
            }
        }
    }

    public void unregisterReceivers()
    {
        // Here, we shouldn't care about the Reference Status so we can have a clean deinit()
        if (this.isServer())
        {
            Syncmatica.debug("Context#unregisterReceivers(): [SERVER] -> unregisterSyncmaticaHandlers");
            ServerPlayNetworking.unregisterGlobalReceiver(SyncmaticaPacket.Payload.ID.id());
            registerC2S = false;
        }
        else
        {
            Syncmatica.debug("Context#unregisterReceivers(): [CLIENT] -> unregisterSyncmaticaHandlers");
            ClientPlayNetworking.unregisterGlobalReceiver(SyncmaticaPacket.Payload.ID.id());
            registerS2C = false;
        }
    }

    public boolean checkPartnerVersion(final String version) {
        return !version.equals("0.0.1");
    }

    public Path getConfigFolder()
    {
        if (this.isServer())
        {
//            return new File(worldFolder, Reference.MOD_ID);
            return worldFolder.resolve(Reference.MOD_ID).normalize();
        }

        //        return new File(new File("."), "config" + File.separator + Reference.MOD_ID);
        return Reference.CONFIG_ROOT.resolve(Reference.MOD_ID).normalize();
    }

    public Path getConfigFile() {
//        return new File(getConfigFolder(), "config.json");
        return this.getConfigFolder().resolve("config.json");
    }

    public Path getAndCreateConfigFile() throws IOException {
//        getConfigFolder().mkdirs();
//        final File configFile = getConfigFile();
//        configFile.createNewFile();
        Path dir = this.getConfigFolder();
        if (!Files.exists(dir))
        {
            Syncmatica.debug("creating config folder: [{}]", dir.toAbsolutePath().toString());
            Files.createDirectory(dir);
        }
        Syncmatica.debug("config dir: [{}]", dir.toAbsolutePath().toString());
        Path configFile = this.getConfigFile();
        if (!Files.exists(configFile))
        {
            Syncmatica.debug("creating config file: [{}]", configFile.getFileName().toString());
            Files.createFile(configFile);
        }
        Syncmatica.debug("config file: [{}]", configFile.getFileName().toString());
        return configFile;
    }

    public void loadConfiguration() {
        boolean attemptToLoad = false;
        JsonObject configuration;

        Syncmatica.debug("loadConfig(): config file: '{}'", this.getConfigFile().toAbsolutePath().toString());
        try {
            configuration = new Gson().fromJson(new BufferedReader(new FileReader(this.getConfigFile().toFile())), JsonObject.class);
            attemptToLoad = true;
        } catch (final Exception ignored) {
            configuration = new JsonObject();
        }
        boolean needsRewrite = false;
        if (isServer()) {
            needsRewrite = loadConfigurationForService(quota, configuration, attemptToLoad);
        }
        needsRewrite |= loadConfigurationForService(debugService, configuration, attemptToLoad);
        if (needsRewrite) {
            try (
                    final Writer writer = new BufferedWriter(new FileWriter(getAndCreateConfigFile().toFile()))
            ) {
                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                final String jsonString = gson.toJson(configuration);
                writer.write(jsonString);
            } catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private Boolean loadConfigurationForService(final IService service, final JsonObject configuration, final boolean attemptToLoad) {
        final String configKey = service.getConfigKey();
        JsonObject serviceJson = null;
        JsonConfiguration serviceConfiguration = null;
        boolean started = false;

        if (attemptToLoad && configuration.has(configKey)) {
            try {
                serviceJson = configuration.getAsJsonObject(configKey);
                if (serviceJson != null) {
                    serviceConfiguration = new JsonConfiguration(serviceJson);
                    service.configure(serviceConfiguration);
                    started = true;
                    if (!serviceConfiguration.hadError()) {
                        return false;
                    }
                }
            } catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
        if (serviceJson == null) {
            serviceJson = new JsonObject();
            configuration.add(configKey, serviceJson);
        }
        if (serviceConfiguration == null) {
            serviceConfiguration = new JsonConfiguration(serviceJson);
        }
        service.getDefaultConfiguration(serviceConfiguration);
        if (!started) {
            service.configure(serviceConfiguration);
        }
        return true;
    }

    private void startupServices() {
        Syncmatica.debug("Context#startupServices()");
        if (quota != null) {
            quota.startup();
        }
        debugService.startup();
    }

    private void shutdownServices() {
        Syncmatica.debug("Context#shutdownServices()");
        if (quota != null) {
            quota.shutdown();
        }
        debugService.shutdown();
    }

    public static class DuplicateContextAssignmentException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = -5147544661160756303L;

        public DuplicateContextAssignmentException(final String reason) {
            super(reason);
        }
    }

    public static class ContextMismatchException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 2769376183212635479L;

        public ContextMismatchException(final String reason) {
            super(reason);
        }
    }
}
