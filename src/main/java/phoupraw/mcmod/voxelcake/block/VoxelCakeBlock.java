package phoupraw.mcmod.voxelcake.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.block.entity.VoxelCakeBlockEntity;
import phoupraw.mcmod.voxelcake.misc.BlockCake;

import static net.minecraft.block.DoorBlock.FACING;

public class VoxelCakeBlock extends BlockEntityBlock {
    public static final VoxelShape SHAPE = Block.createCuboidShape(1, 0, 1, 15, 8, 15);
    public VoxelCakeBlock() {
        this(Settings.copy(Blocks.CAKE).hardness(0).dynamicBounds());
    }
    public VoxelCakeBlock(Settings settings) {
        super(settings);
    }
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VoxelCakeBlockEntity(pos, state);
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
    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        ItemStack pickStack = super.getPickStack(world, pos, state);
        if (world.getBlockEntity(pos) instanceof VoxelCakeBlockEntity blockEntity) {
            pickStack.applyComponentsFrom(blockEntity.createComponentMap());
            //blockEntity.setStackNbt(pickStack,world.getRegistryManager());
        }
        return pickStack;
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (world.getBlockEntity(pos) instanceof VoxelCakeBlockEntity blockEntity) {
            return BlockCake.getShape(blockEntity.getCakeEntry().value(),state.get(FACING));
        }
        return SHAPE;
    }
}
