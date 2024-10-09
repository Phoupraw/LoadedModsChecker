package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.List;

public class NewModEntry extends CheckingListWidget.Entry {
    private final TextRenderer textRenderer;
    private final ModContainer modContainer;
    public NewModEntry(TextRenderer textRenderer, ModContainer modContainer) {
        this.textRenderer = textRenderer;
        this.modContainer = modContainer;
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
        ModMetadata metadata = modContainer.getMetadata();
        Text modName = FabricUtils.getModName(metadata.getId());
        var versionText = Text.literal(metadata.getVersion().getFriendlyString());
        context.drawText(textRenderer, modName, x + entryWidth / 2 - textRenderer.getWidth(modName), y, -1, false);
        context.drawText(textRenderer, versionText, x + entryWidth / 2, y, -1, false);
    }
}
