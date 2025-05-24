package net.msrandom.divinity.data.recipe

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluid
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.item.DivinityMoldItems
import net.msrandom.divinity.world.item.crafting.BlowMoldRecipe
import net.msrandom.divinity.world.item.crafting.DivinityRecipeSerializers
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.msrandom.divinity.world.level.block.DivinityGlasswareBlocks
import net.msrandom.divinity.world.level.fluid.DivinityFluids
import net.neoforged.neoforge.fluids.crafting.FluidIngredient

fun buildBlowMoldRecipes(recipeOutput: RecipeOutput) {
    val recipeTypePath = BuiltInRegistries.RECIPE_SERIALIZER.getKey(DivinityRecipeSerializers.blowMold)!!.path

    fun add(mold: Item, fluid: Fluid, result: ItemStack) {
        val key = BuiltInRegistries.ITEM.getKey(result.item)
        val recipe = BlowMoldRecipe(Ingredient.of(mold), FluidIngredient.of(fluid), result)

        recipeOutput.accept(
            ResourceLocation.fromNamespaceAndPath(key.namespace, "$recipeTypePath/${key.path}"),
            recipe,
            null,
        )
    }

    fun addGlasswareRecipe(info: DivinityGlasswareBlocks.GlasswareBlockInfo) =
        add(info.mold, DivinityFluids.moltenBlueCrystal, ItemStack(info.glassware))

    addGlasswareRecipe(DivinityGlasswareBlocks.funnel)
    addGlasswareRecipe(DivinityGlasswareBlocks.decanter)
    addGlasswareRecipe(DivinityGlasswareBlocks.spitter)
    addGlasswareRecipe(DivinityGlasswareBlocks.gourd)
    addGlasswareRecipe(DivinityGlasswareBlocks.spiral)

    add(DivinityMoldItems.gemMold, DivinityFluids.moltenBlueCrystal, ItemStack(DivinityItems.blueGem, 3))
    add(DivinityMoldItems.gemMold, DivinityFluids.moltenYellowCrystal, ItemStack(DivinityItems.yellowCrystal, 3))

    add(DivinityMoldItems.bottleMold, DivinityFluids.moltenGlass, ItemStack(Items.GLASS_BOTTLE, 6))
    add(DivinityMoldItems.paneMold, DivinityFluids.moltenGlass, ItemStack(Blocks.GLASS_PANE, 5))
    add(DivinityMoldItems.inletMold, DivinityFluids.moltenGlass, ItemStack(DivinityBlocks.bareLiquidInlet))
}
