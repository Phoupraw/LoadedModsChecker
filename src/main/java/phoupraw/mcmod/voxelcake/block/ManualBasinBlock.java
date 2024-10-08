package phoupraw.mcmod.voxelcake.block;

import com.google.common.base.Predicates;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.block.entity.ManualBasinBlockEntity;

public class ManualBasinBlock extends BlockEntityBlock {
    public static final VoxelShape SHAPE = VoxelShapes.union(
      createCuboidShape(2, 0, 2, 14, 2, 14),
      createCuboidShape(2, 2, 0, 14, 16, 2),
      createCuboidShape(14, 2, 2, 16, 16, 14),
      createCuboidShape(0, 2, 2, 2, 16, 14),
      createCuboidShape(2, 2, 14, 14, 16, 16)
    );
    public static final VoxelShape RAYCAST_SHAPE = VoxelShapes.union(SHAPE, createCuboidShape(2, 2, 2, 14, 16, 14));
    public ManualBasinBlock() {
        this(Settings.copy(Blocks.STONE));
    }
    public ManualBasinBlock(Settings settings) {
        super(settings);
    }
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ManualBasinBlockEntity(pos, state);
    }
    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @ApiStatus.OverrideOnly
    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.isOf(this) && !moved) {
            if (world.getBlockEntity(pos) instanceof ManualBasinBlockEntity blockEntity) {
                ItemScatterer.spawn(world, pos, blockEntity.getInventory());
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (hit.getSide() == Direction.UP) {
            if (!world.isClient() && world.getBlockEntity(pos) instanceof ManualBasinBlockEntity blockEntity) {
                var blockStorage = blockEntity.getItemStorage(null);
                var blockInv = blockEntity.getInventory();
                //int size = Iterables.size(blockStorage.nonEmptyViews());
                //if (size==0) {
                //    return ActionResult.FAIL;
                //}
                //if (size==1) {
                //    StorageUtil.move(blockStorage, PlayerInventoryStorage.of(player), Predicates.alwaysTrue(), Long.MAX_VALUE, null);
                //    //return StorageUtil.move(blockStorage, PlayerInventoryStorage.of(player), Predicates.alwaysTrue(), Long.MAX_VALUE, null) > 0 ? ActionResult.SUCCESS : ActionResult.FAIL;
                //}else if (size==2) {
                //
                //}
                StorageUtil.move(blockStorage, PlayerInventoryStorage.of(player), Predicates.alwaysTrue(), Long.MAX_VALUE, null);
                //ResourceAmount<ItemVariant> content = StorageUtil.findExtractableContent(blockStorage, null);
                //if (content != null && StorageUtil.move(blockStorage, PlayerInventoryStorage.of(player), Predicate.isEqual(content.resource()), content.amount(), null) > 0) {
                //    return ActionResult.SUCCESS;
                //}
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if ( hit.getSide() == Direction.UP && !stack.isEmpty()) {
            if (!world.isClient() && world.getBlockEntity(pos) instanceof ManualBasinBlockEntity blockEntity) {
                //if (StorageUtil.move(PlayerInventoryStorage.of(player).getHandSlot(hand), blockEntity.getItemStorage(null), Predicates.alwaysTrue(), Long.MAX_VALUE, null) <= 0) {
                //    return ItemActionResult.FAIL;
                //}
                StorageUtil.move(PlayerInventoryStorage.of(player).getHandSlot(hand), blockEntity.getItemStorage(null), Predicates.alwaysTrue(), Long.MAX_VALUE, null);
            }
            return ItemActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
