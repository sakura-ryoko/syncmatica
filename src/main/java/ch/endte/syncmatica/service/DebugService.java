package ch.endte.syncmatica.service;

import ch.endte.syncmatica.network.packet.SyncmaticaPacketType;
import ch.endte.syncmatica.util.SyncLog;

public class DebugService extends AbstractService {

    private boolean doPacketLogging = true;
    public void logReceivePacket(final SyncmaticaPacketType packetType) {
        if (doPacketLogging) {
            SyncLog.info("Syncmatica - received packet:[type={}]", packetType.toString());
        }
    }

    public void logSendPacket(final SyncmaticaPacketType packetType, final String targetIdentifier) {
        if (doPacketLogging) {
            SyncLog.info(
                    "Sending packet[type={}] to ExchangeTarget[id={}]",
                    packetType.toString(),
                    targetIdentifier
            );
        }
    }
    @Override
    public void getDefaultConfiguration(final IServiceConfiguration configuration) {
        configuration.saveBoolean("doPackageLogging", false);
    }

    @Override
    public String getConfigKey() {
        return "debug";
    }

    @Override
    public void configure(final IServiceConfiguration configuration) {
        configuration.loadBoolean("doPackageLogging", b -> doPacketLogging = b);
    }

    @Override
    public void startup() { //NOSONAR
    }

    @Override
    public void shutdown() { //NOSONAR
    }
}
