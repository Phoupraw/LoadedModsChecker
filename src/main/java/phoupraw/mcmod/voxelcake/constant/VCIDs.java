package phoupraw.mcmod.voxelcake.constant;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.misc.Cake;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

@ApiStatus.NonExtendable
public interface VCIDs {
    Identifier VOXEL = of(Voxel.PATH);
    Identifier CAKE = of(Cake.PATH);
    Identifier VOXEL_CAKE = of("voxel_cake");
    Identifier CREAM = of("cream");
    Identifier BUCKETED_CREAM = bucketed(CREAM);
    Identifier SWEET_BERRY_JAM = of("sweet_berry_jam");
    Identifier BUCKETED_SWEET_BERRY_JAM = bucketed(SWEET_BERRY_JAM);
    Identifier VOXEL_TYPE = of("voxel_type");
    Identifier KELP_ASH = of("kelp_ash");
    Identifier FILLED_CAULDRON =of("filled_cauldron");
    Identifier MANUAL_BASIN =of("manual_basin");
    static Identifier of(String path) {
        return Identifier.of(VoxelCake.ID, path);
    }
    private static Identifier bucketed(Identifier jam) {
        return jam.withPrefixedPath("bucketed_");
    }
}
