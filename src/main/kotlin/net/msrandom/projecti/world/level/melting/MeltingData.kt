package net.msrandom.projecti.world.level.melting

import net.minecraft.SharedConstants.TICKS_PER_SECOND
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.msrandom.projecti.ProjectI
import net.neoforged.neoforge.registries.datamaps.DataMapType

object MeltingData {
    const val MELTING_TICK_FACTOR = 10 + 6 * TICKS_PER_SECOND

    @JvmField
    val DATA_MAP: DataMapType<Item, Fluid> = DataMapType.builder(
        ResourceLocation.fromNamespaceAndPath(ProjectI.Companion.MOD_ID, "meltable"),
        Registries.ITEM,
        BuiltInRegistries.FLUID.byNameCodec(),
    ).build()
}
