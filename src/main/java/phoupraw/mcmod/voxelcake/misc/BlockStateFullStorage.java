package phoupraw.mcmod.voxelcake.misc;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateFullStorage extends BlockStateBinaryStorage implements ExtractionOnlyStorage<ItemVariant> {
    private final BlockState emptyState;
    public BlockStateFullStorage(World world, BlockPos pos, BlockState fullState, BlockState emptyState, ItemVariant resource, long amount) {
        super(world, pos, fullState, resource, amount);
        this.emptyState = emptyState;
    }
    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (maxAmount >= amount && resource.equals(this.resource) && isValid()) {
            updateSnapshots(transaction);
            world.setBlockState(pos, emptyState, 0);
            return amount;
        }
        return 0;
    }
    @Override
    public ItemVariant getResource() {
        return isValid() ? resource : ItemVariant.blank();
    }
    @Override
    public long getAmount() {
        return isValid() ? amount : 0;
    }
}
