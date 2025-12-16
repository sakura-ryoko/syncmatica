package ch.endte.syncmatica.command;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.server.command.ServerCommandSource;

public class PermsWrap
{
	public static Predicate<ServerCommandSource> check(@Nonnull String node, int level)
	{
		return Permissions.require(node, level);
	}
}
