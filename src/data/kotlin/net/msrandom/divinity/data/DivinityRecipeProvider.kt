package net.msrandom.divinity.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import java.util.concurrent.CompletableFuture

class DivinityRecipeProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>,
) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput, holderLookup: HolderLookup.Provider) {
        buildBlowMoldRecipes(recipeOutput)
        buildCircuitStamperRecipes(recipeOutput, holderLookup)
    }
}
