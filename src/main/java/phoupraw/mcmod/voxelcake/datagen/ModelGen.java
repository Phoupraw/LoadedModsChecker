package phoupraw.mcmod.voxelcake.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;
import phoupraw.mcmod.voxelcake.constant.VCIDs;
import phoupraw.mcmod.voxelcake.constant.VCItems;
import phoupraw.mcmod.voxelcake.constant.VCVoxels;
import phoupraw.mcmod.voxelcake.voxel.BakingBlockVoxel;

import java.util.Arrays;
import java.util.Optional;

final class ModelGen extends FabricModelProvider {
    private static final Model CAULDRON_FULL = new Model(Optional.of(VCIDs.of("block/cauldron/full")), Optional.empty(), TextureKey.CONTENT);
    ModelGen(FabricDataOutput output) {
        super(output);
    }
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator g) {
        g.registerNorthDefaultHorizontalRotation(VCBlocks.VOXEL_CAKE);
        for (var block : Arrays.asList(VCBlocks.CREAM, VCBlocks.SWEET_BERRY_JAM)) {
            TexturedModel.CUBE_ALL.upload(block, g.modelCollector);
            g.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(block, ModelIds.getBlockModelId(block)));
            g.excludeFromSimpleItemModelGeneration(block);
            g.registerItemModel(block.asItem());
        }
        g.excludeFromSimpleItemModelGeneration(VCBlocks.VOXEL_CAKE);
        Models.CUBE_ALL.upload(BakingBlockVoxel.toModelId(VCVoxels.HAY_BLOCK.getValue()), TextureMap.all(Identifier.ofVanilla("block/hay_block_top")), g.modelCollector);
        g.registerItemModel(VCItems.KELP_ASH);
        g.blockStateCollector.accept(MultipartBlockStateSupplier.create(VCBlocks.FILLED_CAULDRON)
          .with(BlockStateVariant.create()
            .put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.CAULDRON)))
          .with(BlockStateVariant.create()
            .put(VariantSettings.MODEL, CAULDRON_FULL.upload(VCBlocks.FILLED_CAULDRON, TextureMap.of(TextureKey.CONTENT, TextureMap.getId(VCBlocks.SWEET_BERRY_JAM)), g.modelCollector))));
        g.registerSimpleState(VCBlocks.MANUAL_BASIN);
    }
    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    
    }
}
