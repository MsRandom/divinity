package net.msrandom.projecti.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class CircuitStamperBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ProjectIBlockEntities.circuitStamper, pos, state), Container {
    private val items = NonNullList.withSize(4, ItemStack.EMPTY)

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
}
