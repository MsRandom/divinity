package net.msrandom.projecti.world.level.melting

import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.entity.Entity.RemovalReason
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent

object MeltEventHandler {
    private fun getMeltData(entity: ItemEntity) = entity.item.itemHolder.getData(MeltingData.DATA_MAP)

    @Suppress("unused")
    @SubscribeEvent(priority = EventPriority.HIGH)
    fun checkEntityInvulnerability(event: EntityInvulnerabilityCheckEvent) {
        val entity = event.entity

        if (entity !is ItemEntity) {
            return
        }

        if (event.source.`is`(DamageTypeTags.IS_FIRE) && getMeltData(entity) != null) {
            // This item can melt, thus the "burning" damage should not affect it
            event.isInvulnerable = true
        }
    }

    fun handleItemTick(itemEntity: ItemEntity) {
        val level = itemEntity.level()
        val pos = itemEntity.blockPosition()
        val containingFluid = level.getFluidState(pos)

        if (containingFluid.isEmpty) {
            // Not in fluid, can not be melting

            return
        }

        val meltingData = getMeltData(itemEntity) ?: return

        if (meltingData.moltenFluid.fluidType.temperature >= containingFluid.fluidType.temperature) {
            // Temperature of the molten form of this item is higher than the fluid it is currently submerged in, thus it is not melting
            return
        }

        if (!level.isClientSide && itemEntity.age > meltingData.meltTicks) {
            // Item has lasted long enough to melt
            itemEntity.remove(RemovalReason.DISCARDED)
            level.setBlock(pos, meltingData.moltenFluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_NEIGHBORS or Block.UPDATE_CLIENTS)

            return
        }

        if (itemEntity.deltaMovement.y > 0.0) {
            itemEntity.setDeltaMovement(itemEntity.deltaMovement.x, 0.0, itemEntity.deltaMovement.z)
        }

        return
    }
}
