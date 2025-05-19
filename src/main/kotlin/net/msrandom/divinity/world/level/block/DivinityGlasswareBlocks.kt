package net.msrandom.divinity.world.level.block

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.BlockRegistrar
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.item.DivinityMoldItems
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object DivinityGlasswareBlocks : BlockRegistrar() {
    val funnel by mold()
    val decanter by mold()
    val spitter by mold()
    val gourd by mold()
    val spiral by mold()

    class GlasswareBlockInfo(name: String) : ReadOnlyProperty<Any?, GlasswareBlockInfo> {
        val glassware: Block by register.registerSimpleBlock(name)
        val mold: Item by DivinityMoldItems.register.registerSimpleItem("${name}_mold")

        init {
            DivinityItems.register.registerSimpleBlockItem(name, ::glassware)
        }

        override operator fun getValue(thisRef: Any?, property: KProperty<*>) = this
    }

    private fun mold() = object : PropertyDelegateProvider<Any?, GlasswareBlockInfo> {
        override fun provideDelegate(
            thisRef: Any?,
            property: KProperty<*>,
        ) = GlasswareBlockInfo(property.name)
    }
}
