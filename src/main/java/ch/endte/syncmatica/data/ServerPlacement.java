package ch.endte.syncmatica.data;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.extended_core.PlayerIdentifier;
import ch.endte.syncmatica.extended_core.SubRegionData;
import ch.endte.syncmatica.material.SyncmaticaMaterialList;
import ch.endte.syncmatica.util.SyncmaticaUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class ServerPlacement {

    private final UUID id;

    private final String fileName;
    private final UUID hashValue; // UUID for the file contents
    // UUID since easier to transmit compare etc.

    private PlayerIdentifier owner; // player that shared it
    private PlayerIdentifier lastModifiedBy; // player that last modified it

    private ServerPosition origin;
    private BlockRotation rotation;
    private BlockMirror mirror;

    private SubRegionData subRegionData = new SubRegionData();

    private SyncmaticaMaterialList matList;

    public ServerPlacement(final UUID id, final String fileName, final UUID hashValue, final PlayerIdentifier owner) {
        this.id = id;
        this.fileName = fileName;
        this.hashValue = hashValue;
        this.owner = owner;
        lastModifiedBy = owner;
    }

    public ServerPlacement(final UUID id, final File file, final PlayerIdentifier owner) {
        this(id, removeExtension(file), generateHash(file), owner);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return fileName;
    }

    public UUID getHash() {
        return hashValue;
    }

    public String getDimension() {
        return origin.getDimensionId();
    }

    public BlockPos getPosition() {
        return origin.getBlockPosition();
    }

    public ServerPosition getOrigin() {
        return origin;
    }

    public BlockRotation getRotation() {
        return rotation;
    }

    public BlockMirror getMirror() {
        return mirror;
    }

    public ServerPlacement move(final String dimensionId, final BlockPos origin, final BlockRotation rotation, final BlockMirror mirror) {
        move(new ServerPosition(origin, dimensionId), rotation, mirror);
        return this;
    }

    public ServerPlacement move(final ServerPosition origin, final BlockRotation rotation, final BlockMirror mirror) {
        this.origin = origin;
        this.rotation = rotation;
        this.mirror = mirror;
        return this;
    }

    public PlayerIdentifier getOwner() {
        return owner;
    }

    public void setOwner(final PlayerIdentifier playerIdentifier) {
        owner = playerIdentifier;
    }

    public PlayerIdentifier getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final PlayerIdentifier lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public SubRegionData getSubRegionData() {
        return subRegionData;
    }

    public SyncmaticaMaterialList getMaterialList() {
        return matList;
    }

    public ServerPlacement setMaterialList(final SyncmaticaMaterialList matList) {
        if (this.matList != null) {
            this.matList = matList;
        }
        return this;
    }

    private static String removeExtension(final File file) {
        // source stackoverflow
        final String fileName = file.getName();
        final int pos = fileName.lastIndexOf(".");
        return fileName.substring(0, pos);
    }

    private static UUID generateHash(final File file) {
        UUID hash = null;
        try {
            hash = SyncmaticaUtil.createChecksum(new FileInputStream(file));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return hash;
    }

    public JsonObject toJson() {
        final JsonObject obj = new JsonObject();
        obj.add("id", new JsonPrimitive(id.toString()));

        obj.add("file_name", new JsonPrimitive(fileName));
        obj.add("hash", new JsonPrimitive(hashValue.toString()));

        obj.add("origin", origin.toJson());
        obj.add("rotation", new JsonPrimitive(rotation.name()));
        obj.add("mirror", new JsonPrimitive(mirror.name()));

        obj.add("owner", owner.toJson());
        if (!owner.equals(lastModifiedBy)) {
            obj.add("lastModifiedBy", lastModifiedBy.toJson());
        }

        if (subRegionData.isModified()) {
            obj.add("subregionData", subRegionData.toJson());
        }

        return obj;
    }

    public static ServerPlacement fromJson(final JsonObject obj, final Context context) {
        if (obj.has("id")
                && obj.has("file_name")
                && obj.has("hash")
                && obj.has("origin")
                && obj.has("rotation")
                && obj.has("mirror")) {
            final UUID id = UUID.fromString(obj.get("id").getAsString());
            final String name = obj.get("file_name").getAsString();
            final UUID hashValue = UUID.fromString(obj.get("hash").getAsString());

            PlayerIdentifier owner = PlayerIdentifier.MISSING_PLAYER;
            if (obj.has("owner")) {
                owner = context.getPlayerIdentifierProvider().fromJson(obj.get("owner").getAsJsonObject());
            }

            final ServerPlacement newPlacement = new ServerPlacement(id, name, hashValue, owner);

            final ServerPosition pos = ServerPosition.fromJson(obj.get("origin").getAsJsonObject());
            if (pos == null) {
                return null;
            }
            newPlacement.origin = pos;
            newPlacement.rotation = BlockRotation.valueOf(obj.get("rotation").getAsString());
            newPlacement.mirror = BlockMirror.valueOf(obj.get("mirror").getAsString());

            if (obj.has("lastModifiedBy")) {
                newPlacement.lastModifiedBy = context.getPlayerIdentifierProvider()
                        .fromJson(obj.get("lastModifiedBy").getAsJsonObject());
            } else {
                newPlacement.lastModifiedBy = owner;
            }

            if (obj.has("subregionData")) {
                newPlacement.subRegionData = SubRegionData.fromJson(obj.get("subregionData"));
            }

            return newPlacement;
        }

        return null;
    }
}
