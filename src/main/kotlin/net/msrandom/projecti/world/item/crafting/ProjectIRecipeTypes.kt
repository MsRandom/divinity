package net.msrandom.projecti.world.item.crafting

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import net.msrandom.projecti.ProjectI
import net.neoforged.neoforge.registries.DeferredRegister

object ProjectIRecipeTypes {
    val register: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, ProjectI.MOD_ID)

    val circuitStamper: RecipeType<CircuitStamperRecipe>
}
