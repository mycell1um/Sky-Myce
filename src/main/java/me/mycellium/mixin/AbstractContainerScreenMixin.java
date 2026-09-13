package me.mycellium.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.mycellium.skymyce.api.events.RenderSlotEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @WrapOperation(method = "extractSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V"))
    public void extractSlot(AbstractContainerScreen instance, GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, Operation<Void> original) {
        new RenderSlotEvent.Before(graphics, slot).post(SkyBlockAPI.getEventBus());
        original.call(instance, graphics, slot, mouseX, mouseY);
        new RenderSlotEvent.After(graphics, slot).post(SkyBlockAPI.getEventBus());
    }
}
