package net.msrandom.divinity.world.item.crafting

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object DivinityRecipeTypes : Registrar<RecipeType<*>> {
    override val register: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, Divinity.MOD_ID)

    val circuitStamper by recipeType<CircuitStamperRecipe>("circuit_stamper")

    private fun <T : Recipe<*>> recipeType(name: String): DeferredHolder<RecipeType<*>, RecipeType<T>> =
        register.register(name) { ->
            RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Divinity.MOD_ID, name))
        }
}
