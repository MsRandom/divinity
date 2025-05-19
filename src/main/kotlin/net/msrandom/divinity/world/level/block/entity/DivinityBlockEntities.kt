package net.msrandom.divinity.world.level.block.entity

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.msrandom.divinity.world.level.block.DivinityBlocks

object DivinityBlockEntities : Registrar<BlockEntityType<*>>(Registries.BLOCK_ENTITY_TYPE) {
    val blueCrystal: BlockEntityType<BlueCrystalBlockEntity> by register.register("blue_crystal") { ->
        BlockEntityType(::BlueCrystalBlockEntity, setOf(DivinityBlocks.blueCrystal), null)
    }

    val circuitStamper: BlockEntityType<CircuitStamperBlockEntity> by register.register("circuit_stamper") { ->
        BlockEntityType(::CircuitStamperBlockEntity, setOf(DivinityBlocks.circuitStamper), null)
    }

    val bellows: BlockEntityType<BellowsBlockEntity> by register.register("bellows") { ->
        BlockEntityType(::BellowsBlockEntity, setOf(DivinityBlocks.bellows), null)
    }

    val blowMold: BlockEntityType<BlowMoldBlockEntity> by register.register("blow_mold") { ->
        BlockEntityType(::BlowMoldBlockEntity, setOf(DivinityBlocks.blowMold), null)
    }
}
