package phoupraw.mcmod.voxelcake.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;

import java.util.concurrent.CompletableFuture;

final class BlockTagGen extends FabricTagProvider.BlockTagProvider {
    BlockTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        //getOrCreateTagBuilder(CAKE).add(Blocks.CAKE);
        //getOrCreateTagBuilder(BlockTags.HOE_MINEABLE).add(VCBlocks.VOXEL_CAKE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(VCBlocks.FILLED_CAULDRON,VCBlocks.MANUAL_BASIN);
    }
}
