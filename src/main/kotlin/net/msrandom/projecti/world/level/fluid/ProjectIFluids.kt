package net.msrandom.projecti.world.level.fluid

import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.pathfinder.PathType
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.getValue
import net.msrandom.projecti.world.Registrar
import net.msrandom.projecti.world.item.ProjectIItems
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import net.neoforged.neoforge.common.SoundActions
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ProjectIFluids : Registrar<Fluid> {
    override val register: DeferredRegister<Fluid> =
        DeferredRegister.create(Registries.FLUID, ProjectI.MOD_ID)

    val fluidTypeRegister: DeferredRegister<FluidType> =
        DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ProjectI.MOD_ID)

    val moltenBlueCrystal: FlowingFluid by register("molten_blue_crystal", ProjectIBlocks::moltenBlueCrystal)
    val moltenYellowCrystal: FlowingFluid by register("molten_yellow_crystal", ProjectIBlocks::moltenYellowCrystal)
    val moltenGlass: FlowingFluid by register("molten_glass", ProjectIBlocks::moltenGlass)

    private fun register(name: String, liquidBlock: () -> LiquidBlock): DeferredHolder<Fluid, BaseFlowingFluid.Source> {
        val fluidType = fluidTypeRegister.register(name) { ->
            FluidType(
                FluidType.Properties
                    .create()
                    .descriptionId("block.${ProjectI.MOD_ID}.$name")
                    .canSwim(false)
                    .canDrown(false)
                    .pathType(PathType.LAVA)
                    .adjacentPathType(null)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .lightLevel(10)
                    .density(3000)
                    .viscosity(6000)
                    .temperature(1000)
            )
        }

        val fluid = object {
            val source: DeferredHolder<Fluid, BaseFlowingFluid.Source> = register.register(name) { ->
                BaseFlowingFluid.Source(fluidProperties)
            }

            val flowing: DeferredHolder<Fluid, BaseFlowingFluid.Flowing> = register.register("flowing_$name") { ->
                BaseFlowingFluid.Flowing(fluidProperties)
            }

            val bucket: DeferredHolder<Item, BucketItem> = ProjectIItems.register.registerItem("${name}_bucket", {
                BucketItem(source.get(), it)
            }, Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))

            val fluidProperties = BaseFlowingFluid.Properties(fluidType, source, flowing).block(liquidBlock).bucket(bucket)
        }.source

        return fluid
    }
}
