package ch.endte.syncmatica.mixin;

import java.util.Optional;
import ch.endte.syncmatica.Reference;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.ScreenHelper;
import ch.endte.syncmatica.network.actor.ActorClientPlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient
{
    @Shadow private boolean isLocalServer;

    @Inject(method = "doWorldLoad", at = @At("TAIL"))
    private void syncmatica$startIntegratedServer(LevelStorageSource.LevelStorageAccess levelSourceAccess, PackRepository packRepository, WorldStem worldStem, Optional<GameRules> gameRules, boolean newWorld, CallbackInfo ci)
    {
        if (this.isLocalServer)
        {
            Reference.setIntegratedServer(true);
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    private void syncmatica$shutdownPre(final CallbackInfo ci)
    {
        ActorClientPlayHandler.getInstance().reset();
        Reference.setIntegratedServer(false);
        ScreenHelper.close();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("RETURN"))
    private void syncmatica$shutdownPost(final CallbackInfo ci)
    {
        // This fixes some timing issues between this
        // and when LM unloads/loads placements.
        Syncmatica.shutdown();
        LitematicManager.clear();
    }
}
