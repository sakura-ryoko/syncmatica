package ch.endte.syncmatica.litematica.schematic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.util.StringRepresentable;
import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;

/**
 * Cloned from Litematica 1.21.5 -- Sakura
 */
public enum FileType implements StringRepresentable
{
	INVALID,
	UNKNOWN,
	JSON,
	LITEMATICA_SCHEMATIC,
	SCHEMATICA_SCHEMATIC,
	SPONGE_SCHEMATIC,
	VANILLA_STRUCTURE;

	public static final EnumCodec<FileType> CODEC = StringRepresentable.fromEnum(FileType::values);
	public static final ImmutableList<FileType> VALUES = ImmutableList.copyOf(values());

	public Codec<FileType> codec()
	{
		return CODEC;
	}

	public static FileType fromName(String fileName)
	{
		if (fileName.endsWith(".litematic"))
		{
			return LITEMATICA_SCHEMATIC;
		}
		else if (fileName.endsWith(".schematic"))
		{
			return SCHEMATICA_SCHEMATIC;
		}
		else if (fileName.endsWith(".nbt"))
		{
			return VANILLA_STRUCTURE;
		}
		else if (fileName.endsWith(".schem"))
		{
			return SPONGE_SCHEMATIC;
		}
		else if (fileName.endsWith(".json"))
		{
			return JSON;
		}

		return UNKNOWN;
	}

	@Deprecated
	public static FileType fromFile(File file)
	{
		if (file.isFile() && file.canRead())
		{
			return fromName(file.getName());
		}
		else
		{
			return INVALID;
		}
	}

	public static FileType fromFile(Path file)
	{
		if (Files.exists(file) && Files.isReadable(file))
		{
			return fromName(file.getFileName().toString());
		}
		else
		{
			return INVALID;
		}
	}

	public static String getString(FileType type)
	{
		return switch (type)
		{
			case LITEMATICA_SCHEMATIC -> "litematic";
			case SCHEMATICA_SCHEMATIC -> "schematic";
			case SPONGE_SCHEMATIC -> "sponge";
			case VANILLA_STRUCTURE -> "vanilla_nbt";
			case JSON -> "JSON";
			case INVALID -> "invalid";
			case UNKNOWN -> "unknown";
		};
	}

	@Override
	public @NonNull String getSerializedName()
	{
		return getString(this);
	}
}
