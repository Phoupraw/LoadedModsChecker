package phoupraw.mcmod.voxelcake.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.constant.VCBlockEntityTypes;
import phoupraw.mcmod.voxelcake.misc.FabricUtils;

import java.util.List;

public class ManualBasinBlockEntity extends NameableBlockEntity implements SidedStorageBlockEntity, Clearable {
    private final SimpleInventory inventory = new SimpleInventory(4);
    private final SlottedStorage<ItemVariant> itemStorage = InventoryStorage.of(inventory, null);
    private final CombinedSlottedStorage<FluidVariant, SingleVariantStorage<FluidVariant>> fluidStorage;
    {
        inventory.addListener(inv -> updateListeners());
    }
    {
        List<SingleVariantStorage<FluidVariant>> parts = new ObjectArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            parts.add(SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::updateListeners));
        }
        fluidStorage = new CombinedSlottedStorage<>(parts);
    }
    public ManualBasinBlockEntity(BlockPos pos, BlockState state) {
        this(VCBlockEntityTypes.MANUAL_BASIN, pos, state);
    }
    public ManualBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @ApiStatus.OverrideOnly
    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return fluidStorage;
    }
    @ApiStatus.OverrideOnly
    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return itemStorage;
    }
    @Override
    public void clear() {
        inventory.clear();
        for (SingleVariantStorage<FluidVariant> part : fluidStorage.parts) {
            part.amount = 0;
        }
    }
    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @MustBeInvokedByOverriders
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory.getHeldStacks(), false, registryLookup);
        if (fluidStorage.nonEmptyIterator().hasNext()) {
            nbt.put("fluids", FabricUtils.SLOTTED_FLUID_STORAGE_CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), FabricUtils.toSlottedList(fluidStorage.parts)).getPartialOrThrow());
        }
    }
    @MustBeInvokedByOverriders
    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(inventory.getHeldStacks());
        //TODO fluidStorage
    }
    @MustBeInvokedByOverriders
    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory.getHeldStacks()));
        //TODO fluidStorage
    }
    @Override
    public @Nullable Object getRenderData() {
        return getInventory();
    }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = createNbt(registryLookup);
        if (nbt.isEmpty()) {
            nbt.putByte("0", (byte) 0);
        }
        return nbt;
    }
    public Inventory getInventory() {
        return inventory;
    }
    @MustBeInvokedByOverriders
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        getInventory().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registryLookup);
        if (nbt.contains("fluids")) {
            FabricUtils.set(FabricUtils.SLOTTED_FLUID_STORAGE_CODEC.decode(registryLookup.getOps(NbtOps.INSTANCE), nbt.get("fluids")).getPartialOrThrow().getFirst(), fluidStorage.parts);
        } else {
            FabricUtils.set(List.of(), fluidStorage.parts);
        }
        World world = getWorld();
        if (world.isClient()) {
            world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
        }
    }
    public void updateListeners() {
        markDirty();
        World world = getWorld();
        BlockState cachedState = this.getCachedState();
        BlockPos pos = this.getPos();
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(cachedState));
        world.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_ALL);
    }
}
