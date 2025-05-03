package net.msrandom.divinity.world.item

import com.google.common.base.CaseFormat
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

internal fun Registrar<DeferredRegister.Items>.simpleItem() = object : PropertyDelegateProvider<Any?, DeferredItem<Item>> {
    override fun provideDelegate(
        thisRef: Any?,
        property: KProperty<*>,
    ) = register.registerSimpleItem(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, property.name))
}

object DivinityItems : Registrar<DeferredRegister.Items> {
    override val register: DeferredRegister.Items = DeferredRegister.createItems(Divinity.MOD_ID)

    val tabRegister: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Divinity.MOD_ID)

    val knowledgeGem: Item by simpleItem()
    val foci: Item by simpleItem()
    val soulbone: Item by simpleItem()
    val villageDetector: Item by simpleItem()
    val yellowCrystal: Item by simpleItem()
    val soulboneSplinters: Item by simpleItem()
    val soulboneConduit: Item by simpleItem()
    val stoneRing: Item by simpleItem()
    val controlCircuit: Item by simpleItem()
    val blueGem: Item by simpleItem()
    val yellowGem: Item by simpleItem()
    val emitter: Item by simpleItem()
    val quantifier: Item by simpleItem()
    val ponderanceLattice: Item by simpleItem()

    init {
        tabRegister.register("main") { ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.${Divinity.MOD_ID}.main"))
                .icon { knowledgeGem.defaultInstance }
                .displayItems { _, output ->
                    for (holder in register.entries) {
                        output.accept(holder.get())
                    }
                }.build()
        }
    }
}
