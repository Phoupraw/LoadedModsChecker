package phoupraw.mcmod.loadedmodschecker.constant;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.loadedmodschecker.LoadedModsChecker;

@ApiStatus.NonExtendable
public interface VCIDs {
    static Identifier of(String path) {
        return Identifier.of(LoadedModsChecker.ID, path);
    }
}
