package net.msrandom.projecti.world.item

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.getValue
import net.msrandom.projecti.world.Registrar
import net.neoforged.neoforge.registries.DeferredRegister

object ProjectIItems : Registrar<Item> {
    override val register: DeferredRegister.Items = DeferredRegister.createItems(ProjectI.MOD_ID)
    val tabRegister: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProjectI.MOD_ID)

    val yellowCrystal: Item by register.registerSimpleItem("yellow_crystal")
    val knowledgeGem: Item by register.registerSimpleItem("knowledge_gem")

    init {
        tabRegister.register("main") { ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.${ProjectI.MOD_ID}.main"))
                .icon { knowledgeGem.defaultInstance }
                .displayItems { _, output ->
                    for (holder in register.entries) {
                        output.accept(holder.get())
                    }
                }.build()
        }
    }
}
