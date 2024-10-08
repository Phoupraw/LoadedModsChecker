package phoupraw.mcmod.voxelcake.misc;

import com.google.common.base.Suppliers;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.voxelcake.VoxelCake;
import phoupraw.mcmod.voxelcake.constant.VCComponentTypes;
import phoupraw.mcmod.voxelcake.constant.VCItems;
import phoupraw.mcmod.voxelcake.constant.VCRegistryKeys;
import phoupraw.mcmod.voxelcake.voxel.BakingBlockVoxel;
import phoupraw.mcmod.voxelcake.voxel.BlockVoxel;
import phoupraw.mcmod.voxelcake.voxel.Voxel;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

@UtilityClass
public class VCCommand {
    public static final SetMultimap<Block, RegistryEntry<Voxel>> LOOKUP_HISTORY = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    static {
        CommandRegistrationCallback.EVENT.register(VCCommand::register);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> LOOKUP_HISTORY.clear());
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> LOOKUP_HISTORY.clear());
    }
    public static boolean test(Voxel self, World world, BlockPos pos, BlockState blockState) {
        if (self instanceof BlockVoxel blockVoxel) {
            return blockState.equals(blockVoxel.blockState());
        }
        if (self instanceof BakingBlockVoxel bakingVoxel) {
            return bakingVoxel.predicate().test(new CachedBlockPosition(world, pos, false));
        }
        return false;
    }
    public static @Nullable RegistryEntry<Voxel> getVoxel(ServerWorld world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir()) return null;
        var registryManager = world.getRegistryManager();
        var voxelRegistry = registryManager.get(VCRegistryKeys.VOXEL);
        //RegistryKey<Voxel> voxelKey = null;
        RegistryEntry<Voxel> voxelEntry = null;
        for (RegistryEntry<Voxel> iterEntry : LOOKUP_HISTORY.get(blockState.getBlock())) {
            Voxel voxel = iterEntry.value();
            if (test(voxel, world, pos, blockState)) {
                //voxelKey = iterEntry.getKey().orElseThrow();
                voxelEntry = iterEntry;
                break;
            }
        }
        if (voxelEntry == null) {
            for (var iter = voxelRegistry.streamEntries().iterator(); iter.hasNext(); ) {
                var iterEntry = iter.next();
                Voxel voxel = iterEntry.value();
                if (test(voxel, world, pos, blockState)) {
                    voxelEntry = iterEntry;
                    break;
                }
            }
        }
        return voxelEntry;
    }
    //public static RegistryEntry<Voxel> getVoxel() {
    //
    //}
    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal(VoxelCake.ID)
          .then(CommandManager.literal("give")
            .requires(MCUtils::hasOpLevel)
            .then(CommandManager.argument("from", BlockPosArgumentType.blockPos())
              .then(CommandManager.argument("to", BlockPosArgumentType.blockPos())
                .executes(VCCommand::executeGiveInWorld))))
          .then(CommandManager.literal("save")
            .requires(MCUtils::hasOpLevel)
            .then(CommandManager.literal("mainhand")
              .then(CommandManager.argument("id", IdentifierArgumentType.identifier())
                .executes(VCCommand::executeSaveMainhand)))));
    }
    private static int executeGiveInWorld(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BlockPos from = BlockPosArgumentType.getBlockPos(context, "from");
        BlockPos to = BlockPosArgumentType.getBlockPos(context, "to");
        BlockBox box = BlockBox.create(from, to);
        from = new BlockPos(box.getMinX(), box.getMinY(), box.getMinZ());
        to = new BlockPos(box.getMaxX(), box.getMaxY(), box.getMaxZ());
        Map<BlockPos, RegistryEntry<Voxel>> voxelMap = new Object2ObjectOpenHashMap<>();
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerWorld world = source.getWorld();
        var registryManager = server.getRegistryManager();
        var voxelRegistry = registryManager.get(VCRegistryKeys.VOXEL);
        for (BlockPos pos : BlockPos.iterate(from, to)) {
            //BlockState blockState = world.getBlockState(pos);
            //if (blockState.isAir()) continue;
            //RegistryKey<Voxel> voxelKey = null;
            RegistryEntry<Voxel> voxelEntry = getVoxel(world, pos);
            //for (RegistryEntry<Voxel> iterEntry : LOOKUP_HISTORY.get(blockState.getBlock())) {
            //    Voxel voxel = iterEntry.value();
            //    if (test(voxel, world, pos, blockState)) {
            //        //voxelKey = iterEntry.getKey().orElseThrow();
            //        voxelEntry = iterEntry;
            //        break;
            //    }
            //}
            //if (voxelEntry == null) {
            //    for (var iter = voxelRegistry.streamEntries().iterator(); iter.hasNext(); ) {
            //        var iterEntry = iter.next();
            //        Voxel voxel = iterEntry.value();
            //        if (test(voxel, world, pos, blockState)) {
            //            voxelEntry = iterEntry;
            //            break;
            //        }
            //    }
            //}
            //RegistryKey<BlockVoxel> registryKey = BlockVoxel.get(blockState, registryManager);
            if (voxelEntry != null) {
                voxelMap.put(pos.subtract(from), voxelEntry);
            }
        }
        if (voxelMap.isEmpty()) {
            source.sendError(Text.literal("所选区域的方块全都不是糕点体素！"));
            return 0;
        }
        BlockCake cake = new BlockCake(new BlockPos(box.getDimensions()).add(1, 1, 1), voxelMap);
        RegistryEntry<BlockCake> cakeEntry = BlockCake.intern(cake);
        ItemStack itemStack = VCItems.VOXEL_CAKE.getDefaultStack();
        itemStack.set(VCComponentTypes.CAKE, cakeEntry);
        ServerPlayerEntity player = source.getPlayerOrThrow();
        source.sendFeedback(Suppliers.ofInstance(Text.literal("给予").append(player.getName()).append(itemStack.toHoverableText())), false);
        player.getInventory().offerOrDrop(itemStack);
        return voxelMap.size();
    }
    private static int executeSaveMainhand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof LivingEntity living)) {
            source.sendError(Text.literal("执行者不是生物！"));
            return 0;
        }
        ItemStack stack = living.getMainHandStack();
        if (!stack.isOf(VCItems.VOXEL_CAKE)) {
            source.sendError(Text.literal("主手物品不是体素蛋糕！"));
            return 0;
        }
        RegistryEntry<BlockCake> cakeEntry = stack.getOrDefault(VCComponentTypes.CAKE, BlockCake.EMPTY);
        BlockCake cake = cakeEntry.value();
        if (cake.isEmpty()) {
            source.sendError(Text.literal("蛋糕是空的！"));
            return 0;
        }
        Identifier id = IdentifierArgumentType.getIdentifier(context, "id");
        Path path;
        try {
            path = source.getServer().getSavePath(WorldSavePath.GENERATED)
              .resolve(id.getNamespace())
              .resolve(Cake.PATH_PATH)
              .resolve(id.getPath() + ".json");
        } catch (InvalidPathException e) {
            source.sendError(Text.literal(e.getLocalizedMessage()));
            return 0;
        }
        var result = BlockCake.CODEC.encodeStart(source.getRegistryManager().getOps(JsonOps.INSTANCE), cake);
        if (result.isError()) {
            var error = result.error().orElseThrow();
            source.sendError(Text.literal(error.message()));
        }
        if (result.hasResultOrPartial()) {
            try {
                Files.createDirectories(path.getParent());
                try (var writer = new JsonWriter(new OutputStreamWriter(Files.newOutputStream(path)))) {
                    //writer.setIndent("  ");
                    //writer.setLenient(true);
                    JsonHelper.writeSorted(writer, result.getPartialOrThrow(), Comparator.naturalOrder());
                }
                source.sendFeedback(Suppliers.ofInstance(Text.literal("成功将").append(stack.toHoverableText()).append("序列化至" + path)), true);
                return 1;
            } catch (IOException e) {
                source.sendError(Text.literal(e.getLocalizedMessage()));
                VoxelCake.LOGGER.throwing(e);
                return 0;
            }
        } else {
            return 0;
        }
    }
}
