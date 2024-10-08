package phoupraw.mcmod.loadedmodschecker.misc;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.DialogScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CheckingScreen extends DialogScreen {
    private final @Nullable Screen parent;
    public CheckingScreen(Text title, List<Text> messages, ImmutableList<ChoiceButton> choiceButtons, @Nullable Screen parent) {
        super(title, messages, choiceButtons);
        this.parent = parent;
    }
    @Override
    public void close() {
        if (client!=null&&parent!=null) {
            client.setScreen(parent);
        }
    }
}
