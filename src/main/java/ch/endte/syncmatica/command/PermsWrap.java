package ch.endte.syncmatica.command;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.util.Mth;

public class PermsWrap
{
	public static Predicate<CommandSourceStack> check(@Nonnull String node, PermissionLevel level)
	{
		return Permissions.require(node, level);
	}

	public static Predicate<CommandSourceStack> check(@Nonnull String node, int level)
	{
		return Permissions.require(node, PermissionLevel.byId(Mth.clamp(level, 0, PermissionLevel.OWNERS.id())));
	}
}
