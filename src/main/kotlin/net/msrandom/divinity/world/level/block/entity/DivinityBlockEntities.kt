package net.msrandom.divinity.world.level.block.entity

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper

object DivinityBlockEntities : Registrar<BlockEntityType<*>>(Registries.BLOCK_ENTITY_TYPE) {
    val circuitStamper: BlockEntityType<CircuitStamperBlockEntity> by register.register("circuit_stamper") { ->
        BlockEntityType(::CircuitStamperBlockEntity, setOf(DivinityBlocks.circuitStamper), null)
    }

    val bellows: BlockEntityType<BellowsBlockEntity> by register.register("bellows") { ->
        BlockEntityType(::BellowsBlockEntity, setOf(DivinityBlocks.bellows), null)
    }

    val blowMold: BlockEntityType<BlowMoldBlockEntity> by register.register("blow_mold") { ->
        BlockEntityType(::BlowMoldBlockEntity, setOf(DivinityBlocks.blowMold), null)
    }

    val liquidInlet: BlockEntityType<LiquidInletBlockEntity> by register.register("liquid_inlet") { ->
        BlockEntityType(::LiquidInletBlockEntity, setOf(DivinityBlocks.bareLiquidInlet, DivinityBlocks.cooledLiquidInlet), null)
    }

    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, blowMold) { entity, direction ->
            entity.tank
        }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, circuitStamper) { entity, direction ->
            SidedInvWrapper(entity, direction)
        }
    }
}
