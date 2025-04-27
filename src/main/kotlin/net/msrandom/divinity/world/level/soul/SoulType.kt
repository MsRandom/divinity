package net.msrandom.divinity.world.level.soul

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.msrandom.divinity.Divinity
import net.neoforged.neoforge.registries.datamaps.DataMapType

enum class SoulType : StringRepresentable {
    Benign,
    Immutable,
    Predatory,
    Doomed,
    Unhinged,
    Malleable,
    Wise,
    Noble;

    override fun getSerializedName() = name.lowercase()

    companion object {
        private val byName = entries.associateBy { it.serializedName }

        @JvmField
        val CODEC: Codec<SoulType> = Codec.STRING.flatXmap({
            val soulType = byName[it]

            if (soulType == null) {
                DataResult.error { "Invalid soul type $it, allowed values are ${byName.keys}" }
            } else {
                DataResult.success(soulType)
            }
        }, {
            DataResult.success(it.serializedName)
        })

        @JvmField
        val DATA_MAP: DataMapType<EntityType<*>, SoulType> = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(Divinity.MOD_ID, "soul_types"),
            Registries.ENTITY_TYPE,
            CODEC,
        ).build()
    }
}
