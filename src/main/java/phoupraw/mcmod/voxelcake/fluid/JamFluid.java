package phoupraw.mcmod.voxelcake.fluid;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;

import java.util.Map;

public class JamFluid extends VirtualFluid {
    public static final Map<Block, Fluid> MAP = new Object2ObjectOpenHashMap<>();
    private final Block block;
    public JamFluid(Item bucketItem, Block block) {
        super(bucketItem);
        this.block = block;
        MAP.put(block,this);
    }
    @Override
    public BlockState toBlockState(FluidState state) {
        return getBlock().getDefaultState();
    }
    public Block getBlock() {
        return block;
    }
}
