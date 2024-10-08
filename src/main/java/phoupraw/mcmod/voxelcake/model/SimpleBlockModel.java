package phoupraw.mcmod.voxelcake.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.constant.VCIDs;

import java.util.List;
import java.util.Map;
@Environment(EnvType.CLIENT)
public class SimpleBlockModel implements HasDepthModel {
    public static final Sprite EMPTY_SPRITE = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(VCIDs.of("empty"));
    public static final SimpleBlockModel EMPTY = new SimpleBlockModel(Map.of()/*MultimapBuilder.hashKeys().linkedListValues().build()*/, EMPTY_SPRITE);
    public final Map<@Nullable Direction, List<BakedQuad>> cullFace2quads;
    private final Sprite particleSprite;
    public SimpleBlockModel(Map<@Nullable Direction, List<BakedQuad>> cullFace2quads, Sprite particleSprite) {
        this.cullFace2quads = cullFace2quads;
        this.particleSprite = particleSprite;
    }
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return cullFace2quads.getOrDefault(face, List.of());
    }
    @Override
    public boolean isBuiltin() {
        return false;
    }
    @Override
    public Sprite getParticleSprite() {
        return particleSprite;
    }
    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
    @Override
    public String toString() {
        return "SimpleBakedModel{" +
          "faces2quads=" + cullFace2quads + ", " +
          "particle=" + particleSprite + '}';
    }
}
