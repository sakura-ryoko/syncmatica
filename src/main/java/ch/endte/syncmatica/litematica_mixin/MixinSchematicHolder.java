package ch.endte.syncmatica.litematica_mixin;

//@Mixin(SchematicHolder.class)
public abstract class MixinSchematicHolder {

	public MixinSchematicHolder() {
	}

//	@Inject(method = "removeSchematic", at = @At("RETURN"), remap = false)
//	public void unloadSyncmatic(final LitematicaSchematic schematic, final CallbackInfoReturnable<Boolean> ci)
//	{
//		LitematicManager.getInstance().unrenderSchematic(schematic);
//	}
}
