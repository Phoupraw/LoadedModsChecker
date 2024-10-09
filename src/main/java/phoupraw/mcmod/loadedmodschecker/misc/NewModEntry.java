package phoupraw.mcmod.loadedmodschecker.misc;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraft.text.Text;
import phoupraw.mcmod.loadedmodschecker.mixin.modmenu.AModsScreen;

public class NewModEntry extends ModVersionEntry {
    private final ModContainer modContainer;
    public NewModEntry(CheckingListWidget parent, ModContainer modContainer) {
        super(parent);
        this.modContainer = modContainer;
    }
    @Override
    public Version getVersion() {
        return modContainer.getMetadata().getVersion();
    }
    //@Override
    //public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    //    ModMetadata metadata = modContainer.getMetadata();
    //    Text modName = FabricUtils.getModName(metadata.getId());
    //    var versionText = Text.literal(metadata.getVersion().getFriendlyString());
    //    context.drawText(textRenderer, modName, x + entryWidth / 2 - textRenderer.getWidth(modName), y, -1, false);
    //    context.drawText(textRenderer, versionText, x + entryWidth / 2, y, -1, false);
    //}
    @Override
    public Text getModName() {
        return FabricUtils.getModName(getModId());
    }
    @Override
    public String getModId() {
        return modContainer.getMetadata().getId();
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (FabricLoader.getInstance().isModLoaded(ModMenu.MOD_ID)) {
            var modsScreen = (ModsScreen & AModsScreen) ModMenuApi.createModsScreen(parent.getParent());
            parent.getClient().setScreen(modsScreen);
            boolean found = false;
            for (ModListEntry child : modsScreen.getModList().children()) {
                if (child.mod.getId().equals(getModId())) {
                    modsScreen.getModList().select(child);
                    found = true;
                    break;
                }
            }
            if (!found && !ModMenuConfig.SHOW_LIBRARIES.getValue()) {
                modsScreen.getLibrariesButton().onClick(mouseX,mouseY);
                for (ModListEntry child : modsScreen.getModList().children()) {
                    if (child.mod.getId().equals(getModId())) {
                        modsScreen.getModList().select(child);
                        break;
                    }
                }
            }
        }
        return true;
    }
}
