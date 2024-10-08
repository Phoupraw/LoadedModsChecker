package phoupraw.mcmod.voxelcake.misc;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import phoupraw.mcmod.voxelcake.block.VoxelCakeBlock;
import phoupraw.mcmod.voxelcake.constant.VCRegistryKeys;
import phoupraw.mcmod.voxelcake.model.VoxelCakeModel;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@Unmodifiable
public class BlockCake implements Cake {
    public static final Codec<BlockCake> CODEC = RecordCodecBuilder.build(RecordCodecBuilder.<BlockCake>instance()
      .group(
        BlockPos.CODEC.fieldOf("size").forGetter(BlockCake::getSize),
        Codec.unboundedMap(Voxel.ENTRY_CODEC, Cake.BLOCK_BOX_COMPRESSED_CODEC.listOf())
          .xmap(BlockCake::decompressed, BlockCake::compressed)
          .fieldOf("voxels")
          .forGetter(BlockCake::getVoxelMap))
      .apply(RecordCodecBuilder.instance(), BlockCake::new)).codec();
    public static final Codec<RegistryEntry<BlockCake>> ENTRY_CODEC = RegistryElementCodec.of(VCRegistryKeys.CAKE, CODEC);
    public static final PacketCodec<? super RegistryByteBuf, BlockCake> PACKET_CODEC = PacketCodec.tuple(
      BlockPos.PACKET_CODEC,
      BlockCake::getSize,
      PacketCodecs
        .map(
          (IntFunction<Map<RegistryEntry<Voxel>, List<BlockBox>>>) Object2ObjectOpenHashMap::new,
          PacketCodecs.registryEntry(VCRegistryKeys.VOXEL),
          PacketCodecs.collection(ObjectArrayList::new, Cake.BLOCK_BOX_COMPRESSED_PACKET_CODEC)
        )
        .xmap(BlockCake::decompressed, BlockCake::compressed),
      BlockCake::getVoxelMap,
      BlockCake::new
    );
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<BlockCake>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(VCRegistryKeys.CAKE, PACKET_CODEC);
    public static final Map<BlockCake, RegistryEntry<BlockCake>> INTERNER = new WeakHashMap<>();
    public static final RegistryEntry<BlockCake> EMPTY = intern(new BlockCake(BlockPos.ORIGIN, Map.of()));
    public static final Map<BlockCake, Map<Direction, VoxelShape>> SHAPE_CACHE = new Object2ObjectOpenHashMap<>();
    static {
        DynamicRegistrySetupCallback.EVENT.register(BlockCake::onRegistrySetup);
    }
    public static RegistryEntry<BlockCake> intern(@NotNull RegistryEntry<BlockCake> self) {
        RegistryEntry<BlockCake> cakeEntry = INTERNER.get(self.value());
        if (cakeEntry != null) {
            return cakeEntry;
        }
        INTERNER.put(self.value(), self);
        return self;
    }
    public static RegistryEntry<BlockCake> intern(@NotNull BlockCake self) {
        RegistryEntry<BlockCake> cakeEntry = INTERNER.get(self);
        if (cakeEntry != null) {
            return cakeEntry;
        }
        cakeEntry = RegistryEntry.of(self);
        INTERNER.put(self, cakeEntry);
        return cakeEntry;
    }
    public static @NotNull VoxelShape getShape(BlockCake cake, Direction facing) {
        if (cake.isEmpty()) return VoxelCakeBlock.SHAPE;
        Map<Direction, VoxelShape> map = SHAPE_CACHE.get(cake);
        if (map == null) {
            synchronized (SHAPE_CACHE) {
                map = SHAPE_CACHE.get(cake);
                if (map == null) {
                    map = new EnumMap<>(Direction.class);
                    SHAPE_CACHE.put(cake, map);
                }
            }
        }
        VoxelShape shape = map.get(facing);
        if (shape == null) {
            //synchronized (map) {
            //    shape = map.get(facing);
            //    if (shape==null) {
            if (facing == Direction.NORTH) {
                shape = Cake.toShape(cake.getSize(), cake.getVoxelMap().keySet());
            } else {
                var northShape =getShape(cake,Direction.NORTH);
                shape = MCUtils.rotatedY(northShape,Direction.NORTH,facing);
            }
            map.put(facing, shape);
            //    }
            //}
        }
        return shape;
    }
    private static Map<BlockPos, RegistryEntry<Voxel>> decompressed(Map<? extends RegistryEntry<Voxel>, ? extends Iterable<BlockBox>> compressed) {
        Map<BlockPos, RegistryEntry<Voxel>> voxelMap = new Object2ObjectOpenHashMap<>();
        for (var entry : compressed.entrySet()) {
           var regEntry = entry.getKey();
            var blockBoxes = entry.getValue();
            for (BlockBox blockBox : blockBoxes) {
                for (int i = blockBox.getMinX(); i <= blockBox.getMaxX(); i++) {
                    for (int j = blockBox.getMinY(); j <= blockBox.getMaxY(); j++) {
                        for (int k = blockBox.getMinZ(); k <= blockBox.getMaxZ(); k++) {
                            voxelMap.put(new BlockPos(i, j, k), regEntry);
                        }
                    }
                }
            }
        }
        return voxelMap;
    }
    private static Map<RegistryEntry<Voxel>, List<BlockBox>> compressed(Map<? extends BlockPos, ? extends RegistryEntry<Voxel>> voxelMap) {
        return Maps.transformValues(Multimaps.asMap(JavaUtils.reversed(voxelMap)), (Cake::compressed));
    }
    private static void onRegistrySetup(DynamicRegistryView registryView) {
        var registry0 = registryView.getOptional(VCRegistryKeys.CAKE);
        if (registry0.isEmpty()) return;
        var registry = registry0.get();
        INTERNER.clear();
        RegistryEntryAddedCallback.event(registry).register((rawId, id, object) -> {
            INTERNER.put(object, registry.getEntry(rawId).orElseThrow());
        });
    }
    @Getter
    private final BlockPos size;
    @Getter
    private final Map<BlockPos, RegistryEntry<Voxel>> voxelMap;
    private int hashCode;
    @Environment(EnvType.CLIENT)
    private BakedModel model;
    public BlockCake(BlockPos size, Map<BlockPos, RegistryEntry<Voxel>> voxelMap) {
        this.size = size;
        this.voxelMap = voxelMap;
        if (!isEmpty()) {
            hashCode =  voxelMap.hashCode();
        }
    }
    @Override
    public @Nullable Voxel getVoxel(BlockPos pos) {
        var regEntry = voxelMap.get(pos);
        return regEntry == null ? null : regEntry.value();
    }
    @Override
    public int hashCode() {
        //if (hashCode == null) {
        //    hashCode = getVoxelMap().hashCode();
        //}
        return hashCode;
    }
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof BlockCake cake &&(isEmpty() && cake.isEmpty() || getSize().equals(cake.getSize()) && getVoxelMap().equals(cake.getVoxelMap()));
    }
    @Environment(EnvType.CLIENT)
    public BakedModel getModel(Supplier<Random> randomSupplier) {
        if (model == null) {
            this.model = VoxelCakeModel.toModel(this, randomSupplier);
        }
        return model;
    }
    public boolean isEmpty() {
        return getSize().equals(BlockPos.ORIGIN) || getVoxelMap().isEmpty();
    }
}
