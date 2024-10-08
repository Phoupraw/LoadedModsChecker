package phoupraw.mcmod.voxelcake.misc;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockStateBinaryStorage extends BlockStateSnapshotParticipant implements SingleSlotStorage<ItemVariant> {
    protected final ItemVariant resource;
    protected final long amount;
    protected final BlockState validState;
    public BlockStateBinaryStorage(World world, BlockPos pos, BlockState validState, ItemVariant resource, long amount) {
        super(world, pos);
        this.resource = resource;
        this.amount = amount;
        this.validState = validState;
    }
    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }
    @Override
    public long getCapacity() {
        return amount;
    }
    public boolean isValid() {
        return getBlockState().equals(validState);
    }
}
