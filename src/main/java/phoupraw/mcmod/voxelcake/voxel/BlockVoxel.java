package phoupraw.mcmod.voxelcake.voxel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.constant.VCItems;
import phoupraw.mcmod.voxelcake.misc.MCUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public record BlockVoxel(BlockState blockState, @Nullable FoodComponent foodComponent) implements Voxel {
    public static final Codec<BlockVoxel> CODEC = RecordCodecBuilder.build(RecordCodecBuilder.<BlockVoxel>instance()
      .group(
        BlockState.CODEC.fieldOf("blockState").forGetter(BlockVoxel::blockState),
        FoodComponent.CODEC.optionalFieldOf("foodComponent").forGetter(Voxel::getOptionalFoodComponent)
      )
      .apply(RecordCodecBuilder.instance(),/* BlockVoxel::new*/BlockVoxel::of)).codec();
    //public static final Map<BlockState, RegistryKey<BlockVoxel>> REGISTRY = new Object2ObjectArrayMap<>(/*Map.of(
    //  Blocks.HONEY_BLOCK, VCVoxels.HONEY_VOXEL,
    //  Blocks.NETHER_WART_BLOCK, VCVoxels.NETHER_WART_BLOCK,
    //  Blocks.WARPED_WART_BLOCK, VCVoxels.WARPED_WART_BLOCK
    //)*/);
    //public static final DynamicRegistryInterner<Voxel> INTERNER = new DynamicRegistryInterner<>(VCRegistryKeys.VOXEL);
    //public static final Codec<RegistryEntry<Voxel>> ENTRY_CODEC = RegistryFixedCodec.of(VCRegistryKeys.VOXEL);
    static {
        //DynamicRegistrySetupCallback.EVENT.register(BlockVoxel::onRegistrySetup);
    }
    //public static @Nullable RegistryKey<BlockVoxel> get(BlockState blockState, RegistryWrapper.WrapperLookup lookup) {
    //    return REGISTRY.get(blockState/*.getBlock()*/);
    //}
    public static BlockVoxel of(BlockState blockState, Optional<FoodComponent> foodComponent) {
        return new BlockVoxel(blockState, foodComponent.orElse(null));
    }
    //private static void onRegistrySetup(DynamicRegistryView registryView) {
    //    var registry0 = registryView.getOptional(VCRegistryKeys.VOXEL);
    //    if (registry0.isEmpty()) return;
    //    REGISTRY.clear();
    //    var registry = registry0.get();
    //    RegistryEntryAddedCallback.event(registry).register(BlockVoxel::onEntryAdded);
    //}
    //private static void onEntryAdded(int rawId, Identifier id, Voxel object) {
    //    REGISTRY.put(object.blockState(), RegistryKey.of(VCRegistryKeys.VOXEL, id));
    //}
    public BlockVoxel {
        if (foodComponent == null) {
            VCItems.INEADIBLE_BLOCKS.add(blockState.getBlock());
        } else {
            VCItems.EADIBLE_BLOCKS.add(blockState.getBlock());
        }
    }
    public BlockVoxel(Block block, @Nullable FoodComponent foodComponent) {
        this(block.getDefaultState(), foodComponent);
    }
    @Environment(EnvType.CLIENT)
    @Override
    public @NotNull Sprite getSprite(RegistryEntry<Voxel> voxelEntry, @NotNull Direction face, Supplier<Random> randomSupplier) {
        Random random = randomSupplier.get();
        List<BakedQuad> quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState()).getQuads(blockState(), face, random);
        return quads.isEmpty() ? MCUtils.getMissingSprite() : quads.get(random.nextInt(quads.size())).getSprite();
    }
    @Override
    public @Nullable FoodComponent getFoodComponent() {
        return foodComponent();
    }
    @Override
    public @NotNull Codec<BlockVoxel> getType() {
        return CODEC;
    }
}
