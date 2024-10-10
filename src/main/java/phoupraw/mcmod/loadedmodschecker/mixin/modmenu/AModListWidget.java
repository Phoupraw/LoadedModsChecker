package phoupraw.mcmod.loadedmodschecker.mixin.modmenu;

import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import phoupraw.mcmod.loadedmodschecker.mixin.minecraft.AEntryListWidget;

@Environment(EnvType.CLIENT)
@Mixin(value = ModListWidget.class, remap = false)
public interface AModListWidget extends AEntryListWidget {
}
