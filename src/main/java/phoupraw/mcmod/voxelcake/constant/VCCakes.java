package phoupraw.mcmod.voxelcake.constant;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.misc.BlockCake;

import java.util.Map;

@ApiStatus.NonExtendable
public interface VCCakes {
    RegistryKey<BlockCake> TEST_HONEY = of("test/honey");
    @ApiStatus.Internal
    static void bootstrap(Registerable<BlockCake> registerable) {
        registerable.register(TEST_HONEY,  new BlockCake(new BlockPos(1, 1, 1), Map.of(
          BlockPos.ORIGIN, registerable.getRegistryLookup(VCRegistryKeys.VOXEL).getOrThrow(VCVoxels.HONEY_VOXEL)
        )));
    }
    private static RegistryKey<BlockCake> of(String path) {
        return RegistryKey.of(VCRegistryKeys.CAKE, VCIDs.of(path));
    }
}
