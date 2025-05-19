package net.msrandom.divinity.world.item

import net.minecraft.world.item.Item
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.ItemRegistrar

object DivinityMoldItems : ItemRegistrar() {
    val gemMold: Item by simpleItem()
    val bottleMold: Item by simpleItem()
    val paneMold: Item by simpleItem()
}
