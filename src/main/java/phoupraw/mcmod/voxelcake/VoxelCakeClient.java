package phoupraw.mcmod.voxelcake;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.RenderLayer;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;
import phoupraw.mcmod.voxelcake.model.ManualBasinModel;
import phoupraw.mcmod.voxelcake.model.VoxelCakeModel;

import static phoupraw.mcmod.voxelcake.VoxelCake.loadClass;

@Environment(EnvType.CLIENT)
public final class VoxelCakeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        loadClass(VoxelCakeModel.class);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), VCBlocks.FILLED_CAULDRON,VCBlocks.MANUAL_BASIN);
        ModelLoadingPlugin.register(ManualBasinModel::onInitializeModelLoader);
    }
}
