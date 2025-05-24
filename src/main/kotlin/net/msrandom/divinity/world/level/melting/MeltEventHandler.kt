package net.msrandom.divinity.world.level.melting

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Entity.RemovalReason
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.material.Fluid
import net.msrandom.divinity.world.level.melting.MeltingData.MELTING_TICK_FACTOR
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

internal inline fun Entity.forEachFluidType(crossinline action: (fluidType: FluidType) -> Unit) {
    // No other way to iterate through containing fluid types, so
    isInFluidType { fluidType, _ ->
        action(fluidType)

        // Keep iterating regardless so we get all fluid types
        false
    }
}

internal fun getMoltenForm(itemHolder: Holder<Item>) = itemHolder.getData(MeltingData.DATA_MAP)
internal fun getMoltenForm(entity: ItemEntity) = getMoltenForm(entity.item.itemHolder)

internal fun calculateMeltTime(moltenFluid: Fluid, containingLiquid: FluidType, tickFactor: Int = MELTING_TICK_FACTOR) =
    (moltenFluid.fluidType.temperature * tickFactor) / containingLiquid.temperature

object MeltEventHandler {

    fun checkEntityInvulnerability(event: EntityInvulnerabilityCheckEvent) {
        val entity = event.entity

        if (entity !is ItemEntity) {
            return
        }

        if (event.source.`is`(DamageTypeTags.IS_FIRE) && getMoltenForm(entity) != null) {
            // This item can melt, thus the "burning" damage should not affect it
            event.isInvulnerable = true
        }
    }

    fun handleItemTick(itemEntity: ItemEntity) {
        val level = itemEntity.level()

        if (!itemEntity.isInFluidType) {
            // Not in fluid, can not be melting
            return
        }

        val moltenFluid = getMoltenForm(itemEntity) ?: return

        var hottestFluidType: FluidType? = null

        itemEntity.forEachFluidType {
            if (moltenFluid.fluidType.temperature >= it.temperature) {
                // Molten fluid form of item is hotter than this fluid containing it, thus we can skip it as it is not hot enough
                return@forEachFluidType
            }

            if (hottestFluidType == null || it.temperature > hottestFluidType!!.temperature) {
                hottestFluidType = it
            }
        }

        if (hottestFluidType == null) {
            // The item is not in any fluid hot enough to melt it
            return
        }

        // Hotter fluid means faster melting, colder molten form means faster melting
        val meltTime = calculateMeltTime(moltenFluid, hottestFluidType)

        if (!level.isClientSide && itemEntity.age > meltTime) {
            // Item has lasted long enough to melt
            val pos = BlockPos.containing(itemEntity.x, itemEntity.y + itemEntity.getFluidTypeHeight(hottestFluidType), itemEntity.z)

            itemEntity.discard()

            val tank = FluidTank(FluidType.BUCKET_VOLUME)
            val stack = FluidStack(moltenFluid, FluidType.BUCKET_VOLUME)

            tank.fill(stack, IFluidHandler.FluidAction.EXECUTE)

            val state = moltenFluid.fluidType.getStateForPlacement(level, pos, stack)

            level.setBlock(pos, moltenFluid.fluidType.getBlockForFluidState(level, pos, state), Block.UPDATE_ALL_IMMEDIATE)
            level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0)

            return
        }

        if (itemEntity.deltaMovement.y > 0.0) {
            // Stop item from floating
            itemEntity.setDeltaMovement(itemEntity.deltaMovement.x, 0.0, itemEntity.deltaMovement.z)
        }

        return
    }
}
