package net.msrandom.divinity.mixin

import net.minecraft.world.entity.item.ItemEntity
import net.msrandom.divinity.world.level.melting.MeltEventHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ItemEntity::class)
class ItemEntityMeltingMixin {
    @Inject(at = [At("HEAD")], method = ["tick"], cancellable = true)
    private fun onTick(callbackInfo: CallbackInfo) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        MeltEventHandler.handleItemTick(this as ItemEntity)
    }
}
