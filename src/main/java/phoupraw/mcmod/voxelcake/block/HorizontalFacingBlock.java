package phoupraw.mcmod.voxelcake.block;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.block.DoorBlock.FACING;

public class HorizontalFacingBlock extends MyBlock {
    /**
     直接绕y轴旋转，不会平移到原点（因为不知道要平移多少）
     
     @param self
     @param facing
     @return
     */
    public static Vec3d rotatedY(Vec3d self, Direction facing) {
        float angle = (Direction.NORTH.getHorizontal() - facing.getHorizontal()) * MathHelper.PI / 2;
        return self.rotateY(angle);
    }
    /**
     @param originBoxes 棱长为16
     @see #originBoxOf
     */
    public static Map<Direction, VoxelShape> toHorizontalShapes(Collection<Box> originBoxes) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction facing : net.minecraft.block.HorizontalFacingBlock.FACING.getValues()) {
            Collection<VoxelShape> others = new ObjectArrayList<>(originBoxes.size());
            for (Box box : originBoxes) {
                Vec3d v1 = rotatedY(new Vec3d(box.minX, box.minY, box.minZ), facing).multiply(1 / 16d);
                Vec3d v2 = rotatedY(new Vec3d(box.maxX, box.maxY, box.maxZ), facing).multiply(1 / 16d);
                others.add(VoxelShapes.cuboid(new Box(v1, v2).offset(0.5, 0.5, 0.5)));
            }
            shapes.put(facing, VoxelShapes.union(VoxelShapes.empty(), others.toArray(VoxelShape[]::new)));
        }
        return shapes;
    }
    /**
     @param a 长
     @param b 高
     @param c 宽
     @return 棱长为16，平移到原点
     @see #toHorizontalShapes
     */
    public static Box originBoxOf(double x, double y, double z, double a, double b, double c) {
        return boxOf(x, y, z, a, b, c).offset(-8, -8, -8);
    }
    public HorizontalFacingBlock(Settings settings) {
        super(settings);
    }
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        return state == null ? null : state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }
    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return super.rotate(state, rotation).with(FACING, rotation.rotate(state.get(FACING)));
    }
    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return super.mirror(state, mirror).with(FACING, mirror.apply(state.get(FACING)));
    }
}
