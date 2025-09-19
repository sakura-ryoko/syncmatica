package ch.endte.syncmatica.extended_core;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerIdentifierProvider {
    private final Map<UUID, PlayerIdentifier> identifiers = new HashMap<>();
    private final Context context;

    public PlayerIdentifierProvider(final Context context) {
        this.context = context;
        identifiers.put(PlayerIdentifier.MISSING_PLAYER_UUID, PlayerIdentifier.MISSING_PLAYER);
    }

    public PlayerIdentifier createOrGet(final ExchangeTarget exchangeTarget) {
        final ServerCommunicationManager profileProvider = (ServerCommunicationManager) context.getCommunicationManager();
        return createOrGet(profileProvider.getGameProfile(exchangeTarget));
    }

    public PlayerIdentifier createOrGet(final GameProfile gameProfile) {
        return createOrGet(gameProfile.id(), gameProfile.name());
    }

    public PlayerIdentifier createOrGet(final UUID uuid, final String playerName) {
        final PlayerIdentifier playerIdentifier = identifiers.computeIfAbsent(uuid, id -> new PlayerIdentifier(uuid, playerName));
        if (!context.isServer()) {
            playerIdentifier.updatePlayerName(playerName); // trust that the latest value is up-to-date on the client
        }
        return playerIdentifier;
    }

    public void updateName(final UUID uuid, final String playerName) {
        createOrGet(uuid, playerName).updatePlayerName(playerName);
    }

    public PlayerIdentifier fromJson(final JsonObject obj) {
        if (!obj.has("uuid") || !obj.has("name")) {
            return PlayerIdentifier.MISSING_PLAYER;
        }

        final UUID jsonUUID = UUID.fromString(obj.get("uuid").getAsString());

        return identifiers.computeIfAbsent(jsonUUID,
                key -> new PlayerIdentifier(jsonUUID, obj.get("name").getAsString())
        );
    }
}
