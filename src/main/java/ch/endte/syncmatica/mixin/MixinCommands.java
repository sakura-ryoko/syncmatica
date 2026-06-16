package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.command.SyncmaticaCommand;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class MixinCommands
{
    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/server/commands/WhitelistCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V",
                                        shift = At.Shift.AFTER))
    private void syncmatica_injectDedicatedCommands(Commands.CommandSelection commandSelection,
                                                    CommandBuildContext context, CallbackInfo ci)
    {
        SyncmaticaCommand.INSTANCE.register(this.dispatcher, context, commandSelection);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/server/commands/PublishCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V",
                                        shift = At.Shift.AFTER))
    private void syncmatica_injectIntegratedCommands(Commands.CommandSelection commandSelection,
                                                     CommandBuildContext context, CallbackInfo ci)
    {
        SyncmaticaCommand.INSTANCE.register(this.dispatcher, context, commandSelection);
    }
}
