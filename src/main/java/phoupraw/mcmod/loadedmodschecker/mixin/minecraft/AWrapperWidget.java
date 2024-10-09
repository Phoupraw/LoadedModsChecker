package phoupraw.mcmod.loadedmodschecker.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.WrapperWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(WrapperWidget.class)
public interface AWrapperWidget {
    @Accessor
    void setWidth(int width);
    @Accessor
    void setHeight(int height);
}
