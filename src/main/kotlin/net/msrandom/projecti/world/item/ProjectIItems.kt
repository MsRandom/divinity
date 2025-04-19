package net.msrandom.projecti.world.item

import net.minecraft.world.item.Item
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.getValue
import net.neoforged.neoforge.registries.DeferredRegister

object ProjectIItems {
    val register: DeferredRegister.Items = DeferredRegister.createItems(ProjectI.MOD_ID)

    val blueCrystal: Item by register.registerSimpleItem("blue_crystal")
    val yellowCrystal: Item by register.registerSimpleItem("yellow_crystal")
}
