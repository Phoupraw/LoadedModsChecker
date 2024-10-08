package phoupraw.mcmod.voxelcake.constant;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.voxel.BakingBlockVoxel;
import phoupraw.mcmod.voxelcake.voxel.BlockVoxel;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

@ApiStatus.NonExtendable
public interface VCVoxelTypes {
    Codec<BlockVoxel> DIRECT_BLOCK = r(VCIDs.of("direct_block"), BlockVoxel.CODEC);
    Codec<BakingBlockVoxel> BAKING_BLOCK = r(VCIDs.of("baking_block"), BakingBlockVoxel.CODEC);
    private static<T extends Voxel> @Nullable Codec<T> r(Identifier id, Codec<T> value) {
        return Registry.register(VCRegistries.VOXEL_TYPE, id, value);
    }
}
