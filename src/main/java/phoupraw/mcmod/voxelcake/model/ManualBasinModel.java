package phoupraw.mcmod.voxelcake.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import phoupraw.mcmod.voxelcake.constant.VCIDs;

import java.util.function.Supplier;

public class ManualBasinModel extends ForwardingBakedModel {
    public static final ModelIdentifier MODEL_ID = new ModelIdentifier(VCIDs.MANUAL_BASIN, "");
    @ApiStatus.Internal
    public static void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.modifyModelAfterBake().register(ManualBasinModel::modifyModelAfterBake);
    }
    private static @Nullable BakedModel modifyModelAfterBake(@Nullable BakedModel model, ModelModifier.AfterBake.Context context) {
        ModelIdentifier topLevelId = context.topLevelId();
        //if (topLevelId!=null && topLevelId.id().getPath().contains("basin")) {
        //    System.out.println(topLevelId);
        //}
        if (model != null && MODEL_ID.equals(topLevelId)) {
            return (new ManualBasinModel(model));
        }
        return model;
    }
    private static boolean transform(MutableQuadView quad) {
        Vector3f p = new Vector3f();
        for (int j = 0; j < 4; j++) {
            quad
              .pos(j, quad.copyPos(j, p)
                .add(0.5f, 0.2f, 0.5f)
              )
              .cullFace(null);
        }
        return true;
    }
    public ManualBasinModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }
    @Override
    public boolean isVanillaAdapter() {
        return false;
    }
    @Override
    public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        super.emitBlockQuads(world, state, pos, randomSupplier, context);
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        if (!(world.getBlockEntityRenderData(pos) instanceof Inventory blockInv)) {
            return;
        }
        context.pushTransform(ManualBasinModel::transform);
        for (int i = 0; i < blockInv.size(); i++) {
            ItemStack stack = blockInv.getStack(i);
            if (stack.isEmpty()) continue;
            BakedModel model = itemRenderer.getModel(stack, world instanceof World w ? w : null, null, 42);
            Transformation transformation = model.getTransformation().getTransformation(ModelTransformationMode.GROUND);
            Quaternionf rotation = new Quaternionf().rotationXYZ(
              transformation.rotation.x() * MathHelper.RADIANS_PER_DEGREE,
              transformation.rotation.y() * MathHelper.RADIANS_PER_DEGREE,
              transformation.rotation.z() * MathHelper.RADIANS_PER_DEGREE
            );
            context.pushTransform(quad -> {
                Vector3f p = new Vector3f();
                for (int j = 0; j < 4; j++) {
                    quad.pos(j, quad.copyPos(j, p)
                      .add(transformation.translation)
                      .rotate(rotation)
                      .mul(transformation.scale)
                    );
                }
                return true;
            });
            model.emitBlockQuads(world, state, pos, randomSupplier, context);
            context.popTransform();
            //itemRenderer.renderItem(
            //  blockInv.getStack(i),
            //  ModelTransformationMode.GROUND,
            //  LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK,pos),world.getLightLevel(LightType.SKY,pos)),
            //  OverlayTexture.DEFAULT_UV,
            //
            //);
        }
        context.popTransform();
    }
}
