package phoupraw.mcmod.loadedmodschecker.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.Version;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.lwjgl.glfw.GLFW;
import phoupraw.mcmod.loadedmodschecker.LoadedModsChecker;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class ModVersionEntry extends CheckingListWidget.Entry {
    public static final String COPY = "gui." + LoadedModsChecker.ID + ".copy";
    protected final TextWidget leftTextWidget;
    protected final TextWidget rightTextWidget;
    public ModVersionEntry(CheckingListWidget parent) {
        super(parent);
        leftTextWidget = new TextWidget(Text.empty(), parent.getClient().textRenderer);
        rightTextWidget = new TextWidget(Text.empty(), parent.getClient().textRenderer);
    }
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        TextRenderer textRenderer = parent.getClient().textRenderer;
        int gap = textRenderer.fontHeight + 1;
        leftTextWidget.setX(x + entryWidth / 2 - gap / 2 - leftTextWidget.getWidth());
        leftTextWidget.setY(y);
        leftTextWidget.render(context, mouseX, mouseY, tickDelta);
        rightTextWidget.setX(x + entryWidth / 2 + gap / 2);
        rightTextWidget.setY(y);
        rightTextWidget.render(context, mouseX, mouseY, tickDelta);
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
    @Override
    protected void init() {
        super.init();
        leftTextWidget.setMessage(getModName());
        rightTextWidget.setMessage(getVersionText());
        TextRenderer textRenderer = parent.getClient().textRenderer;
        leftTextWidget.setWidth(textRenderer.getWidth(leftTextWidget.getMessage()));
        rightTextWidget.setWidth(textRenderer.getWidth(rightTextWidget.getMessage()));
    }
    @Override
    public int getMinWidth() {
        TextRenderer textRenderer = parent.getClient().textRenderer;
        int gap = textRenderer.fontHeight + 1;
        int textWidth = Math.max(leftTextWidget.getWidth(), rightTextWidget.getWidth());
        return textWidth * 2 + gap * 2;
    }
    public Text getVersionText() {
        return Text.literal(getVersion().getFriendlyString());
    }
    public Text getModName() {
        return Text.literal(getModId());
    }
    protected void adjustTextsWidth() {
        TextRenderer textRenderer = parent.getClient().textRenderer;
        leftTextWidget.setWidth(textRenderer.getWidth(leftTextWidget.getMessage()));
        rightTextWidget.setWidth(textRenderer.getWidth(rightTextWidget.getMessage()));
    }
    @MustBeInvokedByOverriders
    protected void modifyTooltip(List<Text> tooltip) {
        tooltip.add(Text.translatable(COPY).formatted(Formatting.GRAY, Formatting.ITALIC));
    }
    public abstract Version getVersion();
    public abstract String getModId();
}
