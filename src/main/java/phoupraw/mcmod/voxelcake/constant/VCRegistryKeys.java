package phoupraw.mcmod.voxelcake.constant;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.misc.BlockCake;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

@ApiStatus.NonExtendable
public interface VCRegistryKeys {
    RegistryKey<Registry<Voxel>> VOXEL = RegistryKey.ofRegistry(VCIDs.VOXEL);
    RegistryKey<Registry<BlockCake>> CAKE = RegistryKey.ofRegistry(VCIDs.CAKE);
    RegistryKey<Registry<Codec<? extends Voxel>>> VOXEL_TYPE = RegistryKey.ofRegistry(VCIDs.VOXEL_TYPE);
    //private static <T> RegistryKey<Registry<T>> of(Identifier id) {
    //    return RegistryKey.ofRegistry(id);
    //}
}
