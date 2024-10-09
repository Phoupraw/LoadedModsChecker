package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class VersionPairEntry extends NewModEntry {
    private final Version version;
    public VersionPairEntry(CheckingListWidget parent, ModContainer modContainer, Version version) {
        super(parent, modContainer);
        this.version = version;
    }
    @Override
    public Text getVersionText() {
        return Text.empty().append(version.getFriendlyString()).append(Text.literal(" -> ").formatted(Formatting.GRAY)).append(super.getVersionText());
    }
}
