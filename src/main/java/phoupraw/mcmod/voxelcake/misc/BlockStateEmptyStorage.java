package phoupraw.mcmod.voxelcake.misc;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class BlockStateEmptyStorage extends BlockStateBinaryStorage implements InsertionOnlyStorage<ItemVariant> {
    private final BlockState fullState;
    public BlockStateEmptyStorage(World world, BlockPos pos, BlockState validState, BlockState fullState, ItemVariant resource, long amount) {
        super(world, pos, validState, resource, amount);
        this.fullState = fullState;
    }
    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (maxAmount >= amount && resource.equals(this.resource) && isValid()) {
            updateSnapshots(transaction);
            world.setBlockState(pos, fullState, 0);
            return amount;
        }
        return 0;
    }
    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return InsertionOnlyStorage.super.extract(resource, maxAmount, transaction);
    }
    @Override
    public ItemVariant getResource() {
        return isValid() ? ItemVariant.blank() : resource;
    }
    @Override
    public long getAmount() {
        return isValid() ? 0 : amount;
    }
    @Override
    public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
        return InsertionOnlyStorage.super.iterator();
    }
}
