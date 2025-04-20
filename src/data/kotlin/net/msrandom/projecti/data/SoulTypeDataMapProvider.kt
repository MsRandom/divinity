package net.msrandom.projecti.data

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.world.entity.EntityType
import net.msrandom.projecti.world.level.soul.SoulType
import net.neoforged.neoforge.common.data.DataMapProvider
import java.util.concurrent.CompletableFuture

class SoulTypeDataMapProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
) : DataMapProvider(packOutput, lookupProvider) {
    override fun getName() = "Soul Types Data Map"

    override fun gather(provider: HolderLookup.Provider) {
        val builder = builder(SoulType.DATA_MAP)

        fun register(entity: EntityType<*>, soulType: SoulType) {
            builder.add(BuiltInRegistries.ENTITY_TYPE.getKey(entity), soulType, false)
        }

        register(EntityType.BAT, SoulType.Benign);
        register(EntityType.PIG, SoulType.Benign);
        register(EntityType.SHEEP, SoulType.Benign);
        register(EntityType.COW, SoulType.Benign);
        register(EntityType.CHICKEN, SoulType.Benign);
        register(EntityType.SQUID, SoulType.Benign);
        register(EntityType.HORSE, SoulType.Benign);
        register(EntityType.RABBIT, SoulType.Benign);
        register(EntityType.MOOSHROOM, SoulType.Benign);
        register(EntityType.IRON_GOLEM, SoulType.Immutable);
        register(EntityType.WOLF, SoulType.Predatory);
        register(EntityType.OCELOT, SoulType.Predatory);
        register(EntityType.SPIDER, SoulType.Predatory);
        register(EntityType.CAVE_SPIDER, SoulType.Predatory);
        register(EntityType.SILVERFISH, SoulType.Predatory);
        register(EntityType.GUARDIAN, SoulType.Predatory);
        register(EntityType.ENDERMITE, SoulType.Predatory);
        register(EntityType.GHAST, SoulType.Predatory);
        register(EntityType.BLAZE, SoulType.Predatory);
        register(EntityType.SLIME, SoulType.Predatory);
        register(EntityType.MAGMA_CUBE, SoulType.Predatory);
        register(EntityType.SNOW_GOLEM, SoulType.Predatory);
        register(EntityType.WITHER, SoulType.Doomed);
        register(EntityType.ENDER_DRAGON, SoulType.Doomed);
        register(EntityType.ENDERMAN, SoulType.Unhinged);
        register(EntityType.CREEPER, SoulType.Unhinged);
        register(EntityType.VILLAGER, SoulType.Wise);
        register(EntityType.WITCH, SoulType.Wise);
    }
}
