package net.msrandom.projecti.world.item.crafting

import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.Level

class CircuitStamperRecipe(val ingredients: NonNullList<Ingredient>, val result: ItemStack) : Recipe<CraftingInput> {
    constructor(
        ingredient1: Ingredient,
        ingredient2: Ingredient,
        ingredient3: Ingredient,
        ingredient4: Ingredient,
        result: ItemStack,
    ): this(NonNullList.of(Ingredient.EMPTY, ingredient1, ingredient2, ingredient3, ingredient4), result)

    override fun matches(
        input: CraftingInput,
        level: Level,
    ) = ingredients.withIndex().all { (index, ingredient) -> ingredient.test(input.getItem(index)) }

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack = result.copy()

    override fun canCraftInDimensions(width: Int, height: Int) = width == 2 && height == 2
    override fun getResultItem(registries: HolderLookup.Provider) = result
    override fun getSerializer() = ProjectIRecipeSerializers.circuitStamper
    override fun getType() = ProjectIRecipeTypes.circuitStamper

    companion object {
        private val INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY.listOf().flatXmap({
            if (it.size != 4) {
                DataResult.error { "Incorrect input ingredient size, required is 4 but got ${it.size}" }
            } else {
                DataResult.success(NonNullList.of(Ingredient.EMPTY, *it.toTypedArray()))
            }
        }, DataResult<List<Ingredient>>::success)

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
