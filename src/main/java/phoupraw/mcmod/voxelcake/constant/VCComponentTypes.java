package phoupraw.mcmod.voxelcake.constant;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.misc.BlockCake;

@ApiStatus.NonExtendable
public interface VCComponentTypes {
    ComponentType<RegistryEntry<BlockCake>> CAKE = of(VCIDs.CAKE,BlockCake.ENTRY_CODEC, BlockCake.ENTRY_PACKET_CODEC);
    private static <T> ComponentType<T> r(Identifier id, ComponentType<T> value) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, value);
    }
    private static <T> ComponentType<T> of(Identifier id, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ComponentType.<T>builder()
          .codec(codec)
          .packetCodec(packetCodec)
          .build());
    }
}
