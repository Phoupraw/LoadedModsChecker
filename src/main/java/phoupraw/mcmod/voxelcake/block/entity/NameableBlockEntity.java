package phoupraw.mcmod.voxelcake.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NameableBlockEntity extends BlockEntity implements Nameable {
    public static final String KEY = "CustomName";
    protected @Nullable Text customName;
    public NameableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    public @NotNull Text getName() {
        Text customName = getCustomName();
        return customName != null ? customName : getCachedState().getBlock().getName();
    }
    public @Nullable Text getCustomName() {
        return this.customName;
    }
    public void setCustomName(@Nullable Text customName) {
        boolean changed = !Objects.equals(getCustomName(), customName);
        this.customName = customName;
        if (changed) {
            markDirty();
        }
    }
    @MustBeInvokedByOverriders
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(KEY, NbtElement.STRING_TYPE)) {
            setCustomName(tryParseCustomName(nbt.getString(KEY), registryLookup));
        } else {
            setCustomName(null);
        }
    }
    @MustBeInvokedByOverriders
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Text customName = getCustomName();
        if (customName != null) {
            nbt.putString(KEY, Text.Serialization.toJsonString(customName, registryLookup));
        }
    }
    @MustBeInvokedByOverriders
    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        setCustomName(components.get(DataComponentTypes.CUSTOM_NAME));
    }
    @MustBeInvokedByOverriders
    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        if (getCustomName() != null) {
            builder.add(DataComponentTypes.CUSTOM_NAME, getCustomName());
        }
    }
}
