@file:EventBusSubscriber(modid = Divinity.MOD_ID, bus = EventBusSubscriber.Bus.MOD)

package net.msrandom.divinity.data

import net.msrandom.divinity.Divinity
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@Suppress("unused")
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    event.createProvider(::MeltingDataMapProvider)
    event.createProvider(::SoulTypeDataMapProvider)
    event.createProvider(::CircuitStamperRecipeProvider)

    event.addProvider(DivinityBlockTagsProvider(event.generator.packOutput, event.lookupProvider, event.existingFileHelper))
    event.addProvider(DivinityFluidTagsProvider(event.generator.packOutput, event.lookupProvider, event.existingFileHelper))
}
