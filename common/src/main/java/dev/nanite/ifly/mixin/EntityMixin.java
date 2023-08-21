package dev.nanite.ifly.mixin;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getAllSlots", at = @At("RETURN"), cancellable = true)
    public void getAllSlots(CallbackInfoReturnable<Iterable<ItemStack>> cir){
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player){
            Player player = (Player) entity;
            ItemStack flyItemSlotStack = player.getInventory().getItem(41);
            Iterable<ItemStack> equippedItems = cir.getReturnValue();
            Iterable<ItemStack> equippedBackSlotItems = Arrays.asList(flyItemSlotStack);
            cir.setReturnValue(Iterables.concat(equippedItems, equippedBackSlotItems));
        }
    }
}
