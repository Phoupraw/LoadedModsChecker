package phoupraw.mcmod.voxelcake.constant;

import net.minecraft.block.Blocks;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.voxel.BakingBlockVoxel;
import phoupraw.mcmod.voxelcake.voxel.BlockVoxel;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

@ApiStatus.NonExtendable
public interface VCVoxels {
    //
    RegistryKey<Voxel> HONEY_VOXEL = of("honey_block");
    RegistryKey<Voxel> PUMPKIN = of("pumpkin");
    RegistryKey<Voxel> MELON = of("melon");
    RegistryKey<Voxel> NETHER_WART_BLOCK = of("nether_wart_block");
    RegistryKey<Voxel> WARPED_WART_BLOCK = of("warped_wart_block");
    RegistryKey<Voxel> HAY_BLOCK = of("hay_block");
    RegistryKey<Voxel> ICE = of("ice");
    RegistryKey<Voxel> MOSS = of("moss");
    RegistryKey<Voxel> CREAM = of(VCIDs.CREAM);
    RegistryKey<Voxel> SWEET_BERRY_JAM = of(VCIDs.SWEET_BERRY_JAM);
    //
    RegistryKey<Voxel> OAK_PLANKS = of("oak_planks");
    @ApiStatus.Internal
    static void bootstrap(Registerable<Voxel> registerable) {
        registerable.register(HONEY_VOXEL, new BlockVoxel(Blocks.HONEY_BLOCK, foodOf(24,4.8)));
        registerable.register(PUMPKIN, new BlockVoxel(Blocks.PUMPKIN, foodOf(14,8)));
        registerable.register(MELON, new BlockVoxel(Blocks.MELON, foodOf(18,9.6)));
        registerable.register(NETHER_WART_BLOCK,new BlockVoxel(Blocks.NETHER_WART_BLOCK,foodOf(3,2)));
        registerable.register(WARPED_WART_BLOCK,new BlockVoxel(Blocks.WARPED_WART_BLOCK, foodOf(4,3)));
        registerable.register(HAY_BLOCK,new BakingBlockVoxel(Blocks.HAY_BLOCK, foodOf(12,15)));
        registerable.register(ICE, new BlockVoxel(Blocks.ICE,foodOf(0,0)));
        registerable.register(MOSS, new BlockVoxel(Blocks.MOSS_BLOCK,foodOf(1,0)));
        registerable.register(CREAM,new BlockVoxel(VCBlocks.CREAM,foodOf(10,15)));
        registerable.register(SWEET_BERRY_JAM,new BlockVoxel(VCBlocks.SWEET_BERRY_JAM,foodOf(9,7)));
        //
        registerable.register(OAK_PLANKS, new BlockVoxel(Blocks.OAK_PLANKS,null));
    }
    private static RegistryKey<Voxel> of(String path) {
        return of(VCIDs.of(path));
    }
    private static RegistryKey<Voxel> of(Identifier id) {
        return RegistryKey.of(VCRegistryKeys.VOXEL, id);
    }
    static FoodComponent foodOf(int nutrition, double saturation) {
        return new FoodComponent.Builder()
          .nutrition((nutrition))
          .saturationModifier(saturation==0? 0: (float) (saturation/nutrition/2))
          .build();
    }
}
