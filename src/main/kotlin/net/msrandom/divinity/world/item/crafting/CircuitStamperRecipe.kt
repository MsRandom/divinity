package net.msrandom.divinity.world.item.crafting

import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.Level

class CircuitStamperRecipe(private val ingredients: NonNullList<Ingredient>, private val result: ItemStack) : Recipe<RecipeInput> {
    constructor(
        topLeftIngredient: Ingredient,
        topRightIngredient: Ingredient,
        bottomLeftIngredient: Ingredient,
        bottomRightIngredient: Ingredient,
        result: ItemStack,
    ): this(NonNullList.of(Ingredient.EMPTY, topLeftIngredient, topRightIngredient, bottomLeftIngredient, bottomRightIngredient), result)

    override fun matches(
        input: RecipeInput,
        level: Level,
    ) = ingredients.withIndex().all { (index, ingredient) -> ingredient.test(input.getItem(index)) }

    override fun assemble(input: RecipeInput, registries: HolderLookup.Provider): ItemStack = result.copy()
    override fun canCraftInDimensions(width: Int, height: Int) = width == 2 && height == 2
    override fun getResultItem(registries: HolderLookup.Provider) = result
    override fun getIngredients() = ingredients
    override fun isSpecial() = true
    override fun getToastSymbol() = result
    override fun getSerializer() = DivinityRecipeSerializers.circuitStamper
    override fun getType() = DivinityRecipeTypes.circuitStamper

    override fun isIncomplete(): Boolean {
        val ingredients = ingredients

        return ingredients.isEmpty() || ingredients.filterNot(Ingredient::isEmpty).any(Ingredient::hasNoItems)
    }

    companion object {
        private val INGREDIENTS_CODEC = Ingredient.CODEC.listOf().flatXmap({
            if (it.size != 4) {
                DataResult.error { "Incorrect input ingredient size, required is 4 but got ${it.size}" }
            } else {
                DataResult.success(NonNullList.of(Ingredient.EMPTY, *it.toTypedArray()))
            }
        }, DataResult<List<Ingredient>>::success)

        @JvmField
        val CODEC: MapCodec<CircuitStamperRecipe> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(CircuitStamperRecipe::ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(CircuitStamperRecipe::result),
            ).apply(instance, ::CircuitStamperRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CircuitStamperRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            { it.ingredients[0] },
            Ingredient.CONTENTS_STREAM_CODEC,
            { it.ingredients[0] },
            Ingredient.CONTENTS_STREAM_CODEC,
            { it.ingredients[0] },
            Ingredient.CONTENTS_STREAM_CODEC,
            { it.ingredients[0] },
            ItemStack.STREAM_CODEC,
            { it.result },
            ::CircuitStamperRecipe,
        )
    }
}
