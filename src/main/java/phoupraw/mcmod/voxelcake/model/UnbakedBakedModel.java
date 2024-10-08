package phoupraw.mcmod.voxelcake.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
@Environment(EnvType.CLIENT)
public class UnbakedBakedModel implements UnbakedModel {
    public final @Nullable BakedModel bakedModel;
    public UnbakedBakedModel(@Nullable BakedModel bakedModel) {this.bakedModel = bakedModel;}
    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of();
    }
    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
    
    }
    @Override
    public @Nullable BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        return bakedModel;
    }
}
