package phoupraw.mcmod.loadedmodschecker.misc;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import phoupraw.mcmod.loadedmodschecker.mixin.modmenu.AModListWidget;
import phoupraw.mcmod.loadedmodschecker.mixin.modmenu.AModsScreen;

import java.util.List;

import static phoupraw.mcmod.loadedmodschecker.LoadedModsChecker.ID;

@Environment(EnvType.CLIENT)
public class NewModEntry extends ModVersionEntry {
    public static final String MODMENU = "gui." + ID + ".modmenu";
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
    private static void jumpToModMenu(MinecraftClient client, Screen parent, String modId) {
        var modsScreen = (ModsScreen & AModsScreen) ModMenuApi.createModsScreen(parent);
        client.setScreen(modsScreen);
        if (!selectMod(modsScreen, modId) && !ModMenuConfig.SHOW_LIBRARIES.getValue()) {
            modsScreen.getLibrariesButton().onClick(0, 0);
            selectMod(modsScreen, modId);
        }
    }
    private static boolean selectMod(Screen modsScreen0, String modId) {
        var modsScreen = (ModsScreen & AModsScreen) modsScreen0;
        var modListWidget =(ModListWidget & AModListWidget) modsScreen.getModList();
        for (ModListEntry child : modListWidget.children()) {
            if (child.mod.getId().equals(modId)) {
                modListWidget.select(child);
                modListWidget.invokeCenterScrollOn(child);
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (FabricLoader.getInstance().isModLoaded(ModMenu.MOD_ID)) {
            jumpToModMenu(parent.getClient(), parent.getParent(), getModId());
        }
        return true;
    }
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        if (isMouseOver(mouseX,mouseY)) {
            List<Text > tooltip = new ObjectArrayList<>(2);
            tooltip.add(Text.literal(getModId()).formatted(Formatting.GRAY));
            if (FabricLoader.getInstance().isModLoaded(ModMenu.MOD_ID)) {
                tooltip.add(Text.translatable(MODMENU).formatted(Formatting.GRAY,Formatting.ITALIC));
            }
            context.drawTooltip(parent.getClient().textRenderer, tooltip,mouseX,mouseY);
        }
    }
}
