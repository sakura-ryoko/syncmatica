package ch.endte.syncmatica.litematica;

import java.util.HashMap;
import java.util.UUID;
import ch.endte.syncmatica.Syncmatica;
import ch.endte.syncmatica.data.ServerPlacement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.json.JsonUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.litematica.interfaces.ISchematicPlacementEventListener;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;

@Environment(EnvType.CLIENT)
public class PlacementEventHandler implements ISchematicPlacementEventListener
{
    private final HashMap<UUID, UUID> serverIds = new HashMap<>();

    protected void setServerId(SchematicPlacement placement, UUID id)
    {
        if (placement != null)
        {
            Syncmatica.debug("PlacementEventHandler#setServerId() - name: [{}], ID: [{}]", placement.getName(), id.toString());
            this.serverIds.put(placement.getHashId(), id);
        }
    }

    protected @Nullable UUID getServerId(SchematicPlacement placement)
    {
        if (placement != null && this.serverIds.containsKey(placement.getHashId()))
        {
            Syncmatica.debug("PlacementEventHandler#getServerId():A: - name: [{}], ID: [{}]", placement.getName(), this.serverIds.get(placement.getHashId()).toString());
            return this.serverIds.get(placement.getHashId());
        }
        else
        {
            Syncmatica.LOGGER.warn("PlacementEventHandler#getServerId() - name: [{}] --> NOT FOUND!", placement != null ? placement.getName() : "<NULL>");
        }

        return null;
    }

    protected boolean hasServerId(SchematicPlacement placement)
    {
        return this.serverIds.containsKey(placement.getHashId());
    }

    protected void clearServerId(SchematicPlacement placement)
    {
        if (placement != null)
        {
            Syncmatica.debug("PlacementEventHandler#clearServerId() - name: [{}]", placement.getName());
            this.serverIds.remove(placement.getHashId());
        }
    }

    protected void clear()
    {
        Syncmatica.debug("PlacementEventHandler#clear()");
        this.serverIds.clear();
    }

    @Override
    public void onPlacementInit(SchematicPlacement placement)
    {
//        if (placement != null)
//        {
//            this.clearServerId(placement);
//        }
    }

    @Override
    public void onPlacementCreateFromJson(SchematicPlacement placement, LitematicaSchematic litematicaSchematic, BlockPos blockPos, String s, Rotation blockRotation, Mirror blockMirror, boolean enabled, boolean renderEnabled, JsonObject obj)
    {
        if (JsonUtils.hasString(obj, "syncmatica_uuid") && placement != null)
        {
            String id = obj.get("syncmatica_uuid").getAsString();
            Syncmatica.debug("PlacementEventHandler#onPlacementCreateFromJson(): name: [{}], id: [{}]", placement.getName(), id);
            this.setServerId(placement, UUID.fromString(id));
            LitematicManager.getInstance().preLoad(placement);
        }
    }

    @Override
    public void onPlacementCreateFromData(SchematicPlacement placement, LitematicaSchematic litematicaSchematic, BlockPos blockPos, String s, Rotation blockRotation, Mirror blockMirror, boolean enabled, boolean renderEnabled, CompoundData data)
    {
        if (data.contains("syncmatica_uuid", Constants.NBT.TAG_STRING) && placement != null)
        {
            String id = data.getStringOrDefault("syncmatica_uuid", "");
            Syncmatica.debug("PlacementEventHandler#onPlacementCreateFromData(): name: [{}], id: [{}]", placement.getName(), id);
            this.setServerId(placement, UUID.fromString(id));
            LitematicManager.getInstance().preLoad(placement);
        }
    }

    @Override
    public void onSavePlacementToJson(SchematicPlacement placement, JsonObject obj)
    {
        UUID serverId = this.getServerId(placement);

        if (serverId != null)
        {
            Syncmatica.debug("PlacementEventHandler#onSavePlacementToJson(): name: [{}], id: [{}]", placement.getName(), serverId.toString());
            obj.add("syncmatica_uuid", new JsonPrimitive(serverId.toString()));
        }
        else
        {
            Syncmatica.LOGGER.warn("PlacementEventHandler#onSavePlacementToJson(): name: [{}] --> NOT FOUND!", placement.getName());
        }
    }

    @Override
    public void onSavePlacementToData(SchematicPlacement placement, CompoundData data)
    {
        UUID serverId = this.getServerId(placement);

        if (serverId != null)
        {
            Syncmatica.debug("PlacementEventHandler#onSavePlacementToData(): name: [{}], id: [{}]", placement.getName(), serverId.toString());
            data.putString("syncmatica_uuid", serverId.toString());
        }
        else
        {
            Syncmatica.LOGGER.warn("PlacementEventHandler#onSavePlacementToData(): name: [{}] --> NOT FOUND!", placement.getName());
        }
    }

    @Override
    public void onPlacementAdded(SchematicPlacement placement)
    {
        // This is just a failsafe mechanism in case there is some kinda de-sync
        if (!this.hasServerId(placement) &&
            LitematicManager.getInstance().isSyncmatic(placement))
        {
            ServerPlacement serverPlacement = LitematicManager.getInstance().getRenderingSyncmatic(placement);

            if (serverPlacement != null)
            {
                this.setServerId(placement, serverPlacement.getId());
                LitematicManager.getInstance().preLoad(placement);
            }
        }
    }

    @Override
    public void onPlacementRemoved(SchematicPlacement placement)
    {
        LitematicManager.getInstance().unrenderSchematic(placement.getSchematic());
    }
}
