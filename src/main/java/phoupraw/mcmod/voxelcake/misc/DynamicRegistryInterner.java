package phoupraw.mcmod.voxelcake.misc;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

public class DynamicRegistryInterner<T> {
    private final RegistryKey<? extends Registry<T>> registryKey;
    private final Map<T, RegistryEntry<T>> cache = new WeakHashMap<>();
    public DynamicRegistryInterner(RegistryKey<? extends Registry<T>> registryKey) {
        this.registryKey = registryKey;
        DynamicRegistrySetupCallback.EVENT.register(this::onRegistrySetup);
    }
    public @NotNull RegistryEntry<T> intern(@NotNull RegistryEntry<T> self) {
        RegistryEntry<T> cakeEntry = cache.get(self.value());
        if (cakeEntry != null) {
            return cakeEntry;
        }
        cache.put(self.value(), self);
        return self;
    }
    public @NotNull RegistryEntry<T> intern(@NotNull T self) {
        RegistryEntry<T> cakeEntry = cache.get(self);
        if (cakeEntry != null) {
            return cakeEntry;
        }
        cakeEntry = RegistryEntry.of(self);
        cache.put(self, cakeEntry);
        return cakeEntry;
    }
    private void onRegistrySetup(DynamicRegistryView registryView) {
        var registry0 = registryView.getOptional(registryKey);
        if (registry0.isEmpty()) return;
        var registry = registry0.get();
        cache.clear();
        RegistryEntryAddedCallback.event(registry).register((rawId, id, object) -> {
            cache.put(object, registry.getEntry(rawId).orElseThrow());
        });
    }
}
