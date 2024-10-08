package phoupraw.mcmod.voxelcake.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.voxelcake.constant.VCRegistryKeys;

import java.util.concurrent.CompletableFuture;

final class DynamicGen extends FabricDynamicRegistryProvider {
     DynamicGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    
    @Override
    public String getName() {
        return "Dynamic Registry";
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getWrapperOrThrow(VCRegistryKeys.VOXEL));
        entries.addAll(registries.getWrapperOrThrow(VCRegistryKeys.CAKE));
    }
}
