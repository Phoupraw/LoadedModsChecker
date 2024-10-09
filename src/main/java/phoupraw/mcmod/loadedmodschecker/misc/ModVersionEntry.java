package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.loader.api.Version;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.List;

public abstract class ModVersionEntry extends CheckingListWidget.Entry {
    protected final TextRenderer textRenderer;
    public ModVersionEntry(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }
    @Override
    public List<? extends Selectable> selectableChildren() {
        return List.of();
    }
    @Override
    public List<? extends Element> children() {
        return List.of();
    }
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        //ModMetadata metadata = modContainer.getMetadata();
        Text modName = getModName();
        var versionText = Text.literal(getVersion().getFriendlyString());
        int gap = textRenderer.fontHeight;
        context.drawText(textRenderer, modName, x + entryWidth / 2 - gap / 2 - textRenderer.getWidth(modName), y, -1, false);
        context.drawText(textRenderer, versionText, x + entryWidth / 2 + gap / 2, y, -1, false);
    }
    public abstract Text getModName();
    public abstract Version getVersion();
}
