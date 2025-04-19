package net.msrandom.projecti.world.level.block

import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.getValue
import net.neoforged.neoforge.registries.DeferredRegister

object ProjectIBlocks {
    val register: DeferredRegister.Blocks = DeferredRegister.createBlocks(ProjectI.MOD_ID)

    val circuitStamper: CircuitStamperBlock by register.registerBlock("circuit_stamper", ::CircuitStamperBlock)
    val bellows: BellowsBlock by register.registerBlock("bellows", ::BellowsBlock)
}
