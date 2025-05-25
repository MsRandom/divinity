@file:EventBusSubscriber(modid = Divinity.MOD_ID, bus = EventBusSubscriber.Bus.MOD)

package net.msrandom.divinity.data

import net.msrandom.divinity.Divinity
import net.msrandom.divinity.data.client.DivinityBlockStateProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@Suppress("unused")
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    if (event.includeServer()) {
        event.createProvider(::MeltingDataMapProvider)
        event.createProvider(::SoulTypeDataMapProvider)
        event.createProvider(::DivinityRecipeProvider)

        event.addProvider(
            DivinityBlockTagsProvider(
                event.generator.packOutput,
                event.lookupProvider,
                event.existingFileHelper,
            )
        )
        event.addProvider(
            DivinityFluidTagsProvider(
                event.generator.packOutput,
                event.lookupProvider,
                event.existingFileHelper,
            )
        )
    }

    if (event.includeClient()) {
        event.addProvider(DivinityBlockStateProvider(event.generator.packOutput, event.existingFileHelper))
    }
}
