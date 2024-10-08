package phoupraw.mcmod.voxelcake.model;

import com.google.common.collect.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;
import phoupraw.mcmod.voxelcake.constant.VCComponentTypes;
import phoupraw.mcmod.voxelcake.constant.VCItems;
import phoupraw.mcmod.voxelcake.misc.BlockCake;
import phoupraw.mcmod.voxelcake.misc.FabricUtils;
import phoupraw.mcmod.voxelcake.misc.MCUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
//FIXME 自己和其他方块的剔除面都时不时乱一下
@Environment(EnvType.CLIENT)
public class VoxelCakeModel implements CustomBlockModel {
    public static final VoxelCakeModel INSTANCE = new VoxelCakeModel();
    public static final UnbakedBakedModel UNBAKED = new UnbakedBakedModel(INSTANCE);
    public static final Identifier BLOCK_ID = ModelIds.getBlockModelId(VCBlocks.VOXEL_CAKE);
    public static final Identifier ITEM_ID = ModelIds.getItemModelId(VCItems.VOXEL_CAKE);
    static {
        ModelLoadingPlugin.register(VoxelCakeModel::onInitializeModelLoader);
    }
    public static @NotNull BakedModel toModel(BlockCake self, Supplier<Random> randomSupplier) {
        Table<BlockPos, Direction, Sprite> table = (HashBasedTable.create());
        SetMultimap<Block, Direction> missing = MultimapBuilder.hashKeys().hashSetValues().build();
       var voxelMap = self.getVoxelMap();
        for (var entry : voxelMap.entrySet()) {
            BlockPos pos = entry.getKey();
           var voxelEntry = entry.getValue();
            for (@NotNull Direction face : MCUtils.DIRECTIONS) {
                BlockPos neighborPos = pos.offset(face);
                if (!voxelMap.containsKey(neighborPos)) {
                    Sprite sprite = voxelEntry.value().getSprite(voxelEntry,face, randomSupplier);
                    table.put(pos, face, sprite);
                }
            }
        }
        if (!missing.isEmpty()) {
            VoxelCake.LOGGER.warn("missing = " + missing);
        }
        ListMultimap<@Nullable Direction, BakedQuad> cullFace2quads = MultimapBuilder.hashKeys().arrayListValues().build();
        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();
        BlockPos size = self.getSize();
        var scale = new Vec3d(1.0 / size.getX(), 1.0 / size.getY(), 1.0 / size.getZ());
        //int quadsCount = 0;
        for (var rowEntry : table.rowMap().entrySet()) {
            var pos = rowEntry.getKey();
            var box = new Box(Vec3d.of(pos).multiply(scale), Vec3d.of(pos).add(1, 1, 1).multiply(scale));
            for (Map.Entry<Direction, Sprite> entry : rowEntry.getValue().entrySet()) {
                Sprite sprite = entry.getValue();
                var quad0 = FabricUtils.square(emitter, box, entry.getKey())
                  .color(-1, -1, -1, -1)
                  .spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
                BakedQuad quad = quad0.toBakedQuad(sprite);
                cullFace2quads.put(quad0.cullFace(), quad);
                //quadsCount++;
            }
        }
        return new SimpleBlockModel(Multimaps.asMap(cullFace2quads), MinecraftClient.getInstance().getBlockRenderManager().getModel(Blocks.CAKE.getDefaultState()).getParticleSprite());
    }
    private static @Nullable UnbakedModel resolveModel(ModelResolver.Context context) {
        if (BLOCK_ID.equals(context.id()) || ITEM_ID.equals(context.id())) {
            return UNBAKED;
        }
        return null;
    }
    private static void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.resolveModel().register(VoxelCakeModel::resolveModel);
    }
    
    @Override
    public Sprite getParticleSprite() {
        return MinecraftClient.getInstance().getBlockRenderManager().getModel(Blocks.CAKE.getDefaultState()).getParticleSprite();
    }
    public static final Map<Direction, RenderContext.QuadTransform> TRANSFORMS = new EnumMap<>(Direction.class);
    static {
        for (Direction facing : Direction.Type.HORIZONTAL) {
            var rotation = ModelRotation.get(0, (facing.getHorizontal() - Direction.NORTH.getHorizontal()) * 90).getRotation();
            TRANSFORMS.put(facing,quad -> {
                FabricUtils.rotate(quad,rotation);
                return true;
            });
        }
    }
    @Override
    public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        if (!(world.getBlockEntityRenderData(pos) instanceof BlockCake cake && !cake.isEmpty())) {
            return;
        }
        Direction facing = state.get(DoorBlock.FACING);
        context.pushTransform(TRANSFORMS.get(facing));
        cake.getModel(randomSupplier).emitBlockQuads(world, state, pos, randomSupplier, context);
        context.popTransform();
    }
    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        RegistryEntry<BlockCake> cakeEntry = stack.get(VCComponentTypes.CAKE);
        if (cakeEntry != null) {
            cakeEntry.value().getModel(randomSupplier).emitItemQuads(stack, randomSupplier, context);
        }
    }
}
