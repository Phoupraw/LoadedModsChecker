package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import phoupraw.mcmod.loadedmodschecker.mixin.minecraft.AWrapperWidget;

import java.util.Arrays;
import java.util.List;

//TODO 滚动条
@Environment(EnvType.CLIENT)
public class CheckingScreen extends Screen {
    //@ApiStatus.Obsolete
    //private final @Nullable Screen parent;
    private final Runnable onClose;
    //this.parent = parent;
    private final ThreePartsLayoutWidget rootLayout = new ThreePartsLayoutWidget(this);
    private final String info;
    private final Runnable onContinue;
    private final ModsChanges modsChanges;
    private AxisGridWidget footLayout;
    private CheckingListWidget bodyWidget;
    public CheckingScreen(Text title,   /*@Nullable Screen parent,*/Runnable onClose, String info, Runnable onContinue, ModsChanges modsChanges) {
        super(title);
        this.onClose = onClose;
        this.info = info;
        this.onContinue = onContinue;
        this.modsChanges = modsChanges;
        //var bodyText = Text.empty();
        //for (Iterator<Text> iterator = lines.iterator(); iterator.hasNext(); ) {
        //    Text line = iterator.next();
        //    bodyText.append(line);
        //    if (iterator.hasNext()) {
        //        bodyText.append("\n");
        //    }
        //}
    }
    @Override
    public void close() {
        onClose.run();
        //if (client!=null&&parent!=null) {
        //    client.setScreen(parent);
        //}
    }
    @Override
    public void init() {
        super.init();
        rootLayout.addHeader(getTitle(), textRenderer);
        //ScrollableTextWidget body = new ScrollableTextWidget(0, 0, width, 100, bodyText, textRenderer);
        //rootLayout.addBody(body);
        bodyWidget = new CheckingListWidget(client, width, rootLayout.getContentHeight(), rootLayout.getHeaderHeight(), 20,modsChanges);
        rootLayout.addBody(bodyWidget);
        //addDrawableChild(body);
        ButtonWidget continueButton = ButtonWidget.builder(Text.translatable("gui.continue"), button -> onContinue.run()).build();
        //rootLayout.addFooter(continueButton);
        //addDrawableChild(continueButton);
        ButtonWidget copyButton = ButtonWidget.builder(Text.translatable("chat.copy"), button -> client.keyboard.setClipboard(info)).build();
        //rootLayout.addFooter(copyButton);
        //addDrawableChild(copyButton);
        ButtonWidget backButton = ButtonWidget.builder(Text.translatable("gui.back"), button -> close()).build();
        //rootLayout.addFooter(backButton);
        List<ButtonWidget> footButtons = Arrays.asList(continueButton, copyButton, backButton);
        int maxButtonWidth = 0;
        for (ButtonWidget button : footButtons) {
            maxButtonWidth = Math.max(maxButtonWidth, textRenderer.getWidth(button.getMessage()) + 40);
        }
        var footLayout = new AxisGridWidget((maxButtonWidth + 10) * footButtons.size() - 10, height, AxisGridWidget.DisplayAxis.HORIZONTAL);
        this.footLayout = footLayout;
        for (ButtonWidget button : footButtons) {
            button.setWidth(maxButtonWidth);
            footLayout.add(button);
        }
        rootLayout.addFooter(getFootLayout());
        //addDrawableChild(backButton);
        rootLayout.forEachChild(this::addDrawableChild);
        initTabNavigation();
    }
    @Override
    protected void initTabNavigation() {
        rootLayout.refreshPositions();
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    @SuppressWarnings("unchecked")
    public <T extends AxisGridWidget & AWrapperWidget> T getFootLayout() {
        return (T) footLayout;
    }
    public CheckingListWidget getBodyWidget() {
        return bodyWidget;
    }
}
