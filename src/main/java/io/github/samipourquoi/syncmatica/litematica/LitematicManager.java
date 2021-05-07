package io.github.samipourquoi.syncmatica.litematica;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.SchematicHolder;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.RedirectFileStorage;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.SyncmaticManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

// responsible for loading and keeping track of rendered syncmatic placements
// responsible for keeping track redirected litematic files (e.g. if the syncmatic was
// shared from this client)

public class LitematicManager {
	
	private static LitematicManager instance = null;
	
	
	// links syncmatic to schematic if it is rendered on the client
	// specific client
	private final Map<ServerPlacement, SchematicPlacement> rendering;
	private Collection<SchematicPlacement> preLoadList = new ArrayList<>();
	private Context context;
	
	public static LitematicManager getInstance() {
		if (instance == null) {
			instance = new LitematicManager();
		}
		return instance;
	}
	
	public static void clear() {
		instance = null;
	}

	private LitematicManager() {
		rendering = new HashMap<>();
	}
	
	// sets the active context for the gui side of things
	public void setActiveContext(Context con) {
		if (con.isServer()) {
			throw new RuntimeException("Applied server context where client context was expected");
		}
		context = con;
		ScreenUpdater.getInstance().setActiveContext(con);
	}
	
	public Context getActiveContext() {
		return context;
	}
	
	// 1st case syncmatic placement is present and is now enabled from GUI
	// or another source
	public void renderSyncmatic(ServerPlacement placement) {
		if (rendering.containsKey(placement)) {
			return;
		}
		File file = context.getFileStorage().getLocalLitematic(placement);

		LitematicaSchematic schematic = (LitematicaSchematic) SchematicHolder.getInstance().getOrLoad(file);
		
		if (schematic == null) {
			throw new RuntimeException("Could not create schematic from file");
		}
		
		BlockPos origin = placement.getPosition();

		SchematicPlacement litematicaPlacement = SchematicPlacement.createFor(schematic, origin, file.getName(), true, true);
		rendering.put(placement, litematicaPlacement);
		((IIDContainer)litematicaPlacement).setServerId(placement.getId());
		if (litematicaPlacement.isLocked()) {
			litematicaPlacement.toggleLocked();
		}
		litematicaPlacement.setRotation(placement.getRotation(), null);
		litematicaPlacement.setMirror(placement.getMirror(), null);
		litematicaPlacement.toggleLocked();

		DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, true);
		context.getSyncmaticManager().updateServerPlacement(placement);
	}
	
	// 2nd case litematic placement is present but gets turned into ServerPlacement
	// removed side effects
	public ServerPlacement syncmaticFromSchematic(SchematicPlacement schem) {
		if (rendering.containsValue(schem)) {
			// TODO: use the new ID for faster retrieval
			for (ServerPlacement checkPlacement: rendering.keySet()) {
				if (rendering.get(checkPlacement) == schem) {
					return checkPlacement;
				}
			}
			// theoretically not a possible branch that will be taken
			
			return null;
		}
		File placementFile = schem.getSchematicFile();
		ServerPlacement placement = new ServerPlacement(UUID.randomUUID(), placementFile);
		// thanks miniHUD
		String dimension = MinecraftClient.getInstance().getCameraEntity().getEntityWorld().getRegistryKey().getValue().toString();
		placement.move(dimension, schem.getOrigin(), schem.getRotation(), schem.getMirror());
		return placement;
	}
	
	// 3rd case litematic placement is loaded from file at startup or because the syncmatic got created from
	// it on this client
	// and the server gives confirmation that the schematic exists
	public void renderSyncmatic(ServerPlacement placement, SchematicPlacement litematicaPlacement, boolean addToRendering) {
		if (rendering.containsKey(placement)) {
			return;
		}
		IIDContainer modPlacement = (IIDContainer)litematicaPlacement;
		if (modPlacement.getServerId() != null && !modPlacement.getServerId().equals(placement.getId())) {
			return;
		}
		rendering.put(placement, litematicaPlacement);
		modPlacement.setServerId(placement.getId());
		
		if (litematicaPlacement.isLocked()) {
			litematicaPlacement.toggleLocked();
		}
		litematicaPlacement.setOrigin(placement.getPosition(), null);
		litematicaPlacement.setRotation(placement.getRotation(), null);
		litematicaPlacement.setMirror(placement.getMirror(), null);
		litematicaPlacement.toggleLocked();
		context.getSyncmaticManager().updateServerPlacement(placement);
		if (addToRendering) {
			DataManager.getSchematicPlacementManager().addSchematicPlacement(litematicaPlacement, false);
		}
	}
	
	public void unrenderSyncmatic(ServerPlacement placement) {
		if (!isRendered(placement)) {
			return;
		}
		DataManager.getSchematicPlacementManager().removeSchematicPlacement(rendering.get(placement));
		rendering.remove(placement);
		context.getSyncmaticManager().updateServerPlacement(placement);
	}
	
	public boolean isRendered(ServerPlacement placement) {
		return rendering.containsKey(placement);
	}
	
	public boolean isSyncmatic(SchematicPlacement schem) {
		return rendering.containsValue(schem);
	}
	
	// gets called by code mixed into litematicas loading stage
	// its responsible for keeping the litematics that got loaded in such a way
	// until a time where the server has told the client which syncmatics actually are still loaded
	public void preLoad(SchematicPlacement schem) {
		if (context != null && context.isStarted()) {
			UUID id = ((IIDContainer)schem).getServerId();
			ServerPlacement p = context.getSyncmaticManager().getPlacement(id);
			if (isRendered(p)) {
				rendering.put(p, schem);
				DataManager.getSchematicPlacementManager().addSchematicPlacement(schem, false);
			}
		} else if (preLoadList != null) {
			preLoadList.add(schem);
		}
	}
	
	public void commitLoad() {
		SyncmaticManager man = context.getSyncmaticManager();
		for (SchematicPlacement schem: preLoadList) {
			UUID id = ((IIDContainer)schem).getServerId();
			ServerPlacement p = man.getPlacement(id);
			if (p != null) {
				if (context.getFileStorage().getLocalLitematic(p) != schem.getSchematicFile()) {
					((RedirectFileStorage)context.getFileStorage()).addRedirect(schem.getSchematicFile());
				}
				renderSyncmatic(p, schem, true);
			}
		}
		preLoadList = null;
	}
	
	public void unrenderSchematic(LitematicaSchematic l) {
		rendering.entrySet().removeIf(e ->{
			if (e.getValue().getSchematic() == l) {
				context.getSyncmaticManager().updateServerPlacement(e.getKey());
				return true;
			}
			return false;
		});
	}

	public void unrenderSchematicPlacement(SchematicPlacement placement) {
		UUID id = ((IIDContainer)placement).getServerId();
		ServerPlacement p = context.getSyncmaticManager().getPlacement(id);
		if (p != null) {
			unrenderSyncmatic(p);
		}
		
	}
}