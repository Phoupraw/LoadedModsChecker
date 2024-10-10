package phoupraw.mcmod.loadedmodschecker.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Dynamic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import phoupraw.mcmod.loadedmodschecker.mixins.minecraft.MMIntegratedServerLoader;

@Environment(EnvType.CLIENT)
@Mixin(IntegratedServerLoader.class)
abstract class MIntegratedServerLoader {
    @Shadow
    @Final
    private MinecraftClient client;
    @WrapMethod(method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lcom/mojang/serialization/Dynamic;ZLjava/lang/Runnable;)V")
    private void checkAndWait(LevelStorage.Session session, Dynamic<?> levelProperties, boolean safeMode, Runnable onCancel, Operation<Void> original) {
        MMIntegratedServerLoader.checkAndWait(session, levelProperties, safeMode, onCancel, original, this.client);
    }
}
