package phoupraw.mcmod.loadedmodschecker.misc;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public interface FabricUtils {
    @Environment(EnvType.CLIENT)
    BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
    /**
     @see #registerItemStorage(Block, BlockApiLookup.BlockApiProvider)
     */
    Multimap<Block, BlockApiLookup.BlockApiProvider<Storage<ItemVariant>, @Nullable Direction>> ITEM_STORAGE_SIDED_COMBINED_PROVIDERS = MultimapBuilder.hashKeys().arrayListValues().build();
    static List<Triple<FluidVariant, Long, Integer>> toSlottedList(List<? extends StorageView<FluidVariant>> parts) {
        List<Triple<FluidVariant, Long, Integer>> list = new ObjectArrayList<>(parts.size());
        for (var iter = parts.listIterator(); iter.hasNext(); ) {
            int index = iter.nextIndex();
            var part = iter.next();
            if (part.isResourceBlank() || part.getAmount() <= 0) {
                continue;
            }
            list.add(Triple.of(part.getResource(), part.getAmount(), index));
        }
        //while (storage.hasNext()) {
        //    StorageView<FluidVariant> view = storage.next();
        //    list.add(Pair.of(view.getResource(),view.getAmount()));
        //}
        return list;
    }
    static void set(List<Triple<FluidVariant, Long, Integer>> source, List<SingleVariantStorage<FluidVariant>> target) {
        for (SingleVariantStorage<FluidVariant> part : target) {
            part.amount= 0 ;
        }
        for (Triple<FluidVariant, Long, Integer> triple : source) {
            int index = triple.getRight();
            if (index <= 0 || index >= target.size()) {
                continue;
            }
            SingleVariantStorage<FluidVariant> part = target.get(index);
            part.variant = triple.getLeft();
            part.amount = triple.getMiddle();
        }
        //int size = Math.min(source.size(), target.size());
        //for (int i = 0; i < size; i++) {
        //    Pair<FluidVariant, Long> pair = source.get(i);
        //    SingleVariantStorage<FluidVariant> part = target.get(i);
        //    part.variant = pair.getFirst();
        //    part.amount = part.getAmount();
        //}
    }
    static void registerItemStorage(Block block, BlockApiLookup.BlockApiProvider<Storage<ItemVariant>, @Nullable Direction> provider) {
        synchronized (ITEM_STORAGE_SIDED_COMBINED_PROVIDERS) {
            if (!ITEM_STORAGE_SIDED_COMBINED_PROVIDERS.containsKey(block)) {
                ItemStorage.SIDED.registerForBlocks(FabricUtils::find, block);
            }
            ITEM_STORAGE_SIDED_COMBINED_PROVIDERS.put(block, provider);
        }
    }
    @Environment(EnvType.CLIENT)
    @Contract(mutates = "param1")
    static QuadEmitter square(QuadEmitter emitter, Box box, Direction norminalFace) {
        var pair = MCUtils.square(box, norminalFace, new Vec3i(1, 1, 1));
        var rect = pair.getLeft();
        return square(emitter, norminalFace, rect.m00(), rect.m01(), rect.m10(), rect.m11(), pair.getValue());
    }
    @Environment(EnvType.CLIENT)
    @Contract(mutates = "param1")
    static QuadEmitter square(QuadEmitter emitter, Direction norminalFace, double left, double bottom, double right, double top, double depth) {
        return emitter.square(norminalFace, (float) left, (float) bottom, (float) right, (float) top, (float) depth);
    }
    /**
     如果想用{@link EmptyItemFluidStorage#EmptyItemFluidStorage(ContainerItemContext, Item, Fluid, long)}写{@link FluidStorage.CombinedItemApiProvider}但不想引入额外的lambda表达式，可以用此方法。
     
     @param fullItem {@link EmptyItemFluidStorage#EmptyItemFluidStorage(ContainerItemContext, Item, Fluid, long)}的第2个参数
     @param fluid    {@link EmptyItemFluidStorage#EmptyItemFluidStorage(ContainerItemContext, Item, Fluid, long)}的第3个参数
     @param amount   {@link EmptyItemFluidStorage#EmptyItemFluidStorage(ContainerItemContext, Item, Fluid, long)}的第4个参数
     @return 用于在 {@link  FluidStorage#combinedItemApiProvider} 注册的lambda表达式
     @see #registerStorage
     */
    @Contract(pure = true, value = "_,_,_->new")
    static FluidStorage.@NotNull CombinedItemApiProvider emptyProviderOf(Item fullItem, Fluid fluid, long amount) {
        return context -> new EmptyItemFluidStorage(context, fullItem, fluid, amount);
    }
    /**
     如果想用{@link FullItemFluidStorage#FullItemFluidStorage(ContainerItemContext, Item, FluidVariant, long)}写{@link FluidStorage.CombinedItemApiProvider}但不想引入额外的lambda表达式，可以用此方法。
     
     @param emptyItem {@link FullItemFluidStorage#FullItemFluidStorage(ContainerItemContext, Item, FluidVariant, long)}的第2个参数
     @param variant   {@link FullItemFluidStorage#FullItemFluidStorage(ContainerItemContext, Item, FluidVariant, long)}的第3个参数
     @param amount    {@link FullItemFluidStorage#FullItemFluidStorage(ContainerItemContext, Item, FluidVariant, long)}的第4个参数
     @return 用于在 {@link  FluidStorage#combinedItemApiProvider} 注册的lambda表达式
     @see #registerStorage
     */
    @Contract(pure = true, value = "_,_,_->new")
    static FluidStorage.@NotNull CombinedItemApiProvider fullProviderOf(Item emptyItem, FluidVariant variant, long amount) {
        return context -> new FullItemFluidStorage(context, emptyItem, variant, amount);
    }
    /**
     用{@link Items#BUCKET}和{@value FluidConstants#BUCKET}作为{@code emptyItem}和{@code amount}在{@link #registerStorage}注册。
     
     @since 1.0.0
     */
    static void registerBucketStorage(Fluid fluid, Item fullItem) {
        registerStorage(Items.BUCKET, fullItem, fluid, FluidConstants.BUCKET);
    }
    
    /**
     用{@link Items#GLASS_BOTTLE}和{@value FluidConstants#BOTTLE}作为{@code emptyItem}和{@code amount}在{@link #registerStorage}注册。
     */
    static void registerBottleStorage(Fluid fluid, Item fullItem) {
        registerStorage(Items.GLASS_BOTTLE, fullItem, fluid, FluidConstants.BOTTLE);
    }
    
    /**
     用{@link Items#BOWL}和{@value FluidConstants#BUCKET}÷4作为{@code emptyItem}和{@code amount}在{@link #registerStorage}注册。
     */
    static void registerBowlStorage(Fluid fluid, Item fullItem) {
        registerStorage(Items.BOWL, fullItem, fluid, FluidConstants.BUCKET / 4);
    }
    
    /**
     用{@link FluidStorage#combinedItemApiProvider}注册可逆的物品与流体转换，例如：水桶<->水+桶。
     
     @param emptyItem 空的容器物品，例如桶、玻璃瓶、碗等。
     @param fullItem  装满流体的容器物品，例如水桶、蜂蜜瓶、甜菜汤等。
     @param fluid     要被装进容器物品的流体。
     @param amount    一个容器物品可以盛装的流体的量。
     @see #registerBucketStorage
     @see #registerBottleStorage
     @see #registerBowlStorage
     */
    static void registerStorage(Item emptyItem, Item fullItem, Fluid fluid, long amount) {
        FluidStorage.combinedItemApiProvider(emptyItem).register(emptyProviderOf(fullItem, fluid, amount));
        FluidStorage.combinedItemApiProvider(fullItem).register(fullProviderOf(emptyItem, FluidVariant.of(fluid), amount));
    }
    /**
     @see RenderContext.QuadTransform
     @see RenderContext#pushTransform(RenderContext.QuadTransform)
     */
    @Environment(EnvType.CLIENT)
    @Contract(mutates = "param1", value = "_,_->true")
    static boolean rotate(MutableQuadView quad, AffineTransformation rotation) {
        Direction cullFace = quad.cullFace();
        if (cullFace != null) {
            quad.cullFace(Direction.transform(rotation.getMatrix(), cullFace));
        }
        Vector3f vertexPos = new Vector3f();
        for (int i = 0; i < 4; i++) {
            quad.copyPos(i, vertexPos);
            QUAD_FACTORY.transformVertex(vertexPos, rotation);
            quad.pos(i, vertexPos);
        }
        return true;
    }
    
    private static Storage<ItemVariant> find(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction context) {
        var providers = ITEM_STORAGE_SIDED_COMBINED_PROVIDERS.get(state.getBlock());
        List<Storage<ItemVariant>> parts = new ObjectArrayList<>(providers.size());
        for (var provider : providers) {
            var part = provider.find(world, pos, state, blockEntity, context);
            if (part != null) {
                parts.add(part);
            }
        }
        return parts.isEmpty() ? null : new CombinedStorage<>(parts);
    }
}
