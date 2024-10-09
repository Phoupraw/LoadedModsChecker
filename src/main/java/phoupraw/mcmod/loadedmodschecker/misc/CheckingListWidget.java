package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import phoupraw.mcmod.loadedmodschecker.mixins.minecraft.MMIntegratedServerLoader;

@Environment(EnvType.CLIENT)
public class CheckingListWidget extends ElementListWidget<CheckingListWidget.Entry> {
    private final CheckingScreen parent;
    public CheckingListWidget(MinecraftClient client, int width, int height, int y, int itemHeight, ModsChanges modsChanges, CheckingScreen parent) {
        super(client, width, height, y, itemHeight);
        this.parent = parent;
        //for (Entry entry : entries) {
        //    addEntry(entry);
        //}
        if (!modsChanges.newMods().isEmpty()) {
            addEntry(new CategoryEntry(this, Text.translatable(MMIntegratedServerLoader.NEW).formatted(Formatting.AQUA)));
            for (String modId : modsChanges.newMods()) {
                addEntry(new NewModEntry(this, FabricLoader.getInstance().getModContainer(modId).orElseThrow()));
            }
        }
        if (!modsChanges.deletedMods().isEmpty()) {
            addEntry(new CategoryEntry(this, Text.translatable(MMIntegratedServerLoader.DELETED).formatted(Formatting.RED)));
            for (var entry : modsChanges.deletedMods().entrySet()) {
                addEntry(new DeletedModEntry(this, entry.getKey(), entry.getValue()));
            }
        }
    }
    @Override
    public int getRowWidth() {
        return super.getRowWidth();
    }
    @Override
    public void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        super.drawSelectionHighlight(context, y, entryWidth, entryHeight, borderColor, fillColor);
    }
    public MinecraftClient getClient() {
        return client;
    }
    //@Override
    //public int addEntry(Entry entry) {
    //    return super.addEntry(entry);
    //}
    public CheckingScreen getParent() {
        return parent;
    }
    @Environment(EnvType.CLIENT)
    public static abstract class Entry extends ElementListWidget.Entry<Entry> {
        protected final CheckingListWidget parent;
        public Entry(CheckingListWidget parent) {
            this.parent = parent;
        }
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
