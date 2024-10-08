package phoupraw.mcmod.voxelcake.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
@Environment(EnvType.CLIENT)
public interface HasDepthModel extends BakedModel {
    @Override
    default boolean useAmbientOcclusion() {
        return true;
    }
    @Override
    default boolean hasDepth() {
        return true;
    }
    @Override
    default boolean isSideLit() {
        return true;
    }
    @Override
    default ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }
}
