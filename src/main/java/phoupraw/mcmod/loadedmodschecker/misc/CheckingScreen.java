package phoupraw.mcmod.loadedmodschecker.misc;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.DialogScreen;
import net.minecraft.text.Text;

import java.util.List;

public class CheckingScreen extends DialogScreen {
    //@ApiStatus.Obsolete
    //private final @Nullable Screen parent;
    private final Runnable onClose;
    public CheckingScreen(Text title, List<Text> messages, ImmutableList<ChoiceButton> choiceButtons, /*@Nullable Screen parent,*/Runnable onClose) {
        super(title, messages, choiceButtons);
        this.onClose=onClose;
        //this.parent = parent;
    }
    @Override
    public void close() {
        onClose.run();
        //if (client!=null&&parent!=null) {
        //    client.setScreen(parent);
        //}
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
