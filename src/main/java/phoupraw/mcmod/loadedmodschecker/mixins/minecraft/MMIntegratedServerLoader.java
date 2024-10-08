package phoupraw.mcmod.loadedmodschecker.mixins.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DialogScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.loadedmodschecker.LoadedModsChecker;
import phoupraw.mcmod.loadedmodschecker.misc.CheckingScreen;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static phoupraw.mcmod.loadedmodschecker.LoadedModsChecker.LOGGER;

@ApiStatus.Internal
public interface MMIntegratedServerLoader {
    static void checkAndWait(LevelStorage.Session session, Dynamic<?> levelProperties, boolean safeMode, Runnable onCancel, Operation<Void> original, MinecraftClient client) {
        Path path = session.getDirectory(WorldSavePath.ROOT).resolve("serverconfig").resolve(LoadedModsChecker.ID + ".mods.json");
        Map<String, Version> loadedMods = new Object2ObjectLinkedOpenHashMap<>();
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = modContainer.getMetadata();
            loadedMods.put(metadata.getId(), metadata.getVersion());
        }
        check:
        if (Files.exists(path)) {
            Map<String, Version> lastLoadedMods = read(path);
            if (lastLoadedMods == null) break check;
            Map<String, Version> newMods = new Object2ObjectLinkedOpenHashMap<>();
            Map<String, Version> deletedMods = new Object2ObjectLinkedOpenHashMap<>();
            Map<String, Pair<Version, Version>> updatedMods = new Object2ObjectLinkedOpenHashMap<>();
            Map<String, Pair<Version, Version>> rollbackedMods = new Object2ObjectLinkedOpenHashMap<>();
            //Map<String, Pair<Version,Version>> versionlessMods = new Object2ObjectLinkedOpenHashMap<>();
            for (Map.Entry<String, Version> entry : loadedMods.entrySet()) {
                String modId = entry.getKey();
                Version version = entry.getValue();
                if (!lastLoadedMods.containsKey(modId)) {
                    newMods.put(modId, version);
                } else {
                    Version lastVersion = lastLoadedMods.get(modId);
                    int cmp = version.compareTo(lastVersion);
                    if (cmp < 0) {
                        rollbackedMods.put(modId, Pair.of(lastVersion, version));
                    } else if (cmp > 0) {
                        updatedMods.put(modId, Pair.of(lastVersion, version));
                    }
                    lastLoadedMods.remove(modId);
                }
            }
            deletedMods.putAll(lastLoadedMods);
            if (newMods.isEmpty() && deletedMods.isEmpty() && updatedMods.isEmpty() && rollbackedMods.isEmpty()) {
                original.call(session, levelProperties, safeMode, onCancel);
                break check;
            }
            Text title = Text.literal("当前加载的模组与上次加载的模组有所不同");
            List<Text> messeges = new ObjectArrayList<>();
            if (!newMods.isEmpty()) {
                messeges.add(Text.literal("新增模组：").formatted(Formatting.BLUE));
                for (Map.Entry<String, Version> entry : newMods.entrySet()) {
                    messeges.add(Text.literal(entry.getKey() + " : " + entry.getValue().getFriendlyString()));
                }
            }
            if (!deletedMods.isEmpty()) {
                messeges.add(Text.literal("移除模组：").formatted(Formatting.RED));
                for (Map.Entry<String, Version> entry : deletedMods.entrySet()) {
                    messeges.add(Text.literal(entry.getKey() + " : " + entry.getValue().getFriendlyString()));
                }
            }
            if (!updatedMods.isEmpty()) {
                messeges.add(Text.literal("更新模组：").formatted(Formatting.AQUA));
                for (var entry : updatedMods.entrySet()) {
                    Pair<Version, Version> pair = entry.getValue();
                    messeges.add(Text.literal(entry.getKey() + " : " + pair.left().getFriendlyString() + " -> " + pair.right().getFriendlyString()));
                }
            }
            if (!rollbackedMods.isEmpty()) {
                messeges.add(Text.literal("回退模组：").formatted(Formatting.GOLD));
                for (var entry : rollbackedMods.entrySet()) {
                    Pair<Version, Version> pair = entry.getValue();
                    messeges.add(Text.literal(entry.getKey() + " : " + pair.left().getFriendlyString() + " -> " + pair.right().getFriendlyString()));
                }
            }
            Screen[] checkingScreen = new Screen[1];
            checkingScreen[0] = new CheckingScreen(title, messeges, ImmutableList.of(
              new DialogScreen.ChoiceButton(Text.translatable("gui.continue"), button -> {
                  checkingScreen[0].close();
                  saveMods(session, levelProperties, safeMode, onCancel,original,path,loadedMods);
              }),
              new DialogScreen.ChoiceButton(Text.translatable("gui.back"), button -> {
                  checkingScreen[0].close();
                  session.tryClose();
                  onCancel.run();
              })
            ), client.currentScreen);
            client.setScreen(checkingScreen[0]);
        } else {
            saveMods(session, levelProperties, safeMode, onCancel, original, path, loadedMods);
        }
    }
    private static @Nullable Map<String, Version> read(Path path) {
        Map<String, Version> lastLoadedMods;
        try (var reader = new InputStreamReader(Files.newInputStream(path))) {
            JsonObject jsonLoadedMods = JsonHelper.deserialize(reader);
            lastLoadedMods = new Object2ObjectLinkedOpenHashMap<>(jsonLoadedMods.size());
            for (Map.Entry<String, JsonElement> jsonEntry : jsonLoadedMods.entrySet()) {
                String modId = jsonEntry.getKey();
                Version version;
                try {
                    version = Version.parse(jsonEntry.getValue().getAsString());
                } catch (VersionParsingException | UnsupportedOperationException | IllegalStateException e) {
                    LOGGER.throwing(e);
                    continue;
                }
                lastLoadedMods.put(modId, version);
            }
        } catch (IOException e) {
            LOGGER.throwing(e);
            return null;
        }
        return lastLoadedMods;
    }
    private static void saveMods(LevelStorage.Session session, Dynamic<?> levelProperties, boolean safeMode, Runnable onCancel, Operation<Void> original, Path path, Map<String, Version> loadedMods) {
        write(path, loadedMods);
        original.call(session, levelProperties, safeMode, onCancel);
    }
    private static void write(Path path, Map<String, Version> loadedMods) {
        try {
            Files.createDirectories(path.getParent());
            try (var writer = new JsonWriter(new OutputStreamWriter(Files.newOutputStream(path)))) {
                JsonObject jsonLoadedMods = new JsonObject();
                for (Map.Entry<String, Version> entry : loadedMods.entrySet()) {
                    jsonLoadedMods.addProperty(entry.getKey(), entry.getValue().getFriendlyString());
                }
                JsonHelper.writeSorted(writer, jsonLoadedMods, Comparator.naturalOrder());
            }
        } catch (IOException e) {
            LOGGER.throwing(e);
        }
    }
    
}
