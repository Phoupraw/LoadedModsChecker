package phoupraw.mcmod.loadedmodschecker;

import lombok.SneakyThrows;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;

public final class LoadedModsChecker implements ModInitializer {
    public static final String ID = "voxelcake";
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger(ID);
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    @Contract(value = " -> new", pure = true)
    public static @NotNull MutableText name() {
        return Text.translatable(NAME_KEY);
    }
    @SneakyThrows
    static void loadClass(Class<?> cls) {
        MethodHandles.lookup().ensureInitialized(cls);
    }
    @Override
    public void onInitialize() {
    
        
    }
}
