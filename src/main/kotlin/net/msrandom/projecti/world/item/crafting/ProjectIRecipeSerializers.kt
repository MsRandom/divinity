package net.msrandom.projecti.world.item.crafting

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.getValue
import net.neoforged.neoforge.registries.DeferredRegister

object ProjectIRecipeSerializers {
    val register: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProjectI.MOD_ID)

    val circuitStamper: RecipeSerializer<CircuitStamperRecipe> by register.register("circuit_stamper") { ->
        object : RecipeSerializer<CircuitStamperRecipe> {
            override fun codec() = CircuitStamperRecipe.CODEC
            override fun streamCodec() = CircuitStamperRecipe.STREAM_CODEC
        }
    }
}
