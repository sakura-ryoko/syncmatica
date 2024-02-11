package ch.endte.syncmatica.litematica;

import java.util.UUID;

public interface IIDContainer {
	void syncmatica$setServerId(UUID i);

	UUID syncmatica$getServerId();
}
