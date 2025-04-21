package net.msrandom.projecti.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.msrandom.projecti.world.item.crafting.ProjectIRecipeTypes
import net.neoforged.neoforge.items.wrapper.InvWrapper
import net.neoforged.neoforge.items.wrapper.RecipeWrapper
import kotlin.jvm.optionals.getOrNull

class CircuitStamperBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : BlockEntity(ProjectIBlockEntities.circuitStamper, pos, state), WorldlyContainer {
    val items: NonNullList<ItemStack> = NonNullList.withSize(OUTPUT_SLOT + 1, ItemStack.EMPTY)

    override fun getContainerSize() = items.size
    override fun isEmpty() = items.all(ItemStack::isEmpty)
    override fun getItem(slot: Int) = items[slot]
    override fun removeItem(slot: Int, amount: Int): ItemStack = ContainerHelper.removeItem(items, slot, amount)
    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(items, slot)

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
    }

    override fun stillValid(player: Player) = true
    override fun clearContent() = items.clear()

    override fun getSlotsForFace(side: Direction) = if (side == Direction.DOWN) {
        intArrayOf(OUTPUT_SLOT)
    } else {
        INPUT_SLOTS
    }

    override fun canPlaceItemThroughFace(
        index: Int,
        itemStack: ItemStack,
        direction: Direction?,
    ) = isSlotFacing(index, direction)

    override fun canTakeItemThroughFace(
        index: Int,
        stack: ItemStack,
        direction: Direction,
    ) = isSlotFacing(index, direction)

    private fun isSlotFacing(slot: Int, direction: Direction?): Boolean {
        val isOutputRequested = slot == OUTPUT_SLOT
        val isDirectionDown = direction == Direction.DOWN

        return isOutputRequested == isDirectionDown
    }

    fun stamp() {
        // TODO Implement Capabilities.ItemHandler.BLOCK
        val input = RecipeWrapper(InvWrapper(this))

        val level = level!!

        val recipe = level
            .recipeManager
            .getRecipeFor(ProjectIRecipeTypes.circuitStamper, input, level)
            .getOrNull()
            ?: return

        val output = recipe.value.assemble(input, level.registryAccess())
        val current = getItem(OUTPUT_SLOT)

        if (current.isEmpty) {
            setItem(OUTPUT_SLOT, output)
        } else if (ItemStack.isSameItem(output, current)) {
            if (output.count > current.maxStackSize - current.count) {
                // Failure, TODO play sound event with same sounds of SoundEvents.NOTE_BLOCK_SNARE if the block below is not a sound dampening block

                return
            }

            current.count += output.count
        } else {
            // Failure, TODO same as above

            return
        }

        // TODO play success sound effect if sound is not dampened
    }

    companion object {
        const val OUTPUT_SLOT = 4

        @JvmField
        val INPUT_SLOTS = (0..OUTPUT_SLOT - 1).toList().toIntArray()
    }
}
