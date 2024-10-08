package phoupraw.mcmod.voxelcake.fluid;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

@Getter
public class VirtualFluid extends FlowableFluid {
    private final Item bucketItem;
    public VirtualFluid(Item bucketItem) {
        this.bucketItem = bucketItem;
    }
    
    @Override
    public Fluid getFlowing() {
        return this;
    }

    @Override
    public Fluid getStill() {
        return this;
    }

    @Override
    protected boolean isInfinite(World world) {
        return false;
    }

   @Override
    public void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }
    
    @Override
    protected int getMaxFlowDistance(WorldView world) {
        return 0;
    }
    /**
     * Water returns 1. Lava returns 2 in the Overworld and 1 in the Nether.
     */
    @Override
    public int getLevelDecreasePerBlock(WorldView worldView) {
        return 0;
    }

    @Override
    public int getLevel(FluidState state) {
        return 8;
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }

   @Override
    public int getTickRate(WorldView worldView) {
        return Integer.MAX_VALUE;
    }

   @Override
    public float getBlastResistance() {
        return 100;
    }

    @Override
    public BlockState toBlockState(FluidState state) {
        return Blocks.AIR.getDefaultState();
    }
    
    @Override
    public boolean isStill(FluidState state) {
        return true;
    }
}
