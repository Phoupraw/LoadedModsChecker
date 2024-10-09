package phoupraw.mcmod.loadedmodschecker.mixin.modmenu;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(value = ModsScreen.class, remap = false)
public interface AModsScreen {
    @Accessor
    ModListWidget getModList();
    @Accessor
    ClickableWidget getLibrariesButton();
}
