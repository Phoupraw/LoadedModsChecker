package phoupraw.mcmod.voxelcake.misc;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateSnapshotParticipant extends SnapshotParticipant<BlockState> {
    protected final World world;
    protected final BlockPos pos;
    private BlockState releasedSnapshot;
    public BlockStateSnapshotParticipant(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }
    @Override
    protected BlockState createSnapshot() {
        return getBlockState();
    }
    @Override
    protected void readSnapshot(BlockState snapshot) {
        world.setBlockState(pos, snapshot, 0);
    }
    @Override
    protected void releaseSnapshot(BlockState snapshot) {
        super.releaseSnapshot(snapshot);
        releasedSnapshot = snapshot;
    }
    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        BlockState current = getBlockState();
        BlockState prev = releasedSnapshot;
        if (!current.equals(prev)) {
            world.setBlockState(pos, prev, 0);
            world.setBlockState(pos, current);
        }
    }
    protected BlockState getBlockState() {
        return world.getBlockState(pos);
    }
}
