package phoupraw.mcmod.voxelcake.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class, priority = 10000)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    protected abstract void sendMovementPackets();
    @Shadow
    @Final
    protected MinecraftClient client;
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
}
