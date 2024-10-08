package phoupraw.mcmod.voxelcake.voxel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.constant.VCIDs;
import phoupraw.mcmod.voxelcake.misc.MCUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class BakingBlockVoxel implements Voxel {
    public static final Codec<BakingBlockVoxel> CODEC = RecordCodecBuilder.build(RecordCodecBuilder.<BakingBlockVoxel>instance().group(
      BlockPredicate.CODEC.fieldOf("predicate").forGetter(BakingBlockVoxel::predicate),
      FoodComponent.CODEC.optionalFieldOf("foodComponent").forGetter(Voxel::getOptionalFoodComponent)
    ).apply(RecordCodecBuilder.instance(), BakingBlockVoxel::of)).codec();
    public static final Collection<Identifier> MODEL_IDS = new ObjectArrayList<>();
    static {
        ModelLoadingPlugin.register(BakingBlockVoxel::onInitializeModelLoader);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new Reloader());
    }
    public static BakingBlockVoxel of(BlockPredicate predicate, Optional<FoodComponent> foodComponent) {
        return new BakingBlockVoxel(predicate, foodComponent.orElse(null));
    }
    public static Identifier toModelId(Identifier id) {
        return id.withPrefixedPath(Voxel.PATH_PATH);
    }
    private static void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
        //ModelLoader.MODELS_FINDER.findAllResources(pluginContext)
        pluginContext.addModels(MODEL_IDS);
        //pluginContext.resolveModel().register(BakingBlockVoxel::resolveModel);
    }
    private final BlockPredicate predicate;
    private final @Nullable FoodComponent foodComponent;
    private Identifier modelId;
    public BakingBlockVoxel(BlockPredicate predicate, @Nullable FoodComponent foodComponent) {
        this.predicate = predicate;
        this.foodComponent = foodComponent;
    }
    //private static @Nullable UnbakedModel resolveModel(ModelResolver.Context context) {
    //    //if (context.id().getPath().startsWith(PATH_PATH)) {
    //    //    return context.getOrLoadModel(context.id());
    //    //}
    //    return null;
    //}
    public BakingBlockVoxel(Block block, @Nullable FoodComponent foodComponent) {
        this(new BlockPredicate(Optional.of(RegistryEntryList.of(Registries.BLOCK.getEntry(block))), Optional.empty(), Optional.empty()), foodComponent);
    }
    @Override
    public @NotNull Sprite getSprite(RegistryEntry<Voxel> voxelEntry, @NotNull Direction face, Supplier<Random> randomSupplier) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (modelId == null) {
            modelId = toModelId(voxelEntry.getKey().orElseThrow().getValue());
        }
        BakedModel model = client.getBakedModelManager().getModel(modelId);
        if (model==null) return MCUtils.getMissingSprite();
        List<BakedQuad> quads = model.getQuads(null, face, randomSupplier.get());
        return quads.isEmpty() ? MCUtils.getMissingSprite() : quads.get(randomSupplier.get().nextInt(quads.size())).getSprite();
    }
    @Override
    public @Nullable FoodComponent getFoodComponent() {
        return foodComponent;
    }
    @Override
    public @NotNull Codec<BakingBlockVoxel> getType() {
        return CODEC;
    }
    //@Override
    //public int hashCode() {
    //    return Objects.hash(predicate, foodComponent);
    //}
    //@Override
    //public boolean equals(Object obj) {
    //    if (obj == this) return true;
    //    if (obj == null || obj.getClass() != this.getClass()) return false;
    //    var that = (BakingBlockVoxel) obj;
    //    return Objects.equals(this.predicate, that.predicate) &&
    //      Objects.equals(this.foodComponent, that.foodComponent);
    //}
    @Override
    public String toString() {
        return "BakingBlockVoxel[" +
          "predicate=" + predicate + ", " +
          "foodComponent=" + foodComponent + ']';
    }
    public BlockPredicate predicate() {
        return predicate;
    }
    //public Identifier getModelId() {
    //    if (modelId == null) {
    //        Identifier id = MinecraftClient.getInstance().world.getRegistryManager().get(VCRegistryKeys.VOXEL).getId(this);
    //        modelId = toModelId(id);
    //    }
    //    return modelId;
    //}

    private static class Reloader implements SimpleResourceReloadListener<Void> {
        @Override
        public Identifier getFabricId() {
            return VCIDs.VOXEL;
        }
        @Override
        public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.supplyAsync(()->{
                Collection<Identifier> modelIds = new ObjectArrayList<>();
                for (Identifier resourceId : ModelLoader.MODELS_FINDER.findResources(manager).keySet()) {
                    if (resourceId.getPath().startsWith("models/" + Voxel.PATH_PATH)){
                        String path = resourceId.getPath().replaceAll("^models/(.*)\\.json$", "$1");
                        modelIds.add(Identifier.of(resourceId.getNamespace(),path));
                    }
                }
                MODEL_IDS.clear();
                MODEL_IDS.addAll(modelIds);
                return null;
            },executor);
        }
        @Override
        public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
