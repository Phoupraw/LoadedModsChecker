package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CheckingScreen extends Screen {
    private final ThreePartsLayoutWidget rootLayout = new ThreePartsLayoutWidget(this);
    private final String info;
    private final ModsChanges modsChanges;
    private CheckingListWidget bodyWidget;
    public CheckingScreen(Text title, String info, ModsChanges modsChanges) {
        super(title);
        this.info = info;
        this.modsChanges = modsChanges;
    }
    @Override
    public void init() {
        super.init();
        rootLayout.addHeader(getTitle(), textRenderer);
        ButtonWidget continueButton = ButtonWidget.builder(Text.translatable("gui.continue"), this::onClickContinue).build();
        ButtonWidget copyButton = ButtonWidget.builder(Text.translatable("chat.copy"), this::onClickCopy).build();
        ButtonWidget backButton = ButtonWidget.builder(Text.translatable("gui.back"), this::onClickBack).build();
        bodyWidget = new CheckingListWidget(client, width, rootLayout.getContentHeight(), rootLayout.getHeaderHeight(), textRenderer.fontHeight + 4, modsChanges, this);
        rootLayout.addBody(bodyWidget);
        List<ButtonWidget> footButtons = Arrays.asList(continueButton, copyButton, backButton);
        int maxButtonWidth = 0;
        for (ButtonWidget button : footButtons) {
            maxButtonWidth = Math.max(maxButtonWidth, textRenderer.getWidth(button.getMessage()) + 40);
        }
        var footLayout = new DirectionalLayoutWidget(0, 0, DirectionalLayoutWidget.DisplayAxis.HORIZONTAL).spacing(8);
        for (ButtonWidget button : footButtons) {
            button.setWidth(maxButtonWidth);
            footLayout.add(button);
        }
        rootLayout.addFooter(footLayout);
        rootLayout.forEachChild(this::addDrawableChild);
        initTabNavigation();
    }
    @Override
    protected void initTabNavigation() {
        getBodyWidget().setWidth(width);
        getBodyWidget().setHeight(rootLayout.getContentHeight());
        rootLayout.refreshPositions();
    }
    public CheckingListWidget getBodyWidget() {
        return bodyWidget;
    }
    @ApiStatus.OverrideOnly
    protected void onClickContinue(ButtonWidget button) {
    
    }
    @ApiStatus.OverrideOnly
    private void onClickBack(ButtonWidget button) {
        close();
    }
    @ApiStatus.OverrideOnly
    private void onClickCopy(ButtonWidget button) {
        client.keyboard.setClipboard(info);
    }
}
