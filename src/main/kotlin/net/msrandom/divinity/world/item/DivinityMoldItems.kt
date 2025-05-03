package net.msrandom.divinity.world.item

import net.minecraft.world.item.Item
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.neoforged.neoforge.registries.DeferredRegister

object DivinityMoldItems : Registrar<DeferredRegister.Items> {
    override val register: DeferredRegister.Items = DeferredRegister.createItems(Divinity.MOD_ID)

    val gemMold: Item by simpleItem()
    val bottleMold: Item by simpleItem()
    val paneMold: Item by simpleItem()
}
