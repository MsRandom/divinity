package net.msrandom.projecti.world.level.melting

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.SharedConstants.TICKS_PER_SECOND
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.msrandom.projecti.ProjectI
import net.neoforged.neoforge.registries.datamaps.DataMapType

data class MeltingData(val moltenFluid: Fluid, val meltTicks: Int = DEFAULT_MELT_TICKS) {
    companion object {
        const val DEFAULT_MELT_TICKS = 5 * TICKS_PER_SECOND

        @JvmField
        val CODEC: Codec<MeltingData> = RecordCodecBuilder.create { instance ->
            instance.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("molten_fluid").forGetter(MeltingData::moltenFluid),
                Codec.INT.optionalFieldOf("melt_ticks", DEFAULT_MELT_TICKS).forGetter(MeltingData::meltTicks)
            ).apply(instance, ::MeltingData)
        }

        @JvmField
        val DATA_MAP: DataMapType<Item, MeltingData> = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(ProjectI.Companion.MOD_ID, "meltable"),
            Registries.ITEM,
            CODEC,
        ).build()
    }
}
