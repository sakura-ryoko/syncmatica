package ch.endte.syncmatica.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public interface IServerCommand
{
    /**
     * Register a Server Side command
     */
    void register(CommandDispatcher<ServerCommandSource> dispatcher,
                  CommandRegistryAccess registryAccess,
                  CommandManager.RegistrationEnvironment environment);
}
