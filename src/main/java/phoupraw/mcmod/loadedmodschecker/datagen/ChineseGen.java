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

final class ChineseGen extends FabricLanguageProvider {
    ChineseGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "存档模组核对";
        b.add(LoadedModsChecker.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "进入存档时检测与上次相比是否缺失模组");
        b.add("modmenu.descriptionTranslation." + ID, """
          每次进入存档时会比对当前加载的模组与上次进入存档时加载的模组是否一致，如果检测到不一致，则并提示有哪些模组不一致，并允许玩家不加载存档直接返回。 
          """);
        b.add(MMIntegratedServerLoader.TITLE, "当前加载的模组与上次进入《%s》时的有所不同");
        b.add(MMIntegratedServerLoader.NEW, "新增：");
        b.add(MMIntegratedServerLoader.DELETED, "移除：");
        b.add(MMIntegratedServerLoader.UPDATED, "更新：");
        b.add(MMIntegratedServerLoader.ROLLBACKED, "回退：");
        b.add(NewModEntry.MODMENU, "左键单击跳转到模组菜单");
        b.add(ModVersionEntry.COPY, "右键单击复制模组ID");
    }
}
