package phoupraw.mcmod.voxelcake.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.apache.commons.lang3.tuple.Triple;

public class MapTripleCodec<L, M, R> implements Codec<Triple<L, M, R>> {
    private final Codec<L> left;
    private final Codec<M> middle;
    private final Codec<R> right;
    public MapTripleCodec(Codec<L> left, Codec<M> middle, Codec<R> right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
    @Override
    public <T> DataResult<Pair<Triple<L, M, R>, T>> decode(DynamicOps<T> ops, T input) {
        return left.decode(ops, input).flatMap(r1 ->
          middle.decode(ops, r1.getSecond()).flatMap(r2 ->
            right.decode(ops, r2.getSecond()).map(r3 ->
              Pair.of(Triple.of(r1.getFirst(), r2.getFirst(), r3.getFirst()), r3.getSecond()))));
    }
    @Override
    public <T> DataResult<T> encode(Triple<L, M, R> input, DynamicOps<T> ops, T prefix) {
        return right.encode(input.getRight(), ops, prefix).flatMap(f1 ->
          middle.encode(input.getMiddle(), ops, f1).flatMap(f2 ->
            left.encode(input.getLeft(), ops, f2)));
    }
}
