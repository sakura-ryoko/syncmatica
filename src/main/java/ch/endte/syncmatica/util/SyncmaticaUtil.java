package ch.endte.syncmatica.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.litematica.schematic.SchematicMetadata;
import ch.endte.syncmatica.litematica.schematic.SchematicSchema;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class SyncmaticaUtil
{
    static final int[] ILLEGAL_CHARS = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
    static final String ILLEGAL_PATTERNS = "(^(con|prn|aux|nul|com[0-9]|lpt[0-9])(\\..*)?$)|(^\\.\\.*$)";

    private SyncmaticaUtil()
    {
        // NOT USED
    }

    public static UUID createChecksum(final InputStream fis) throws NoSuchAlgorithmException, IOException
    {
        // source StackOverflow
        final byte[] buffer = new byte[4096]; // 4096 is the most common cluster size
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        int numRead;

        do
        {
            numRead = fis.read(buffer);
            if (numRead > 0)
            {
                messageDigest.update(buffer, 0, numRead);
            }
        }
        while (numRead != -1);

        fis.close();
        return UUID.nameUUIDFromBytes(messageDigest.digest());
    }
    // taken from stackoverflow

    static
    {
        Arrays.sort(ILLEGAL_CHARS);
    }

    public static String sanitizeFileName(final String badFileName)
    {
        final StringBuilder sanitized = new StringBuilder();
        final int len = badFileName.codePointCount(0, badFileName.length());

        for (int i = 0; i < len; i++)
        {
            final int c = badFileName.codePointAt(i);
            if (Arrays.binarySearch(ILLEGAL_CHARS, c) < 0)
            {
                sanitized.appendCodePoint(c);
                if (sanitized.length() == 255)
                { //make sure .length stays below 255
                    break;
                }
            }
        }
        // ^ sanitizes unique characters
        // v sanitizes entire patterns
        return sanitized.toString().replaceAll(ILLEGAL_PATTERNS, "_");
    }

    public static void backupAndReplace(final Path backup, final Path current, final Path incoming)
    {
        if (!Files.exists(incoming))
        {
            return;
        }

        if (overwrite(backup, current, 2) && !overwrite(current, incoming, 4))
        {
            overwrite(current, backup, 8); // NOSONAR restore backup
        }
    }

    private static boolean overwrite(final Path backup, final Path current, final int tries)
    {
        if (!Files.exists(current))
        {
            return true;
        }
        try
        {
            Files.deleteIfExists(backup);
            Files.move(current, backup);
        }
        catch (final IOException exception)
        {
            if (tries <= 0)
            {
                Syncmatica.LOGGER.error("Excessive retries when trying to write Syncmatica placement", exception);

                return false;
            }
            return overwrite(backup, current, tries - 1);
        }

        return true;
    }

    public static double getBlockDistanceSquared(final BlockPos a, final double x, final double y, final double z)
    {
        final double combinedX = a.getX() - x;
        final double combinedY = a.getY() - y;
        final double combinedZ = a.getZ() - z;

        return combinedX * combinedX + combinedY * combinedY + combinedZ * combinedZ;
    }

    public static NbtCompound readNbtFromFile(@Nonnull Path file)
    {
        if (!Files.exists(file) || !Files.isReadable(file))
        {
            return new NbtCompound();
        }

        try
        {
            return NbtIo.readCompressed(Files.newInputStream(file), NbtSizeTracker.ofUnlimitedBytes());
        }
        catch (Exception e)
        {
            Syncmatica.LOGGER.warn("readNbtFromFile: Failed to read NBT data from file '{}'", file.toString());
        }

        return new NbtCompound();
    }

    public static Pair<SchematicMetadata, SchematicSchema> litematicPeek(Path file)
    {
        NbtCompound nbt = SyncmaticaUtil.readNbtFromFile(file);

        if (nbt.isEmpty() || !nbt.contains("Metadata"))
        {
            return Pair.of(null, null);
        }

        final int version = nbt.contains("Version") ? nbt.getInt("Version") : -1;
        final int dataVersion = nbt.contains("MinecraftDataVersion") ? nbt.getInt("MinecraftDataVersion") : -1;
        SchematicMetadata metadata = new SchematicMetadata();
        metadata.readFromNBT(nbt.getCompound("Metadata"));

        return Pair.of(metadata, new SchematicSchema(version, dataVersion));
    }

    public static @Nonnull NbtCompound createVec3iTag(@Nonnull Vec3i pos)
    {
        return putVec3i(new NbtCompound(), pos);
    }

    public static @Nonnull NbtCompound putVec3i(@Nonnull NbtCompound tag, @Nonnull Vec3i pos)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());

        return tag;
    }

    @Nullable
    public static Vec3i readVec3iFromTag(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            tag.contains("x") &&
            tag.contains("y") &&
            tag.contains("z"))
        {
            return new Vec3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }

        return null;
    }
}
