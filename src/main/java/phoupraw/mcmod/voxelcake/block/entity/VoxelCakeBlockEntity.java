package phoupraw.mcmod.voxelcake.block.entity;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.block.VoxelCakeBlock;
import phoupraw.mcmod.voxelcake.constant.VCBlockEntityTypes;
import phoupraw.mcmod.voxelcake.constant.VCComponentTypes;
import phoupraw.mcmod.voxelcake.misc.BlockCake;
import phoupraw.mcmod.voxelcake.misc.Cake;

public class VoxelCakeBlockEntity extends NameableBlockEntity {
    @Deprecated
    public VoxelShape shape = VoxelCakeBlock.SHAPE;
    @Getter
    private @NotNull RegistryEntry<BlockCake> cakeEntry = BlockCake.EMPTY;
    public VoxelCakeBlockEntity(BlockPos pos, BlockState state) {
        this(VCBlockEntityTypes.VOXEL_CAKE, pos, state);
    }
    public VoxelCakeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    public @NotNull BlockCake getRenderData() {
        RegistryEntry<BlockCake> cakeEntry = getCakeEntry();
        return /*cakeEntry == null ? null :*/ cakeEntry.value();
    }
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("cake")) {
            var nbtCake = nbt.get("cake");
            var result = BlockCake.ENTRY_CODEC.decode(registryLookup.getOps(NbtOps.INSTANCE), nbtCake);
            if (result.hasResultOrPartial()) {
                setCakeEntry(result.getPartialOrThrow().getFirst());
            }
            if (result.isError()) {
                VoxelCake.LOGGER.error("{} {}", getPos().toShortString(), result.error().orElseThrow());
            }
        } else {
            setCakeEntry(BlockCake.EMPTY);
        }
    }
    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        var cakeEntry = getCakeEntry();
        if (!cakeEntry.value().isEmpty()) {
            var result = BlockCake.ENTRY_CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), cakeEntry);
            if (result.hasResultOrPartial()) {
                nbt.put("cake", result.getPartialOrThrow());
            }
            if (result.isError()) {
                VoxelCake.LOGGER.error("{} {}", getPos().toShortString(), result.error().orElseThrow());
            }
        }
    }
    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        setCakeEntry(components.getOrDefault(VCComponentTypes.CAKE, BlockCake.EMPTY));
    }
    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        var cakeEntry = getCakeEntry();
        builder.add(VCComponentTypes.CAKE, cakeEntry);
    }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = createNbt(registryLookup);
        if (nbt.isEmpty()) {
            nbt.putByte("0", (byte) 0);
        }
        return nbt;
    }
    @Override
    public @NotNull Text getName() {
        {
            var name = getCustomName();
            if (name != null) return name;
        }
        {
            var name = Cake.getName(getCakeEntry());
            if (name != null) return name;
        }
        return getCachedState().getBlock().getName();
    }
    public void setCakeEntry(@NotNull RegistryEntry<BlockCake> cakeEntry) {
        this.cakeEntry = BlockCake.intern(cakeEntry);
        markDirty();
        //BlockCake cake = cakeEntry.value();
        //shape=Cake.toShape(cake.getSize(), cake.getVoxelMap().keySet());
        World world = getWorld();
        if (world != null /*&& world.isClient()*/) {
            if (cakeEntry.value().isEmpty()) {
                world.removeBlock(getPos(), false);
            } else if (world.isClient()){
                world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
            }
        }
    }
}
