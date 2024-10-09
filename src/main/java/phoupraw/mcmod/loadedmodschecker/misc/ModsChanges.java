package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.loader.api.Version;

import java.util.Collection;
import java.util.Map;

public record ModsChanges(Collection<String> newMods, Map<String,Version> deletedMods, Map<String, Version> updatedMods,Map<String,Version> rollbackedMods) {
}
