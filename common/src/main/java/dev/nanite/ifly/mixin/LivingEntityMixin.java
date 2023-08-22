package dev.nanite.ifly.mixin;

import dev.nanite.ifly.iFly;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;equipmentHasChanged(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onArmorChange(CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir, Map map, EquipmentSlot[] equipmentSlots, int slotSize, int slotIndex, EquipmentSlot equipmentSlot, ItemStack itemStack, ItemStack itemStack2) {
        RegistryAccess levelRegistryAccess = level().registryAccess();
        iFly.canFly(levelRegistryAccess, this, itemStack2, equipmentSlot);
    }
}
