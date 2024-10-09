package phoupraw.mcmod.loadedmodschecker.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(EntryListWidget.class)
public interface AEntryListWidget {
    @Invoker
    void invokeCenterScrollOn(EntryListWidget.Entry<?> entry);
}
