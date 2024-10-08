package phoupraw.mcmod.voxelcake.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;
import phoupraw.mcmod.voxelcake.constant.VCComponentTypes;
import phoupraw.mcmod.voxelcake.constant.VCItems;

import java.util.concurrent.CompletableFuture;

final class BlockLootGen extends FabricBlockLootTableProvider {
     BlockLootGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override
    public void generate() {
        addDrop(VCBlocks.VOXEL_CAKE, LootTable.builder()
          .pool(LootPool.builder()
            .with(ItemEntry.builder(VCItems.VOXEL_CAKE)
              .apply(CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)
                .include(VCComponentTypes.CAKE)
                .include(DataComponentTypes.CUSTOM_NAME)))));
        addDrop(VCBlocks.FILLED_CAULDRON,LootTable.builder()
          .pool(LootPool.builder()
            .with(ItemEntry.builder(Items.CAULDRON))
            .with(ItemEntry.builder(Items.SWEET_BERRIES)
              .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(9))))));
        addDrop(VCBlocks.MANUAL_BASIN, LootTable.builder()
          .pool(LootPool.builder()
            .with(ItemEntry.builder(VCItems.MANUAL_BASIN)
              .apply(CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)
                .include(DataComponentTypes.CUSTOM_NAME)))));
    }
}
