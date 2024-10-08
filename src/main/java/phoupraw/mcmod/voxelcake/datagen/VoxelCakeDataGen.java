package phoupraw.mcmod.voxelcake.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.constant.VCCakes;
import phoupraw.mcmod.voxelcake.constant.VCRegistryKeys;
import phoupraw.mcmod.voxelcake.constant.VCVoxels;

public final class VoxelCakeDataGen implements DataGeneratorEntrypoint {
     static void print(RegistryWrapper.WrapperLookup lookup, RegistryKey<? extends Registry<?>> registryKey) {
         VoxelCake.LOGGER.info("print "+registryKey.getValue()+" :");
         for(var iter = lookup.getWrapperOrThrow(registryKey).streamEntries().iterator();iter.hasNext();){
             var regEntry = iter.next();
             VoxelCake.LOGGER.info(regEntry);
         }
    }
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator g) {
        var pack = g.createPack();
        //server
        pack.addProvider(BlockLootGen::new);
        pack.addProvider(RecipeGen::new);
        pack.addProvider(BlockTagGen::new);
        pack.addProvider(ItemTagGen::new);
        //pack.addProvider(EnchTagGen::new);
        //pack.addProvider(EnchGen::new);
        pack.addProvider(DynamicGen::new);
        //pack.addProvider(CakeGen::new);
        //client
        pack.addProvider(ChineseGen::new);
        pack.addProvider(EnglishGen::new);
        pack.addProvider(ModelGen::new);
        
        ////override
        //var fishing = g.createBuiltinResourcePack(TIDs.LEVELED_FISHING_TREASURE);
        ////server
        //fishing.addProvider(FishingLootGen2::new);
    }
    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(VCRegistryKeys.VOXEL, VCVoxels::bootstrap);
        registryBuilder.addRegistry(VCRegistryKeys.CAKE, VCCakes::bootstrap);
    }
}
