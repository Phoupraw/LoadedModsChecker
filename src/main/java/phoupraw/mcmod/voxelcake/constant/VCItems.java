package phoupraw.mcmod.voxelcake.constant;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.item.PowderSnowLikeItem;
import phoupraw.mcmod.voxelcake.item.VoxelCakeItem;

import java.util.Arrays;
import java.util.Collection;

/**
 <ul>
 <li><b>复制的时候记得改物品类！</b></li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.ItemTagGen}</li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.ModelGen}</li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.ChineseGen}</li>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.RecipeGen}</li>
 </ul>
 */

@ApiStatus.NonExtendable
public interface VCItems {
    Collection<ItemConvertible> EADIBLE_BLOCKS = new ObjectLinkedOpenHashSet<>();
    Collection<ItemConvertible> INEADIBLE_BLOCKS = new ObjectLinkedOpenHashSet<>();
    BlockItem VOXEL_CAKE = r(VCIDs.VOXEL_CAKE, new VoxelCakeItem());
    BlockItem MANUAL_BASIN = r(VCIDs.MANUAL_BASIN, new BlockItem(VCBlocks.MANUAL_BASIN,new Item.Settings()));
    BlockItem BUCKETED_CREAM = jam(VCIDs.BUCKETED_CREAM, (VCBlocks.CREAM));
    BlockItem BUCKETED_SWEET_BERRY_JAM = jam(VCIDs.BUCKETED_SWEET_BERRY_JAM, (VCBlocks.SWEET_BERRY_JAM));
    Item KELP_ASH = r(VCIDs.KELP_ASH,new Item(new Item.Settings()));
    ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, VCIDs.VOXEL_CAKE, FabricItemGroup.builder()
      .displayName(VoxelCake.name())
      .icon(VOXEL_CAKE::getDefaultStack)
      .entries(VCItems::entries)
      .build());
    private static <T extends Item> T r(Identifier id, T value) {
        return Registry.register(Registries.ITEM, id, value);
    }
    private static void entries(ItemGroup.DisplayContext displayContext, ItemGroup.Entries entries) {
        entries.add(MANUAL_BASIN);
        for (var item : EADIBLE_BLOCKS) {
            entries.add(item);
        }
        for (var item : INEADIBLE_BLOCKS) {
            entries.add(item);
        }
        for (var item : Arrays.asList(KELP_ASH)) {
            entries.add(item);
        }
    }
    private static BlockItem of(Block block) {
        return r(Registries.BLOCK.getId(block), new BlockItem(block, new Item.Settings()));
    }
    private static BlockItem jam(Identifier id, Block block) {
        return r(id, new PowderSnowLikeItem(block));
    }
}
