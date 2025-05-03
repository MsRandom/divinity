package net.msrandom.divinity.world.level.block

import com.google.common.base.CaseFormat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.msrandom.divinity.world.item.DivinityMoldItems
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

object DivinityGlasswareBlocks : Registrar<DeferredRegister.Blocks> {
    override val register: DeferredRegister.Blocks = DeferredRegister.createBlocks(Divinity.Companion.MOD_ID)

    val funnel by mold()
    val decanter by mold()
    val spitter by mold()
    val gourd by mold()
    val spiral by mold()

    class GlasswareBlockInfo(name: String) {
        val glassware: Block by register.registerSimpleBlock(name)
        val mold: Item by DivinityMoldItems.register.registerSimpleItem("${name}_mold")

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = this
    }

    private fun mold() = object : PropertyDelegateProvider<Any?, GlasswareBlockInfo> {
        override fun provideDelegate(
            thisRef: Any?,
            property: KProperty<*>,
        ) = GlasswareBlockInfo(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, property.name))
    }
}
