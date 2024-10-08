package phoupraw.mcmod.voxelcake.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.constant.VCBlocks;
import phoupraw.mcmod.voxelcake.constant.VCItems;

import java.util.concurrent.CompletableFuture;

import static phoupraw.mcmod.voxelcake.VoxelCake.ID;

final class ChineseGen extends FabricLanguageProvider {
    ChineseGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "体素糕点";
        b.add(VoxelCake.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "用方块充当体素制作单方块糕点");
        b.add("modmenu.descriptionTranslation." + ID, """
           
          
          """);
        b.add(VCBlocks.VOXEL_CAKE,"体素糕点");
        b.add(VCBlocks.CREAM,"奶油");
        b.add(VCItems.BUCKETED_CREAM,"桶装奶油");
        b.add(VCBlocks.SWEET_BERRY_JAM,"甜浆果酱");
        b.add(VCItems.BUCKETED_SWEET_BERRY_JAM,"桶装甜浆果酱");
        b.add(VCItems.KELP_ASH,"海带灰烬");
        b.add(VCBlocks.FILLED_CAULDRON,"装满甜浆果的炼药锅");
        b.add(VCBlocks.MANUAL_BASIN,"工作釜");
    }
}
