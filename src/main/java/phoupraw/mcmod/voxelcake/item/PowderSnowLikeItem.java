package phoupraw.mcmod.voxelcake.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PowderSnowLikeItem extends BlockItem implements FluidModificationItem {
    public PowderSnowLikeItem(Block block) {
        this(block, new Settings().maxCount(4).recipeRemainder(Items.BUCKET));
    }
    public PowderSnowLikeItem(Block block, Settings settings) {
        super(block, settings);
    }
    @Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        if (!world.isInBuildLimit(pos) || !world.isAir(pos)) return false;
        BlockState blockState = this.getBlock().getDefaultState();
        world.setBlockState(pos, blockState, 3);
        world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
        world.playSound(player, pos, getPlaceSound(blockState), SoundCategory.BLOCKS, 1, 1);
        return true;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult result = super.useOnBlock(context);
        PlayerEntity player = context.getPlayer();
        if (result.isAccepted() && player != null && (!player.isCreative() || !player.getInventory().contains(Items.BUCKET.getDefaultStack()))) {
            player.getInventory().offerOrDrop(Items.BUCKET.getDefaultStack());
        }
        return result;
    }
    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }
}
