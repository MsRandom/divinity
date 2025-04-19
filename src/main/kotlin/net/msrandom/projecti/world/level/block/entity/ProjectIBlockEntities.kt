package net.msrandom.projecti.world.level.block.entity

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.getValue
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import net.neoforged.neoforge.registries.DeferredRegister

object ProjectIBlockEntities {
    val register: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProjectI.MOD_ID)

    val circuitStamper: BlockEntityType<CircuitStamperBlockEntity> by register.register("circuit_stamper") { ->
        BlockEntityType(::CircuitStamperBlockEntity, setOf(ProjectIBlocks.circuitStamper), null)
    }

    val bellows: BlockEntityType<BellowsBlockEntity> by register.register("bellows") { ->
        BlockEntityType(::BellowsBlockEntity, setOf(ProjectIBlocks.bellows), null)
    }
}
