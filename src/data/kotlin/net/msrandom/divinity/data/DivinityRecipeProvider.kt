package net.msrandom.divinity.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.msrandom.divinity.data.recipe.buildBlowMoldRecipes
import net.msrandom.divinity.data.recipe.buildCircuitStamperRecipes
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.item.DivinityMoldItems
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.msrandom.divinity.world.level.block.DivinityGlasswareBlocks
import net.neoforged.neoforge.common.Tags
import java.util.concurrent.CompletableFuture

class DivinityRecipeProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>,
) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput, holderLookup: HolderLookup.Provider) {
        buildBlowMoldRecipes(recipeOutput)
        buildCircuitStamperRecipes(recipeOutput, holderLookup)

        val cooledLiquidInlet = SmithingTransformRecipeBuilder.smithing(
            Ingredient.EMPTY,
            Ingredient.of(DivinityBlocks.bareLiquidInlet),
            Ingredient.of(Tags.Items.INGOTS_COPPER),
            RecipeCategory.TOOLS,
            DivinityBlocks.cooledLiquidInlet.asItem(),
        ).unlocks("has_bare_liquid_inlet", has(DivinityBlocks.bareLiquidInlet))

        cooledLiquidInlet.save(recipeOutput, DivinityBlocks.holder(DivinityBlocks::cooledLiquidInlet).id)

        buildMoldRecipes(recipeOutput)
    }

    private fun buildMoldRecipe(recipeOutput: RecipeOutput, mold: Item) {
        stonecutterResultFromBase(recipeOutput, RecipeCategory.TOOLS, mold, DivinityMoldItems.blankMold)
    }

    private fun buildMoldRecipes(recipeOutput: RecipeOutput) {
        buildMoldRecipe(recipeOutput, DivinityMoldItems.bottleMold)
        buildMoldRecipe(recipeOutput, DivinityMoldItems.paneMold)
        buildMoldRecipe(recipeOutput, DivinityMoldItems.inletMold)

        buildMoldRecipe(recipeOutput, DivinityGlasswareBlocks.funnel.mold)
        buildMoldRecipe(recipeOutput, DivinityGlasswareBlocks.spiral.mold)
        buildMoldRecipe(recipeOutput, DivinityGlasswareBlocks.spitter.mold)
        buildMoldRecipe(recipeOutput, DivinityGlasswareBlocks.decanter.mold)
        buildMoldRecipe(recipeOutput, DivinityGlasswareBlocks.gourd.mold)
    }
}
