package phoupraw.mcmod.voxelcake.constant;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

@ApiStatus.NonExtendable
public interface VCRegistries {
    Registry<Codec<? extends Voxel>> VOXEL_TYPE = FabricRegistryBuilder.createSimple(VCRegistryKeys.VOXEL_TYPE).buildAndRegister();
}
