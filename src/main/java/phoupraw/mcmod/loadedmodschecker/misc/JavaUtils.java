package phoupraw.mcmod.loadedmodschecker.misc;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface JavaUtils {
    @Contract(pure = true)
    static <T> T getOrNull(Optional<T> o) {
        return o.orElse(null);
    }
    @Contract(pure = true)
    static <K, V> Multimap<V, K> reversed(Map<? extends K, ? extends V> self) {
        Multimap<V, K> reversed = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        for (var entry : self.entrySet()) {
            reversed.put(entry.getValue(), entry.getKey());
        }
        return reversed;
    }
    /**
     @see Comparator#compare
     */
    static <T> int compareNothing(T o1, T o2) {
        return 0;
    }
    static <T> Comparator<T> comparingNothing() {
        return JavaUtils::compareNothing;
    }
}
