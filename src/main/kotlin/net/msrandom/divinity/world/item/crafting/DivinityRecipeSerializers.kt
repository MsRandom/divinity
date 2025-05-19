package net.msrandom.divinity.world.item.crafting

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar

object DivinityRecipeSerializers : Registrar<RecipeSerializer<*>>(Registries.RECIPE_SERIALIZER) {
    val circuitStamper: RecipeSerializer<CircuitStamperRecipe> by register.register("circuit_stamper") { ->
        object : RecipeSerializer<CircuitStamperRecipe> {
            override fun codec() = CircuitStamperRecipe.CODEC
            override fun streamCodec() = CircuitStamperRecipe.STREAM_CODEC
        }
    }

    val blowMold: RecipeSerializer<BlowMoldRecipe> by register.register("blow_mold") { ->
        object : RecipeSerializer<BlowMoldRecipe> {
            override fun codec() = BlowMoldRecipe.CODEC
            override fun streamCodec() = BlowMoldRecipe.STREAM_CODEC
        }
    }
}
