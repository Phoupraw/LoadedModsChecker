package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Matrix2d;
import org.joml.Matrix2dc;
import org.joml.Vector3d;

import java.util.Comparator;
import java.util.List;

public interface MCUtils {
    @Unmodifiable
    List<@NotNull Direction> DIRECTIONS = List.of(Direction.values());
    Comparator<? super Vec3i> VEC3I_COMPARATOR = Comparator
      .comparingInt(Vec3i::getY)
      .thenComparingInt(Vec3i::getX)
      .thenComparingInt(Vec3i::getZ);
    @Environment(EnvType.CLIENT)
    @Contract(pure = true)
    static Sprite getMissingSprite() {
        return MinecraftClient.getInstance().getBakedModelManager().getMissingModel().getParticleSprite();
    }
    @Contract(pure = true)
    static Pair<Matrix2dc, Double> square(Box box, Direction face, Vec3i size) {
        return switch (face) {
            case WEST -> Pair.of(new Matrix2d(box.minZ, box.minY, box.maxZ, box.maxY), box.minX);
            case EAST -> Pair.of(new Matrix2d(size.getZ() - box.maxZ, box.minY, size.getZ() - box.minZ, box.maxY), size.getX() - box.maxX);
            case DOWN -> Pair.of(new Matrix2d(box.minX, box.minZ, box.maxX, box.maxZ), box.minY);
            case UP -> Pair.of(new Matrix2d(box.minX, size.getZ() - box.maxZ, box.maxX, size.getZ() - box.minZ), size.getY() - box.maxY);
            case NORTH -> Pair.of(new Matrix2d(size.getX() - box.maxX, box.minY, size.getX() - box.minX, box.maxY), box.minZ);
            case SOUTH -> Pair.of(new Matrix2d(box.minX, box.minY, box.maxX, box.maxY), size.getZ() - box.maxZ);
        };
    }
    @Contract(pure = true)
    static Box rotatedY(Box normalized, Direction origin, Direction target) {
        double angle = (origin.getHorizontal() - target.getHorizontal()) * Math.PI / 2;
        var min = new Vector3d()
          .set(normalized.minX - 0.5, normalized.minY, normalized.minZ - 0.5)
          .rotateY(angle)
          .add(0.5, 0, 0.5);
        var max = new Vector3d()
          .set(normalized.maxX - 0.5, normalized.maxY, normalized.maxZ - 0.5)
          .rotateY(angle)
          .add(0.5, 0, 0.5);
        return new Box(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }
    @Contract(pure = true)
    static VoxelShape rotatedY(VoxelShape shape, Direction origin, Direction target) {
        if (target == origin) return shape;
        if (shape.isEmpty()) return shape;
        VoxelShape rotated = VoxelShapes.empty();
        for (Box box : shape.getBoundingBoxes()) {
            rotated = VoxelShapes.union(rotated, VoxelShapes.cuboid(rotatedY(box, origin, target)));
        }
        return rotated;
    }
    static boolean hasOpLevel(CommandSource source) {
        return source.hasPermissionLevel(2);
    }
}
