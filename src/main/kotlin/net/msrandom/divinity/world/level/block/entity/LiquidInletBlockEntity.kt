package net.msrandom.divinity.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class LiquidInletBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(DivinityBlockEntities.liquidInlet, pos, state) {
    // Not exposed as a Capabilities.FluidHandler.BLOCK as it is meant to be used exclusively with the blow mold
    internal val tank = FluidTank(FluidType.BUCKET_VOLUME)

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tank.writeToNBT(registries, tag)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        tank.readFromNBT(registries, tag)
    }
}
