package phoupraw.mcmod.voxelcake;

import lombok.SneakyThrows;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.block.Blocks;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import phoupraw.mcmod.voxelcake.block.FilledCauldronBlock;
import phoupraw.mcmod.voxelcake.constant.*;
import phoupraw.mcmod.voxelcake.misc.BlockCake;
import phoupraw.mcmod.voxelcake.misc.FabricUtils;
import phoupraw.mcmod.voxelcake.misc.VCCommand;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

import java.lang.invoke.MethodHandles;

public final class VoxelCake implements ModInitializer {
    public static final String ID = "voxelcake";
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger(ID);
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    @Contract(value = " -> new", pure = true)
    public static @NotNull MutableText name() {
        return Text.translatable(NAME_KEY);
    }
    @SneakyThrows
    static void loadClass(Class<?> cls) {
        MethodHandles.lookup().ensureInitialized(cls);
    }
    @Override
    public void onInitialize() {
        loadClass(VCComponentTypes.class);
        loadClass(VCCommand.class);
        loadClass(VCBlockEntityTypes.class);
        loadClass(VCItems.class);
        loadClass(VCVoxelTypes.class);
        DynamicRegistries.registerSynced(VCRegistryKeys.VOXEL, Voxel.CODEC);
        DynamicRegistries.registerSynced(VCRegistryKeys.CAKE, BlockCake.CODEC);
        FabricUtils.registerBucketStorage(VCFluids.CREAM,VCItems.BUCKETED_CREAM);
        FabricUtils.registerBucketStorage(VCFluids.SWEET_BERRY_JAM,VCItems.BUCKETED_SWEET_BERRY_JAM);
        //ItemStorage.SIDED.registerForBlocks(FilledCauldronBlock::find,VCBlocks.FILLED_CAULDRON);
        FabricUtils.registerItemStorage(VCBlocks.FILLED_CAULDRON,FilledCauldronBlock::findFull);
        FabricUtils.registerItemStorage(Blocks.CAULDRON,FilledCauldronBlock::findEmpty);
        
    }
}
