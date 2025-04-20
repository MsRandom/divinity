@file:EventBusSubscriber(modid = ProjectI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)

package net.msrandom.projecti.data

import net.msrandom.projecti.ProjectI
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@Suppress("unused")
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    event.createProvider(::MeltingDataMapProvider)
    event.createProvider(::SoulTypeDataMapProvider)

    event.addProvider(ProjectITagProvider(event.generator.packOutput, event.lookupProvider, event.existingFileHelper))
}
