package phoupraw.mcmod.loadedmodschecker.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.loadedmodschecker.LoadedModsChecker;
import phoupraw.mcmod.loadedmodschecker.misc.ModVersionEntry;
import phoupraw.mcmod.loadedmodschecker.misc.NewModEntry;
import phoupraw.mcmod.loadedmodschecker.mixins.minecraft.MMIntegratedServerLoader;

import java.util.concurrent.CompletableFuture;

import static phoupraw.mcmod.loadedmodschecker.LoadedModsChecker.ID;

final class EnglishGen extends FabricLanguageProvider {
    EnglishGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "Loaded Mods Checker";
        b.add(LoadedModsChecker.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "Check if there are missing mods compared to the last time when entering a world");
        b.add("modmenu.descriptionTranslation." + ID, """
          Every time you enter a world, it will compare whether the currently loaded mods are consistent with the mods loaded the last time. If any inconsistency is detected, it will prompt which mods are inconsistent and allow you to return directly without loading the world. 
          """);
        b.add(MMIntegratedServerLoader.TITLE, "The currently loaded mods are different from the last time enter §o%s§r");
        b.add(MMIntegratedServerLoader.NEW, "New:");
        b.add(MMIntegratedServerLoader.DELETED, "Deleted:");
        b.add(MMIntegratedServerLoader.UPDATED, "Updated:");
        b.add(MMIntegratedServerLoader.ROLLBACKED, "Rollbacked:");
        b.add(NewModEntry.MODMENU,"Left click to jump to Mod Menu");
        b.add(ModVersionEntry.COPY,"Right click to copy the id of the mod");
    }
}
