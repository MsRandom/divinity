package net.msrandom.divinity.world.item.crafting

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.neoforged.neoforge.registries.DeferredRegister

object DivinityRecipeSerializers : Registrar<RecipeSerializer<*>> {
    override val register: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Divinity.MOD_ID)

    val circuitStamper: RecipeSerializer<CircuitStamperRecipe> by register.register("circuit_stamper") { ->
        object : RecipeSerializer<CircuitStamperRecipe> {
            override fun codec() = CircuitStamperRecipe.CODEC
            override fun streamCodec() = CircuitStamperRecipe.STREAM_CODEC
        }
    }
}
