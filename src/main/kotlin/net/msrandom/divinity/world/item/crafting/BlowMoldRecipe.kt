package net.msrandom.divinity.world.item.crafting

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.Level
import net.msrandom.divinity.world.level.block.entity.BlowMoldBlockEntity
import net.neoforged.neoforge.fluids.crafting.FluidIngredient

class BlowMoldRecipe(
    private val moldIngredient: Ingredient,
    private val fluidIngredient: FluidIngredient,
    private val result: ItemStack,
) : Recipe<BlowMoldBlockEntity> {
    override fun matches(
        input: BlowMoldBlockEntity,
        level: Level,
    ) = moldIngredient.test(input.getItem(BlowMoldBlockEntity.MOLD_SLOT)) &&
            fluidIngredient.test(input.getFluidInTank(0))

    override fun assemble(input: BlowMoldBlockEntity, registries: HolderLookup.Provider): ItemStack = result.copy()
    override fun canCraftInDimensions(width: Int, height: Int) = true
    override fun getResultItem(registries: HolderLookup.Provider) = result
    override fun getIngredients(): NonNullList<Ingredient> = NonNullList.of(Ingredient.EMPTY, moldIngredient)
    override fun isSpecial() = true
    override fun getToastSymbol() = result
    override fun getSerializer() = DivinityRecipeSerializers.blowMold
    override fun getType() = DivinityRecipeTypes.blowMold

    override fun isIncomplete(): Boolean {
        val hasIncompleteMold = !moldIngredient.isEmpty && moldIngredient.hasNoItems()
        val hasIncompleteFluid = !fluidIngredient.isEmpty && fluidIngredient.hasNoFluids()

        return hasIncompleteMold || hasIncompleteFluid
    }

    companion object {
        @JvmField
        val CODEC: MapCodec<BlowMoldRecipe> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("mold").forGetter(BlowMoldRecipe::moldIngredient),
                FluidIngredient.CODEC_NON_EMPTY.fieldOf("fluid").forGetter(BlowMoldRecipe::fluidIngredient),
                ItemStack.CODEC.fieldOf("result").forGetter(BlowMoldRecipe::result),
            ).apply(instance, ::BlowMoldRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BlowMoldRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            BlowMoldRecipe::moldIngredient,
            FluidIngredient.STREAM_CODEC,
            BlowMoldRecipe::fluidIngredient,
            ItemStack.STREAM_CODEC,
            BlowMoldRecipe::result,
            ::BlowMoldRecipe,
        )
    }
}
