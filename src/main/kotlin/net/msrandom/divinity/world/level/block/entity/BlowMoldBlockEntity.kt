package net.msrandom.divinity.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class BlowMoldBlockEntity private constructor(
    pos: BlockPos,
    state: BlockState,
    private val tank: FluidTank = FluidTank(FluidType.BUCKET_VOLUME),
) :
    BlockEntity(DivinityBlockEntities.blowMold, pos, state),
    RecipeInput,
    Container,
    IFluidHandler by tank {
    private var moldItem = ItemStack.EMPTY
    private var resultItem = ItemStack.EMPTY

    constructor(pos: BlockPos, state: BlockState): this(pos, state, FluidTank(FluidType.BUCKET_VOLUME))

    override fun getContainerSize() = 2

    override fun isEmpty() = moldItem.isEmpty && resultItem.isEmpty

    override fun getItem(index: Int): ItemStack = if (index == OUTPUT_SLOT) {
        resultItem
    } else {
        moldItem
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun stillValid(player: Player) = true

    override fun size() = containerSize

    override fun loadAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.loadAdditional(tag, registries)

        tank.readFromNBT(registries, tag)
    }

    override fun saveAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.saveAdditional(tag, registries)

        tank.writeToNBT(registries, tag)
    }

    override fun clearContent() {
        moldItem = ItemStack.EMPTY
        resultItem = ItemStack.EMPTY
    }

    companion object {
        const val MOLD_SLOT = 1
        const val OUTPUT_SLOT = 1
    }
}
