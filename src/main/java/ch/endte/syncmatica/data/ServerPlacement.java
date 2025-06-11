package ch.endte.syncmatica.data;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.extended_core.PlayerIdentifier;
import ch.endte.syncmatica.extended_core.SubRegionData;
import ch.endte.syncmatica.litematica.schematic.SchematicMetadata;
import ch.endte.syncmatica.litematica.schematic.SchematicSchema;
import ch.endte.syncmatica.material.SyncmaticaMaterialList;
import ch.endte.syncmatica.util.SyncmaticaUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class ServerPlacement
{
    private final UUID id;
    private final Path file; // Stores as a Path just for easier file name operations
    private final String fileName; // The basic "file name" field that may, or may not point to the actual origin file.
    private final UUID hashValue; // UUID for the file contents
    // UUID since easier to transmit compare etc.

    private PlayerIdentifier owner; // player that shared it
    private PlayerIdentifier lastModifiedBy; // player that last modified it

    private ServerPosition origin;
    private BlockRotation rotation;
    private BlockMirror mirror;
    private SubRegionData subRegionData = new SubRegionData();

    // Feature.DISPLAY_NAME
    private String displayName; // Save the proper Display Name of the Litematic file
    private boolean dirty;

    // Feature.VERSION
    private int dataVersion;
    private int litematicVersion;

    private SyncmaticaMaterialList matList;

    public ServerPlacement(final UUID id, final Path file, final String fileName, final String displayName,
                           final UUID hashValue, final PlayerIdentifier owner, int litematicVersion, int dataVersion)
    {
        this.id = id;
        this.file = file;
        this.fileName = fileName;
        this.displayName = displayName;
        this.hashValue = hashValue;
        this.owner = owner;
        this.lastModifiedBy = owner;
        this.litematicVersion = litematicVersion;
        this.dataVersion = dataVersion;
    }

    public ServerPlacement(final UUID id, final Path file, final String displayName, final PlayerIdentifier owner)
    {
//        this(id, file, removeExtension(file), displayName, generateHash(file), owner, -1, -1);
        this(id, file, file.toAbsolutePath().toString(), displayName, generateHash(file), owner, -1, -1);
    }

    public ServerPlacement(UUID id, String fileName, final String displayName, UUID hash, PlayerIdentifier owner)
    {
        this(id, Path.of(fileName), fileName, displayName, hash, owner, -1, -1);
    }

    public ServerPlacement(UUID id, String fileName, final String displayName, UUID hash, PlayerIdentifier owner,
                           int litematicVersion, int dataVersion)
    {
        this(id, Path.of(fileName), fileName, displayName, hash, owner, litematicVersion, dataVersion);
    }

    public UUID getId()
    {
        return id;
    }

    public Path getFile()
    {
        return this.file;
    }

    public String getName()
    {
//        if (this.getFileName().contains(".")) {
//            return removeExtension(this.getFileName());
//        }
//
//        return this.getFileName();
        return this.displayName;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public UUID getHash()
    {
        return hashValue;
    }

    public String getDimension()
    {
        return origin.getDimensionId();
    }

    public BlockPos getPosition()
    {
        return origin.getBlockPosition();
    }

    public ServerPosition getOrigin()
    {
        return origin;
    }

    public BlockRotation getRotation()
    {
        return rotation;
    }

    public BlockMirror getMirror()
    {
        return mirror;
    }

    // Feature.VERSION
    public int getLitematicVersion() {return litematicVersion;}

    public int getDataVersion() {return dataVersion;}

    public ServerPlacement setVersion(final int litematicVersion, final int dataVersion)
    {
        this.litematicVersion = litematicVersion;
        this.dataVersion = dataVersion;
        return this;
    }

    public ServerPlacement move(final String dimensionId, final BlockPos origin, final BlockRotation rotation,
                                final BlockMirror mirror)
    {
        move(new ServerPosition(origin, dimensionId), rotation, mirror);
        return this;
    }

    public ServerPlacement move(final ServerPosition origin, final BlockRotation rotation, final BlockMirror mirror)
    {
        this.origin = origin;
        this.rotation = rotation;
        this.mirror = mirror;
        return this;
    }

    public ServerPlacement setSchema(SchematicSchema schema)
    {
        this.litematicVersion = schema.litematicVersion();
        this.dataVersion = schema.minecraftDataVersion();
        return this;
    }

    public ServerPlacement setMetadata(SchematicMetadata meta)
    {
        this.displayName = meta.getName();
        return this;
    }

    public PlayerIdentifier getOwner()
    {
        return owner;
    }

    public void setOwner(final PlayerIdentifier playerIdentifier)
    {
        owner = playerIdentifier;
    }

    public PlayerIdentifier getLastModifiedBy()
    {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final PlayerIdentifier lastModifiedBy)
    {
        this.lastModifiedBy = lastModifiedBy;
    }

    public SubRegionData getSubRegionData()
    {
        return subRegionData;
    }

    public SyncmaticaMaterialList getMaterialList()
    {
        return matList;
    }

    public ServerPlacement setMaterialList(final SyncmaticaMaterialList matList)
    {
        if (this.matList != null)
        {
            this.matList = matList;
        }
        return this;
    }

    public static String removeExtension(final Path file)
    {
        // source stackoverflow
//        final String fileName = file.getName();
        final String fileName = file.toString();
        final int pos = fileName.lastIndexOf(".");
        return fileName.substring(0, pos);
    }

    public static String removeExtension(final String fileName)
    {
        // source stackoverflow
        final int pos = fileName.lastIndexOf(".");
        return fileName.substring(0, pos);
    }

    public static String normalizeFileName(final String badFileName)
    {
        String fileName = badFileName;

        if (badFileName.contains("/") || badFileName.contains("\\"))
        {
            Path dirtyPath = Paths.get(badFileName);
            fileName = dirtyPath.getFileName().toString();
            Syncmatica.debug("normalizeFileName(): Normalizing placement filename '{}' to: '{}'", badFileName,
                             fileName);
        }

        return fileName;
    }

    public String getNormalFileName()
    {
        return normalizeFileName(this.fileName);
    }

    private static UUID generateHash(final Path file)
    {
        UUID hash = null;
        try
        {
            hash = SyncmaticaUtil.createChecksum(new FileInputStream(file.toFile()));
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
        return hash;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void markDirty()
    {
        this.dirty = true;
    }

    public JsonObject toJson()
    {
        final JsonObject obj = new JsonObject();
        obj.add("id", new JsonPrimitive(id.toString()));

        obj.add("file_name", new JsonPrimitive(this.fileName));
        // Feature.DISPLAY_NAME
        obj.add("display_name", new JsonPrimitive(this.displayName));
        obj.add("hash", new JsonPrimitive(hashValue.toString()));

        obj.add("origin", origin.toJson());
        obj.add("rotation", new JsonPrimitive(rotation.name()));
        obj.add("mirror", new JsonPrimitive(mirror.name()));

        obj.add("owner", owner.toJson());
        if (!owner.equals(lastModifiedBy))
        {
            obj.add("lastModifiedBy", lastModifiedBy.toJson());
        }

        if (subRegionData.isModified())
        {
            obj.add("subregionData", subRegionData.toJson());
        }
        // Feature.VERSION
        if (litematicVersion > -1)
        {
            obj.add("litematicVersion", new JsonPrimitive(litematicVersion));
        }
        if (dataVersion > -1)
        {
            obj.add("dataVersion", new JsonPrimitive(dataVersion));
        }

        return obj;
    }

    public static ServerPlacement fromJson(final JsonObject obj, final Context context)
    {
        if (obj.has("id")
            && obj.has("file_name")
            && obj.has("hash")
            && obj.has("origin")
            && obj.has("rotation")
            && obj.has("mirror"))
        {
            final UUID id = UUID.fromString(obj.get("id").getAsString());
            final String fileName = obj.get("file_name").getAsString();
            final UUID hashValue = UUID.fromString(obj.get("hash").getAsString());
            String displayName;
            int version = -1;
            int dataVersion = -1;
            boolean dirty = false;

            PlayerIdentifier owner = PlayerIdentifier.MISSING_PLAYER;
            if (obj.has("owner"))
            {
                owner = context.getPlayerIdentifierProvider().fromJson(obj.get("owner").getAsJsonObject());
            }

            // Feature.DISPLAY_NAME
            if (obj.has("display_name"))
            {
                displayName = obj.get("display_name").getAsString();
            }
            else
            {
                // Check for Absolute Paths being used, and fix
                displayName = SyncmaticaUtil.sanitizeUnicodeFileName(normalizeFileName(fileName));
                dirty = true;
            }

            // Feature.VERSION
            if (obj.has("litematicVersion"))
            {
                version = obj.get("litematicVersion").getAsInt();
            }
            if (obj.has("dataVersion"))
            {
                dataVersion = obj.get("dataVersion").getAsInt();
            }

            final ServerPlacement newPlacement = new ServerPlacement(id, fileName, displayName, hashValue, owner,
                                                                     version, dataVersion);

            final ServerPosition pos = ServerPosition.fromJson(obj.get("origin").getAsJsonObject());
            if (pos == null)
            {
                return null;
            }
            newPlacement.origin = pos;
            newPlacement.rotation = BlockRotation.valueOf(obj.get("rotation").getAsString());
            newPlacement.mirror = BlockMirror.valueOf(obj.get("mirror").getAsString());

            if (obj.has("lastModifiedBy"))
            {
                newPlacement.lastModifiedBy = context.getPlayerIdentifierProvider()
                                                     .fromJson(obj.get("lastModifiedBy").getAsJsonObject());
            }
            else
            {
                newPlacement.lastModifiedBy = owner;
                dirty = true;
            }

            if (obj.has("subregionData"))
            {
                newPlacement.subRegionData = SubRegionData.fromJson(obj.get("subregionData"));
            }

            // This means that something has changed to correct the placement data at load time
            if (dirty)
            {
                newPlacement.markDirty();
            }

            return newPlacement;
        }

        return null;
    }
}
