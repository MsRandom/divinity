package net.msrandom.projecti.world.level.soul

import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.msrandom.projecti.world.item.ProjectIItems
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent

object SoulEventHandler {
    // TODO Needs special handling for hollow golems and entities with no souls
    private fun isSoulPresent(entity: LivingEntity): Boolean =
        // Ignore baby villagers
        entity is Player || entity !is Villager || entity.isBaby

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun dropSoulBone(event: LivingDeathEvent) {
        val entity = event.entity

        if (entity.level().isClientSide) {
            return
        }

        val attacker = event.source.directEntity

        if (attacker !is LivingEntity) {
            return
        }

        val weapon = attacker.mainHandItem

        if (weapon.item !== Items.BONE) {
            return
        }

        @Suppress("DEPRECATION")
        val soulType = entity.type.builtInRegistryHolder().getData(SoulType.DATA_MAP)

        if (soulType == null || !isSoulPresent(entity)) {
            return
        }

        // TODO Sound efect

        if (attacker is Player) {
            attacker.setItemInHand(
                InteractionHand.MAIN_HAND,
                ItemUtils.createFilledResult(weapon, attacker, ProjectIItems.soulbone.defaultInstance)
            )

            // TODO Grant knowledge
            return
        }

        weapon.consume(1, attacker)

        if (weapon.isEmpty) {
            attacker.setItemInHand(InteractionHand.MAIN_HAND, ProjectIItems.soulbone.defaultInstance)
        } else {
            attacker.level().addFreshEntity(
                ItemEntity(
                    attacker.level(),
                    attacker.x,
                    attacker.y + attacker.bbHeight * 0.5,
                    attacker.z,
                    ProjectIItems.soulbone.defaultInstance,
                )
            )
        }

        return
    }
}
