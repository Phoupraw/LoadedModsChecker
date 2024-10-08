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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Language;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.loadedmodschecker.misc.CheckingScreen;
import phoupraw.mcmod.loadedmodschecker.misc.JavaUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static phoupraw.mcmod.loadedmodschecker.LoadedModsChecker.ID;
import static phoupraw.mcmod.loadedmodschecker.LoadedModsChecker.LOGGER;

@ApiStatus.Internal
public interface MMIntegratedServerLoader {
    String TITLE = "gui." + ID + ".title";
    String NEW = "gui." + ID + ".new";
    String DELETED = "gui." + ID + ".deleted";
    String UPDATED = "gui." + ID + ".updated";
    String ROLLBACKED = "gui." + ID + ".rollbacked";
    static void checkAndWait(LevelStorage.Session session, Dynamic<?> levelProperties, boolean safeMode, Runnable onCancel, Operation<Void> original, MinecraftClient client) {
        Path path = session.getDirectory(WorldSavePath.ROOT).resolve("serverconfig").resolve(ID + ".mods.json");
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
            Text title = Text.translatable(TITLE, session.getDirectoryName());
            List<Text> messeges = new ObjectArrayList<>();
            var info = new StringJoiner(System.lineSeparator());
            info.add(title.getString());
            if (!newMods.isEmpty()) {
                messeges.add(Text.translatable(NEW).formatted(Formatting.AQUA));
                info.add(Text.translatable(NEW).getString());
                for (Map.Entry<String, Version> entry : newMods.entrySet()) {
                    String modId = entry.getKey();
                    String version = entry.getValue().getFriendlyString();
                    messeges.add(Text.empty()
                      .append(getModName(modId))
                      .append(Text.literal(" : ").formatted(Formatting.DARK_GRAY))
                      .append(version));
                    info.add(modId + " " + version);
                }
            }
            if (!deletedMods.isEmpty()) {
                messeges.add(Text.translatable(DELETED).formatted(Formatting.RED));
                info.add(Text.translatable(DELETED).getString());
                for (Map.Entry<String, Version> entry : deletedMods.entrySet()) {
                    String modId = entry.getKey();
                    String version = entry.getValue().getFriendlyString();
                    messeges.add(Text.empty()
                      .append(getModName(modId))
                      .append(Text.literal(" : ").formatted(Formatting.DARK_GRAY))
                      .append(version));
                    info.add(modId + " " + version);
                }
            }
            if (!updatedMods.isEmpty()) {
                messeges.add(Text.translatable(UPDATED).formatted(Formatting.GREEN));
                info.add(Text.translatable(UPDATED).getString());
                for (var entry : updatedMods.entrySet()) {
                    Pair<Version, Version> pair = entry.getValue();
                    String modId = entry.getKey();
                    String versionPair = pair.left().getFriendlyString() + " -> " + pair.right().getFriendlyString();
                    messeges.add(Text.empty()
                      .append(getModName(modId))
                      .append(Text.literal(" : ").formatted(Formatting.DARK_GRAY))
                      .append(versionPair));
                    info.add(modId + " " + versionPair);
                }
            }
            if (!rollbackedMods.isEmpty()) {
                messeges.add(Text.translatable(ROLLBACKED).formatted(Formatting.GOLD));
                info.add(Text.translatable(ROLLBACKED).getString());
                for (var entry : rollbackedMods.entrySet()) {
                    Pair<Version, Version> pair = entry.getValue();
                    String modId = entry.getKey();
                    String versionPair = pair.left().getFriendlyString() + " -> " + pair.right().getFriendlyString();
                    messeges.add(Text.empty()
                      .append(getModName(modId))
                      .append(Text.literal(" : ").formatted(Formatting.DARK_GRAY))
                      .append(versionPair));
                    info.add(modId + " " + versionPair);
                }
            }
            LOGGER.info(info);
            Screen[] checkingScreen = new Screen[1];
            checkingScreen[0] = new CheckingScreen(title, messeges, ImmutableList.of(
              new DialogScreen.ChoiceButton(Text.translatable("gui.continue"), button -> {
                  checkingScreen[0].close();
                  saveMods(session, levelProperties, safeMode, onCancel, original, path, loadedMods);
              }),
              new DialogScreen.ChoiceButton(Text.translatable("chat.copy"), button -> {
                  client.keyboard.setClipboard(info.toString());
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
    static Text getModName(String modId) {
        MutableText name;
        String key = "modmenu.nameTranslation." + modId;
        if (Language.getInstance().hasTranslation(key)) {
            name = Text.translatable(key);
            //return Text.empty()
            //  .append(Text.translatable(key))
            //  .append(Text.literal(" (" + modId + ")").formatted(Formatting.GRAY));
        } else {
            Optional<ModContainer> mod0 = FabricLoader.getInstance().getModContainer(modId);
            if (mod0.isPresent()) {
                name = Text.literal(mod0.get().getMetadata().getName());
                //return Text.empty()
                //  .append(Text.literal(mod0.get().getMetadata().getName()))
                //  .append(Text.literal(" (" + modId + ")").formatted(Formatting.GRAY));
            } else {
                name = null;
            }
        }
        MutableText copyableName;
        if (name == null) {
            copyableName = Text.literal(modId)/*.fillStyle(name.getStyle()
              .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy"))))*/;
            //return Text.literal(modId);
        } else {
            copyableName = name.append(Text.literal(" (" + modId + ")").formatted(Formatting.GRAY)) /*name.fillStyle(name.getStyle()
              .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text
                .literal(" (" + modId + ")")
                .append("\n")
                .append(Text.translatable("chat.copy")))))*/;
            //return name.fillStyle(name.getStyle()
            //  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(" (" + modId + ")")))
            //  .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, modId)));
        }
        return copyableName/*.fillStyle(copyableName.getStyle()
          .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, modId)))*/;
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
                JsonHelper.writeSorted(writer, jsonLoadedMods, JavaUtils.comparingNothing());
            }
        } catch (IOException e) {
            LOGGER.throwing(e);
        }
    }
    
}
