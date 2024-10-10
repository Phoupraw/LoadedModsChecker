package phoupraw.mcmod.loadedmodschecker.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.Version;
import net.minecraft.text.Text;
@Environment(EnvType.CLIENT)
public class DeletedModEntry extends ModVersionEntry {
    private final String modId;
    private final Version version;
    public DeletedModEntry(CheckingListWidget parent, String modId, Version version) {
        super(parent);
        this.modId = modId;
        this.version = version;
        //leftTextWidget.setMessage(getModName());
        //rightTextWidget.setMessage(getVersionText());
        //adjustTextsWidth();
    }
    @Override
    public Text getModName() {
        return Text.literal(getModId());
    }
    @Override
    public Version getVersion() {
        return version;
    }
    public String getModId() {
        return modId;
    }
}
