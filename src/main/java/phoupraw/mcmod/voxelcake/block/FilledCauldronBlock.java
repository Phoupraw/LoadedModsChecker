package phoupraw.mcmod.voxelcake.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;
import phoupraw.mcmod.voxelcake.misc.BlockStateEmptyStorage;
import phoupraw.mcmod.voxelcake.misc.BlockStateFullStorage;

public class FilledCauldronBlock extends MyBlock {
    /**
     @see AbstractCauldronBlock#RAYCAST_SHAPE
     */
    public static final VoxelShape RAYCAST_SHAPE = createCuboidShape(2, 4, 2, 14, 16, 14);
    /**
     @see AbstractCauldronBlock#OUTLINE_SHAPE
     */
    public static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
      VoxelShapes.fullCube(),
      VoxelShapes.union(
        createCuboidShape(0, 0, 4, 16, 3, 12),
        createCuboidShape(4, 0, 0, 12, 3, 16),
        createCuboidShape(2, 0, 2, 14, 3, 14),
        RAYCAST_SHAPE
      ),
      BooleanBiFunction.ONLY_FIRST
    );
    static {
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map().put(Items.SWEET_BERRIES, FilledCauldronBlock::interact);
    }
    @ApiStatus.Internal
    public static @Nullable Storage<ItemVariant> findFull(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction context) {
        if (context != Direction.DOWN) {
            return new BlockStateFullStorage(world, pos, state,Blocks.CAULDRON.getDefaultState(),ItemVariant.of(Items.SWEET_BERRIES),9);
        }
        return null;
    }
    @ApiStatus.Internal
    public static @Nullable Storage<ItemVariant> findEmpty(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction context) {
        if (context != Direction.DOWN) {
            return new BlockStateEmptyStorage(world, pos, state,VCBlocks.FILLED_CAULDRON.getDefaultState(),ItemVariant.of(Items.SWEET_BERRIES),9);
        }
        return null;
    }
    private static ItemActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        if (stack.getCount() >= 9) {
            player.incrementStat(Stats.FILL_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            stack.decrement(9);
            world.setBlockState(pos, VCBlocks.FILLED_CAULDRON.getDefaultState());
            world.playSound(null, pos, SoundEvents.BLOCK_WART_BLOCK_PLACE, SoundCategory.BLOCKS, 1, 1);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    public FilledCauldronBlock(Settings settings) {
        super(settings);
    }
    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultStack();
    }
    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
        ItemStack stack = Items.SWEET_BERRIES.getDefaultStack();
        stack.setCount(9);
        player.getInventory().offerOrDrop(stack);
        world.playSound(null, pos, SoundEvents.BLOCK_WART_BLOCK_BREAK, SoundCategory.BLOCKS, 1, 1);
        return ActionResult.SUCCESS;
    }
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }
    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 15;
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }
}
