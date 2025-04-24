package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.command.SyncmaticaCommand;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class MixinCommandManager
{
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/server/dedicated/command/WhitelistCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V",
                                        shift = At.Shift.AFTER))
    private void syncmatica_injectDedicatedCommands(CommandManager.RegistrationEnvironment environment,
                                                    CommandRegistryAccess registryAccess, CallbackInfo ci)
    {
        SyncmaticaCommand.INSTANCE.register(this.dispatcher, registryAccess, environment);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/server/command/PublishCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V",
                                        shift = At.Shift.AFTER))
    private void syncmatica_injectIntegratedCommands(CommandManager.RegistrationEnvironment environment,
                                                    CommandRegistryAccess registryAccess, CallbackInfo ci)
    {
        SyncmaticaCommand.INSTANCE.register(this.dispatcher, registryAccess, environment);
    }
}
