package phoupraw.mcmod.voxelcake.constant;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.VoxelCakeClient;
import phoupraw.mcmod.voxelcake.block.FilledCauldronBlock;
import phoupraw.mcmod.voxelcake.block.ManualBasinBlock;
import phoupraw.mcmod.voxelcake.block.PowderSnowLikeBlock;
import phoupraw.mcmod.voxelcake.block.VoxelCakeBlock;

/**
 <ul>
 <li><b>复制的时候记得改方块类！</b></li>
 <li>{@link VCItems}</li>
 <li>{@link VoxelCakeClient} {@link BlockRenderLayerMap}</li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.BlockLootGen}</li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.BlockTagGen}</li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.ModelGen}</li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.ChineseGen}</li>
 <li>{@link VCVoxels}</li>
 </ul>
 */
@ApiStatus.NonExtendable
public interface VCBlocks {
    Block VOXEL_CAKE = r(VCIDs.VOXEL_CAKE, new VoxelCakeBlock());
    Block FILLED_CAULDRON = r(VCIDs.FILLED_CAULDRON, new FilledCauldronBlock(Settings.copy(Blocks.WATER_CAULDRON)));
    Block MANUAL_BASIN = r(VCIDs.MANUAL_BASIN, new ManualBasinBlock());
    Block CREAM = r(VCIDs.CREAM, PowderSnowLikeBlock.of(Settings.create().mapColor(MapColor.WHITE)));
    Block SWEET_BERRY_JAM = r(VCIDs.SWEET_BERRY_JAM, PowderSnowLikeBlock.of(Settings.create().mapColor(MapColor.RED)));
    private static <T extends Block> T r(Identifier id, T block) {
        return Registry.register(Registries.BLOCK, id, block);
    }
}
