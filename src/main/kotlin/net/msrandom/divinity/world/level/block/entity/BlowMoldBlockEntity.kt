package net.msrandom.divinity.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.msrandom.divinity.world.item.crafting.DivinityRecipeTypes
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class BlowMoldBlockEntity(
    pos: BlockPos,
    state: BlockState,
) :
    BlockEntity(DivinityBlockEntities.blowMold, pos, state),
    RecipeInput,
    Container {
    internal val tank: FluidTank = FluidTank(FluidType.BUCKET_VOLUME)
    private val items = NonNullList.withSize(2, ItemStack.EMPTY)

    override fun getContainerSize() = items.size
    override fun isEmpty() = items.all(ItemStack::isEmpty)
    override fun getItem(index: Int): ItemStack = items[index]
    override fun removeItem(slot: Int, amount: Int): ItemStack = ContainerHelper.removeItem(items, slot, amount)
    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(items, slot)

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
    }

    override fun stillValid(player: Player) = true

    override fun size() = containerSize

    override fun loadAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.loadAdditional(tag, registries)

        ContainerHelper.loadAllItems(tag, items, registries)
        tank.readFromNBT(registries, tag)
    }

    override fun saveAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.saveAdditional(tag, registries)

        ContainerHelper.saveAllItems(tag, items, registries)
        tank.writeToNBT(registries, tag)
    }

    override fun clearContent() {
        items.clear()
    }

    fun craft() {
        val recipe = level?.recipeManager?.getRecipeFor(DivinityRecipeTypes.blowMold, this, level)?.orElse(null) ?: return

        val result = recipe.value.assemble(this, level!!.registryAccess())

        setItem(OUTPUT_SLOT, result)

        tank.drain(tank.fluidAmount, IFluidHandler.FluidAction.EXECUTE)
    }

    companion object {
        const val MOLD_SLOT = 0
        const val OUTPUT_SLOT = 1
    }
}
