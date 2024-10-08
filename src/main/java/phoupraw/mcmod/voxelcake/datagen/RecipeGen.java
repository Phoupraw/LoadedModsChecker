package phoupraw.mcmod.voxelcake.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.voxelcake.constant.VCItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

final class RecipeGen extends FabricRecipeProvider {
    RecipeGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    @Override
    public void generate(RecipeExporter exporter) {
        offerSmelting(exporter, List.of(Items.DRIED_KELP), RecipeCategory.FOOD, VCItems.KELP_ASH, 0.2f, 200, null);
        offerBlasting(exporter, List.of(Items.DRIED_KELP), RecipeCategory.FOOD, VCItems.KELP_ASH, 0.2f, 100, null);
    }
}
