package phoupraw.mcmod.voxelcake.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntityBlock extends MyBlock implements BlockEntityProvider {
    public static boolean onSyncedBlockEvent(Block self, World world, BlockPos pos, int type, int data) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }
    public static ActionResult onUse(Block self, BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        var menuFactory = state.createScreenHandlerFactory(world, pos);
        if (menuFactory != null) {
            player.openHandledScreen(menuFactory);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    public static void onStateReplaced(Block self, BlockState state, World world, BlockPos pos) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos, state, null, null);
        if (storage != null && storage.supportsExtraction()) {
            DefaultedList<ItemStack> itemStacks = DefaultedList.of();
            try (var t = Transaction.openOuter()) {
                for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
                    ItemVariant resource = view.getResource();
                    long extracted = view.extract(resource, view.getAmount(), t);
                    while (extracted > 0) {
                        ItemStack stack = resource.toStack();
                        int count = (int) Math.min(extracted, stack.getMaxCount());
                        extracted -= count;
                        stack.setCount(count);
                        itemStacks.add(stack);
                    }
                }
                t.commit();
            }
            ItemScatterer.spawn(world, pos, itemStacks);
        }
    }
    public BlockEntityBlock(Settings settings) {
        super(settings);
    }
    @ApiStatus.OverrideOnly
    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntityBlock.onStateReplaced(this, state, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        var result = BlockEntityBlock.onUse(this, state, world, pos, player,  hit);
        if (result != ActionResult.PASS) return result;
        return super.onUse(state, world, pos, player, hit);
    }
    @ApiStatus.OverrideOnly
    @Override
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        return super.onSyncedBlockEvent(state, world, pos, type, data) | BlockEntityBlock.onSyncedBlockEvent(this, world, pos, type, data);
    }
    @ApiStatus.OverrideOnly
    @Override
    protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof NamedScreenHandlerFactory menuFactory ? menuFactory : super.createScreenHandlerFactory(state, world, pos);
    }
}
