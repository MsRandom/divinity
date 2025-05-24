package net.msrandom.divinity.data.recipe

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.item.crafting.CircuitStamperRecipe
import net.msrandom.divinity.world.item.crafting.DivinityRecipeSerializers
import net.msrandom.divinity.world.level.block.DivinityBlocks

fun buildCircuitStamperRecipes(recipeOutput: RecipeOutput, holderLookup: HolderLookup.Provider) {
    val recipeTypePath = BuiltInRegistries.RECIPE_SERIALIZER.getKey(DivinityRecipeSerializers.circuitStamper)!!.path

    fun add(recipe: CircuitStamperRecipe) {
        val key = BuiltInRegistries.ITEM.getKey(recipe.getResultItem(holderLookup).item)
        recipeOutput.accept(
            ResourceLocation.fromNamespaceAndPath(key.namespace, "$recipeTypePath/${key.path}"),
            recipe,
            null
        )
    }

    val soulbone = Ingredient.of(DivinityItems.soulbone)
    val boneConduit = Ingredient.of(DivinityItems.soulboneConduit)
    val blueCrystal = Ingredient.of(DivinityBlocks.blueCrystal)
    val boneSplinters = Ingredient.of(DivinityItems.soulboneSplinters)
    val controlCircuit = Ingredient.of(DivinityItems.controlCircuit)
    val emitter = Ingredient.of(DivinityItems.emitter)
    val yellowCrystal = Ingredient.of(DivinityItems.yellowCrystal)
    val blueGem = Ingredient.of(DivinityItems.blueGem)
    val redstone = Ingredient.of(Items.REDSTONE)
    val stone = Ingredient.of(Items.STONE)

    add(
        CircuitStamperRecipe(
            blueCrystal,
            soulbone,
            soulbone,
            redstone,
            DivinityItems.knowledgeGem.defaultInstance,
        )
    )

    add(
        CircuitStamperRecipe(
            stone,
            stone,
            stone,
            stone,
            ItemStack(DivinityItems.stoneRing, 4),
        ),
    )

    add(
        CircuitStamperRecipe(
            soulbone,
            Ingredient.EMPTY,
            Ingredient.EMPTY,
            Ingredient.EMPTY,
            ItemStack(DivinityItems.soulboneSplinters, 6),
        ),
    )

    add(
        CircuitStamperRecipe(
            redstone,
            boneSplinters,
            boneSplinters,
            boneSplinters,
            ItemStack(DivinityItems.soulboneConduit, 3),
        ),
    )

    add(
        CircuitStamperRecipe(
            blueGem,
            boneConduit,
            boneConduit,
            boneConduit,
            DivinityItems.controlCircuit.defaultInstance,
        ),
    )

    add(
        CircuitStamperRecipe(
            blueCrystal,
            controlCircuit,
            redstone,
            controlCircuit,
            DivinityItems.emitter.defaultInstance
        ),
    )

    add(
        CircuitStamperRecipe(
            emitter,
            yellowCrystal,
            emitter,
            controlCircuit,
            DivinityItems.quantifier.defaultInstance,
        ),
    )

    add(
        CircuitStamperRecipe(
            controlCircuit,
            blueGem,
            controlCircuit,
            Ingredient.of(DivinityItems.yellowGem),
            DivinityItems.ponderanceLattice.defaultInstance,
        ),
    )

    add(
        CircuitStamperRecipe(
            yellowCrystal,
            boneConduit,
            controlCircuit,
            controlCircuit,
            DivinityItems.foci.defaultInstance,
        ),
    )

    add(
        CircuitStamperRecipe(
            Ingredient.of(DivinityItems.ponderanceLattice),
            boneConduit,
            controlCircuit,
            boneConduit,
            DivinityItems.villageDetector.defaultInstance,
        ),
    )
}
