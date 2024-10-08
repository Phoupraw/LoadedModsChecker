package phoupraw.mcmod.voxelcake.item;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;
import phoupraw.mcmod.voxelcake.constant.VCComponentTypes;
import phoupraw.mcmod.voxelcake.misc.BlockCake;
import phoupraw.mcmod.voxelcake.misc.Cake;

public class VoxelCakeItem extends BlockItem {
    public VoxelCakeItem() {
        this(VCBlocks.VOXEL_CAKE, new Settings().component(VCComponentTypes.CAKE, BlockCake.EMPTY));
    }
    public VoxelCakeItem(Block block, Settings settings) {
        super(block, settings);
    }
    @Override
    public Text getName(ItemStack stack) {
        var name = Cake.getName(stack.getOrDefault(VCComponentTypes.CAKE,BlockCake.EMPTY));if (name!=null)return name;
        return super.getName(stack);
    }
    @Override
    public void postProcessComponents(ItemStack stack) {
        super.postProcessComponents(stack);
        var cakeEntry = stack.getOrDefault(VCComponentTypes.CAKE, BlockCake.EMPTY);
        var cake = cakeEntry.value();
        if (cake.isEmpty() ) {
            if (stack.get(DataComponentTypes.FOOD)!=null) {
                stack.remove(DataComponentTypes.FOOD);
            }
        }else{
            //TODO 什么情况下可以拿在手上吃？是一次性全吃掉还是每次只吃一口？怎么处理剩余物？
        }
    }
}
