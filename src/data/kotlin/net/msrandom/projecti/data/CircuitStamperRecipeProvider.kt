package net.msrandom.projecti.data

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.msrandom.projecti.world.item.ProjectIItems
import net.msrandom.projecti.world.item.crafting.CircuitStamperRecipe
import net.msrandom.projecti.world.item.crafting.ProjectIRecipeSerializers
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import java.util.concurrent.CompletableFuture

class CircuitStamperRecipeProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>,
) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput, holderLookup: HolderLookup.Provider) {
        val circuitStamperPath = BuiltInRegistries.RECIPE_SERIALIZER.getKey(ProjectIRecipeSerializers.circuitStamper)!!.path

        fun add(recipe: CircuitStamperRecipe) {
            val key = BuiltInRegistries.ITEM.getKey(recipe.getResultItem(holderLookup).item)
            recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(key.namespace, "$circuitStamperPath/${key.path}"), recipe, null)
        }

        val soulbone = Ingredient.of(ProjectIItems.soulbone)
        val boneConduit = Ingredient.of(ProjectIItems.soulboneConduit)
        val blueCrystal = Ingredient.of(ProjectIBlocks.blueCrystal)
        val boneSplinters = Ingredient.of(ProjectIItems.soulboneSplinters)
        val controlCircuit = Ingredient.of(ProjectIItems.controlCircuit)
        val emitter = Ingredient.of(ProjectIItems.emitter)
        val yellowCrystal = Ingredient.of(ProjectIItems.yellowCrystal)
        val blueGem = Ingredient.of(ProjectIItems.blueGem)
        val redstone = Ingredient.of(Items.REDSTONE)
        val stone = Ingredient.of(Items.STONE)

        add(
            CircuitStamperRecipe(
                blueCrystal,
                soulbone,
                soulbone,
                redstone,
                ProjectIItems.knowledgeGem.defaultInstance,
            )
        )

        add(
            CircuitStamperRecipe(
                stone,
                stone,
                stone,
                stone,
                ItemStack(ProjectIItems.stoneRing, 4),
            ),
        )

        add(
            CircuitStamperRecipe(
                soulbone,
                Ingredient.EMPTY,
                Ingredient.EMPTY,
                Ingredient.EMPTY,
                ItemStack(ProjectIItems.soulboneSplinters, 6),
            ),
        )

        add(
            CircuitStamperRecipe(
                redstone,
                boneSplinters,
                boneSplinters,
                boneSplinters,
                ItemStack(ProjectIItems.soulboneConduit, 3),
            ),
        )

        add(
            CircuitStamperRecipe(
                blueGem,
                boneConduit,
                boneConduit,
                boneConduit,
                ProjectIItems.controlCircuit.defaultInstance,
            ),
        )

        add(
            CircuitStamperRecipe(
                blueCrystal,
                controlCircuit,
                redstone,
                controlCircuit,
                ProjectIItems.emitter.defaultInstance
            ),
        )

        add(
            CircuitStamperRecipe(
                emitter,
                yellowCrystal,
                emitter,
                controlCircuit,
                ProjectIItems.quantifier.defaultInstance,
            ),
        )

        add(
            CircuitStamperRecipe(
                controlCircuit,
                blueGem,
                controlCircuit,
                Ingredient.of(ProjectIItems.yellowGem),
                ProjectIItems.ponderanceLattice.defaultInstance,
            ),
        )

        add(
            CircuitStamperRecipe(
                yellowCrystal,
                boneConduit,
                controlCircuit,
                controlCircuit,
                ProjectIItems.foci.defaultInstance,
            ),
        )

        add(
            CircuitStamperRecipe(
                Ingredient.of(ProjectIItems.ponderanceLattice),
                boneConduit,
                controlCircuit,
                boneConduit,
                ProjectIItems.villageDetector.defaultInstance,
            ),
        )
    }
}
