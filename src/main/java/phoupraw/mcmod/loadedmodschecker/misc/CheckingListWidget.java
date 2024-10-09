package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import phoupraw.mcmod.loadedmodschecker.mixins.minecraft.MMIntegratedServerLoader;

@Environment(EnvType.CLIENT)
public class CheckingListWidget extends ElementListWidget<CheckingListWidget.Entry> {
    public CheckingListWidget(MinecraftClient client, int width, int height, int y, int itemHeight, ModsChanges modsChanges) {
        super(client, width, height, y, itemHeight);
        //for (Entry entry : entries) {
        //    addEntry(entry);
        //}
        if (!modsChanges.newMods().isEmpty()) {
            addEntry(new CategoryEntry(client.textRenderer, Text.translatable(MMIntegratedServerLoader.NEW).formatted(Formatting.AQUA)));
            for (String modId : modsChanges.newMods()) {
                addEntry(new NewModEntry(client.textRenderer, FabricLoader.getInstance().getModContainer(modId).orElseThrow()));
            }
        }
        if (!modsChanges.deletedMods().isEmpty()) {
            addEntry(new CategoryEntry(client.textRenderer, Text.translatable(MMIntegratedServerLoader.DELETED).formatted(Formatting.RED)));
            for (var entry : modsChanges.deletedMods().entrySet()) {
                addEntry(new DeletedModEntry(client.textRenderer, entry.getKey(), entry.getValue()));
            }
        }
    }
    @Override
    public int getRowWidth() {
        return super.getRowWidth();
    }
    //@Override
    //public int addEntry(Entry entry) {
    //    return super.addEntry(entry);
    //}
    @Environment(EnvType.CLIENT)
    public static abstract class Entry extends ElementListWidget.Entry<Entry> {
        //@Override
        //public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        //    for (Element child : children()) {
        //        if (child instanceof Drawable drawable) {
        //            drawable.render(context, mouseX, mouseY, tickDelta);
        //        }
        //    }
        //}
    }
    
}
