package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.litematica.IIDContainer;
import ch.endte.syncmatica.litematica.LitematicManager;
import ch.endte.syncmatica.litematica.MovingFinisher;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(SchematicPlacement.class)
public abstract class MixinSchematicPlacement implements IIDContainer, MovingFinisher {

    // unsure if I can just make an assignment here so I mixin to the constructor
    @Unique
    UUID serverId;

    private MixinSchematicPlacement() {
    }

    @Inject(method = "toJson", at = @At("RETURN"), remap = false)
    public void saveUuid(final CallbackInfoReturnable<JsonObject> cir) {
        final JsonObject saveData = cir.getReturnValue();
        if (saveData != null) {
            if (serverId != null) {
                saveData.add("syncmatica_uuid", new JsonPrimitive(serverId.toString()));
            }
        }
    }

    @Inject(method = "fromJson", at = @At("RETURN"), remap = false, cancellable = true)
    private static void loadSyncmatic(final JsonObject obj, final CallbackInfoReturnable<SchematicPlacement> cir) {
        if (JsonUtils.hasString(obj, "syncmatica_uuid")) {
            final SchematicPlacement newInstance = cir.getReturnValue();
            if (newInstance != null) {
                ((IIDContainer) newInstance).syncmatica$setServerId(UUID.fromString(obj.get("syncmatica_uuid").getAsString()));
                cir.setReturnValue(null);
                LitematicManager.getInstance().preLoad(newInstance);
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void setNull(final LitematicaSchematic schematic, final BlockPos origin, final String name, final boolean enabled, final boolean enableRender, final CallbackInfo ci) {
        serverId = null;
    }

    @Override
    public void syncmatica$setServerId(final UUID i) {
        serverId = i;
    }

    @Override
    public UUID syncmatica$getServerId() {
        return serverId;
    }

    @Override
    @Invoker(value = "onModified", remap = false)
    public abstract void onFinishedMoving(String subRegionName, SchematicPlacementManager manager);
}
