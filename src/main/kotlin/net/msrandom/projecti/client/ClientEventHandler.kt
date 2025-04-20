@file:EventBusSubscriber(value = [Dist.CLIENT], modid = ProjectI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)

package net.msrandom.projecti.client

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.world.level.fluid.ProjectIFluids
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent

@SubscribeEvent
fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
    fun registerFluidTextures(fluid: Fluid) {
        val id = BuiltInRegistries.FLUID.getKey(fluid)

        event.registerFluidType(
            object : IClientFluidTypeExtensions {
                override fun getStillTexture() =
                    ResourceLocation.fromNamespaceAndPath(id.namespace, "block/${id.path}_still")

                override fun getFlowingTexture() =
                    ResourceLocation.fromNamespaceAndPath(id.namespace, "block/${id.path}_flow")
            },
            fluid.fluidType,
        )
    }

    registerFluidTextures(ProjectIFluids.moltenBlueCrystal)
    registerFluidTextures(ProjectIFluids.moltenYellowCrystal)
    registerFluidTextures(ProjectIFluids.moltenGlass)
}
