package phoupraw.mcmod.voxelcake.voxel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.constant.VCRegistries;
import phoupraw.mcmod.voxelcake.constant.VCRegistryKeys;

import java.util.Optional;
import java.util.function.Supplier;

public interface Voxel {
    String PATH = "voxel";
    String PATH_PATH = VoxelCake.ID + "/" + PATH+"/";
    Codec<Voxel> CODEC = VCRegistries.VOXEL_TYPE.getCodec().dispatch(Voxel::getType, MapCodec::assumeMapUnsafe);
    Codec<RegistryEntry<Voxel>> ENTRY_CODEC = RegistryFixedCodec.of(VCRegistryKeys.VOXEL);
    static Optional<FoodComponent> getOptionalFoodComponent(Voxel self) {
        return Optional.ofNullable(self.getFoodComponent());
    }
    @Environment(EnvType.CLIENT)
    @NotNull Sprite getSprite(RegistryEntry<Voxel> voxelEntry, @NotNull Direction face, Supplier<Random> randomSupplier);
    @Nullable FoodComponent getFoodComponent();
    @NotNull Codec<? extends Voxel> getType();
}
