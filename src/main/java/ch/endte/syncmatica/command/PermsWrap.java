package ch.endte.syncmatica.command;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PermsWrap
{
	public static final String REGEX_ALLOWED = "[^a-z0-9:_./\\-]+";        // Identifier Safe

	public static boolean check(@Nonnull CommandSourceStack src, @Nonnull String node, int level)
	{
		return check(src, node, PermissionLevel.byId(Mth.clamp(level, 0, PermissionLevel.OWNERS.id())));
	}

	public static boolean check(@Nonnull CommandSourceStack src, @Nonnull String node, @Nonnull PermissionLevel pl)
	{
		Identifier id = Identifier.tryParse(sanitizeNode(node));

		if (id != null)
		{
			if (src.getPlayer() != null)
			{
				return src.getPlayer().checkPermission(id, pl);
			}

			return src.checkPermission(id, pl);
		}

		return false;
	}

	public static boolean check(@Nonnull Entity src, @Nonnull String node, int level)
	{
		return check(src, node, PermissionLevel.byId(Mth.clamp(level, 0, PermissionLevel.OWNERS.id())));
	}

	public static boolean check(@Nonnull Entity src, @Nonnull String node, @Nonnull PermissionLevel pl)
	{
		Identifier id = Identifier.tryParse(sanitizeNode(node));

		if (id != null)
		{
			return src.checkPermission(id, pl);
		}

		return false;
	}

	public static String sanitizeNode(@NotNull final String node)
	{
		return node.toLowerCase().replaceAll(REGEX_ALLOWED, "");
	}
}
