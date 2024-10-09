package phoupraw.mcmod.loadedmodschecker.misc;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.List;

public class CategoryEntry extends CheckingListWidget.Entry {
    private final TextWidget textWidget;
    public CategoryEntry(CheckingListWidget parent, Text text) {
        super(parent);
        textWidget = new TextWidget(text, parent.getClient().textRenderer);
    }
    @Override
    public List<? extends Selectable> selectableChildren() {
        return List.of();
    }
    @Override
    public List<? extends Element> children() {
        return List.of(textWidget);
    }
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        textWidget.setX(x);
        textWidget.setY(y);
        textWidget.render(context, mouseX, mouseY, tickDelta);
    }
}
