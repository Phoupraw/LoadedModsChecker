package phoupraw.mcmod.loadedmodschecker.mixins;

import com.terraformersmc.modmenu.ModMenu;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import phoupraw.mcmod.loadedmodschecker.LoadedModsChecker;

import java.util.List;
import java.util.Set;

public final class LoadedModsCheckerMixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    
    }
    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("phoupraw.mcmod." + LoadedModsChecker.ID + ".mixin." + ModMenu.MOD_ID)) {
            return FabricLoader.getInstance().isModLoaded(ModMenu.MOD_ID);
        }
        return true;
    }
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    
    }
    @Override
    public @Nullable List<String> getMixins() {
        return null;
    }
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    
    }
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    
    }
}
