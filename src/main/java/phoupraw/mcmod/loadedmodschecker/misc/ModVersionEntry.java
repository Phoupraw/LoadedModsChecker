package phoupraw.mcmod.loadedmodschecker.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.Version;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.lwjgl.glfw.GLFW;
import phoupraw.mcmod.loadedmodschecker.LoadedModsChecker;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class ModVersionEntry extends CheckingListWidget.Entry {
    public static final String COPY = "gui." + LoadedModsChecker.ID + ".copy";
    //protected final TextRenderer textRenderer;
    public ModVersionEntry(CheckingListWidget parent) {
        super(parent);
        //this.textRenderer = parent.getClient().textRenderer;
    }
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        //parent.setFocused(this);
    }
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        //ModMetadata metadata = modContainer.getMetadata();
        Text modName = getModName();
        var versionText = getVersionText();
        TextRenderer textRenderer = parent.getClient().textRenderer;
        int gap = textRenderer.fontHeight + 1;
        context.drawText(textRenderer, modName, x + entryWidth / 2 - gap / 2 - textRenderer.getWidth(modName), y, -1, false);
        context.drawText(textRenderer, versionText, x + entryWidth / 2 + gap / 2, y, -1, false);
        if (isMouseOver(mouseX, mouseY)) {
            List<Text> tooltip = new ObjectArrayList<>(3);
            modifyTooltip(tooltip);
            context.drawTooltip(parent.getClient().textRenderer, tooltip, mouseX, mouseY);
        }
    }
    @Override
    public void drawBorder(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        if (hovered) {
            parent.drawSelectionHighlight(context, y, entryWidth, entryHeight, -1, 0xFF000000);
        }
    }
    public abstract Version getVersion();
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean r = super.mouseClicked(mouseX, mouseY, button);
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            parent.getClient().keyboard.setClipboard(getModId());
            parent.playDownSound(parent.getClient().getSoundManager());
            return true;
        }
        return r;
    }
    public abstract String getModId();
    public Text getVersionText() {
        return Text.literal(getVersion().getFriendlyString());
    }
    public Text getModName() {
        return Text.literal(getModId());
    }
    @MustBeInvokedByOverriders
    protected void modifyTooltip(List<Text> tooltip) {
        tooltip.add(Text.translatable(COPY).formatted(Formatting.GRAY, Formatting.ITALIC));
    }
    //@Override
    //protected void afterMouseClicked(double mouseX, double mouseY, int button, boolean result) {
    //    if (result) {
    //        parent.playDownSound(parent.getClient().getSoundManager());
    //    }
    //}
}
