package phoupraw.mcmod.voxelcake.misc;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public interface Cake {
    Codec<BlockBox> BLOCK_BOX_COMPRESSED_CODEC = Codec
      .either(BlockBox.CODEC, BlockPos.CODEC)
      .xmap(Cake::toBlockBox, Cake::toEither);
    PacketCodec<? super RegistryByteBuf, BlockBox> BLOCK_BOX_PACKET_CODEC = PacketCodecs
      .collection((IntFunction<IntList>) IntArrayList::new, PacketCodecs.VAR_INT)
      .xmap(Cake::toBlockBox, Cake::toList);
    PacketCodec<? super RegistryByteBuf, BlockBox> BLOCK_BOX_COMPRESSED_PACKET_CODEC = PacketCodecs
      .either(BLOCK_BOX_PACKET_CODEC, BlockPos.PACKET_CODEC)
      .xmap(Cake::toBlockBox, Cake::toEither);
    static <T> List<? extends T> asToList(Iterable<? extends T> iterable) {
        if (iterable instanceof List<? extends T> list) {
            return list;
        }
        if (iterable instanceof Collection<? extends T> c) {
            return new ObjectArrayList<>(c);
        }
        return new ObjectArrayList<>(iterable.iterator());
    }
    static Box multiplied(Box box, double x, double y, double z) {
        return new Box(box.minX * x, box.minY * y, box.minZ * z, box.maxX * x, box.maxY * y, box.maxZ * z);
    }
    static VoxelShape toShape(BlockPos size, Collection<BlockPos> posSet) {
        int a = size.getX();
        int b = size.getY();
        int c = size.getZ();
        VoxelShape acc = VoxelShapes.empty();
        for (BlockBox blockBox : compressed(posSet)) {
            acc = VoxelShapes.union(acc, VoxelShapes.cuboid(multiplied(Box.from(blockBox), 1d / a, 1d / b, 1d / c)));
        }
        return acc;
        //posSet = posSet.stream()
        //  .sorted(Comparator
        //    .comparingInt(BlockPos::getY)
        //    .thenComparingInt(BlockPos::getX)
        //    .thenComparingInt(BlockPos::getZ))
        //  .collect(Collectors.toCollection(ObjectArrayList::new));
        //VoxelCake.LOGGER.debug("toShape 总共有%d个体素".formatted(posSet.size()));
        //Collection<Box> boxes = new LinkedList<>();
        //BlockPos.Mutable pos = new BlockPos.Mutable();
        //int loopC = 0;
        //while (!posSet.isEmpty()) {
        //    BlockPos start = posSet.iterator().next();
        //    int x1 = start.getX();
        //    int y1 = start.getY();
        //    int z1 = start.getZ();
        //    int x2 = x1 + 1;
        //    int y2 = y1 + 1;
        //    int z2 = z1 + 1;
        //    pos.set(start);
        //    for (int i = x1 + 1; i <= x0; i++) {
        //        loopC++;
        //        pos.setX(i);
        //        if (!posSet.contains(pos)) {
        //            x2 = i;
        //            break;
        //        }
        //    }
        //    pos.set(start);
        //    outer:
        //    for (int i = z1 + 1; i <= z0; i++) {
        //        pos.setZ(i);
        //        for (int j = x1; j < x2; j++) {
        //            loopC++;
        //            pos.setX(j);
        //            if (!posSet.contains(pos)) {
        //                z2 = i;
        //                break outer;
        //            }
        //        }
        //    }
        //    pos.set(start);
        //    outer:
        //    for (int i = y1 + 1; i <= y0; i++) {
        //        pos.setY(i);
        //        for (int j = z1; j < z2; j++) {
        //            pos.setZ(j);
        //            for (int k = x1; k < x2; k++) {
        //                loopC++;
        //                pos.setX(k);
        //                if (!posSet.contains(pos)) {
        //                    y2 = i;
        //                    break outer;
        //                }
        //            }
        //        }
        //    }
        //    for (BlockPos pos1 : BlockPos.iterate(x1, y1, z1, x2 - 1, y2 - 1, z2 - 1)) {
        //        posSet.remove(pos1);
        //    }
        //    boxes.add(new Box((double) x1 / x0, (double) y1 / y0, (double) z1 / z0, (double) x2 / x0, (double) y2 / y0, (double) z2 / z0));
        //}
        //VoxelCake.LOGGER.debug("toShape 循环了%d次".formatted(loopC));
        //VoxelCake.LOGGER.debug("toShape 优化至%d个碰撞箱".formatted(boxes.size()));
        //return boxes.stream().map(VoxelShapes::cuboid).reduce(VoxelShapes.empty(), VoxelShapes::union);
    }
    String PATH = "cake";
    private static BlockBox toBlockBox(Either<BlockBox, BlockPos> either) {
        return either.left().orElse(new BlockBox(either.right().orElse(BlockPos.ORIGIN)));
    }
    private static Either<BlockBox, BlockPos> toEither(BlockBox box) {
        return box.getBlockCountX() == 1 && box.getBlockCountY() == 1 && box.getBlockCountZ() == 1 ? Either.right(box.getCenter()) : Either.left(box);
    }
    private static BlockBox toBlockBox(IntList ints) {
        return new BlockBox(ints.getInt(0), ints.getInt(1), ints.getInt(2), ints.getInt(3), ints.getInt(4), ints.getInt(5));
    }
    private static IntList toList(BlockBox box) {
        return IntArrayList.of(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
    }
    String PATH_PATH = VoxelCake.ID + "/" + PATH;
    static List<BlockBox> compressed(Collection<? extends BlockPos> posSet) {
        posSet = posSet.stream()
          .sorted(MCUtils.VEC3I_COMPARATOR)
          .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));
        List<BlockBox> boxes = new ObjectArrayList<>();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        while (!posSet.isEmpty()) {
            BlockPos start = posSet.iterator().next();
            int x1 = start.getX();
            int y1 = start.getY();
            int z1 = start.getZ();
            int x2;
            int y2;
            int z2;
            pos.set(start);
            for (int i = x1 + 1; ; i++) {
                pos.setX(i);
                if (!posSet.contains(pos)) {
                    x2 = i - 1;
                    break;
                }
            }
            pos.set(start);
            outer:
            for (int i = z1 + 1; ; i++) {
                pos.setZ(i);
                for (int j = x1; j <= x2; j++) {
                    pos.setX(j);
                    if (!posSet.contains(pos)) {
                        z2 = i - 1;
                        break outer;
                    }
                }
            }
            pos.set(start);
            outer:
            for (int i = y1 + 1; ; i++) {
                pos.setY(i);
                for (int j = z1; j <= z2; j++) {
                    pos.setZ(j);
                    for (int k = x1; k <= x2; k++) {
                        pos.setX(k);
                        if (!posSet.contains(pos)) {
                            y2 = i - 1;
                            break outer;
                        }
                    }
                }
            }
            for (BlockPos pos1 : BlockPos.iterate(x1, y1, z1, x2, y2, z2)) {
                posSet.remove(pos1);
            }
            boxes.add(new BlockBox(x1, y1, z1, x2, y2, z2));
        }
        return boxes;
    }
    BlockPos getSize();
    @Nullable Voxel getVoxel(BlockPos pos);
    static @Nullable Text getName(RegistryEntry<? extends Cake> cakeEntry) {
        var key0 = cakeEntry.getKey();
        if (key0.isPresent()) {
            String translationKey = toTranslationKey(key0.get());
            if (Language.getInstance().hasTranslation(translationKey)) {
                return Text.translatable(translationKey);
            }
        }
        return null;
    }
    static String toTranslationKey(RegistryKey<? extends Cake> cakeKey) {
        return cakeKey.getValue().toTranslationKey(VoxelCake.ID + "." + Cake.PATH);
    }
}
