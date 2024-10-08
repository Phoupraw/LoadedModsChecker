package phoupraw.mcmod.loadedmodschecker.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.loadedmodschecker.LoadedModsChecker;

import java.util.concurrent.CompletableFuture;

import static phoupraw.mcmod.loadedmodschecker.LoadedModsChecker.ID;

final class ChineseGen extends FabricLanguageProvider {
    ChineseGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "体素糕点";
        b.add(LoadedModsChecker.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "用方块充当体素制作单方块糕点");
        b.add("modmenu.descriptionTranslation." + ID, """
           
          
          """);
    }
}
