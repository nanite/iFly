package dev.nanite.ifly.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin implements Container {
    @Mutable
    @Shadow
    @Final
    private List<NonNullList<ItemStack>> compartments;
    private NonNullList<ItemStack> flyItemSlot;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(Player player, CallbackInfo ci) {
        this.flyItemSlot = NonNullList.of(ItemStack.EMPTY);

        this.compartments = new ArrayList<>(compartments);
        this.compartments.add(flyItemSlot);
        this.compartments = ImmutableList.copyOf(this.compartments);
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void save(ListTag listTag, CallbackInfoReturnable<ListTag> cir){
        if (!this.flyItemSlot.isEmpty()){
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte) (110));
            this.flyItemSlot.get(0).save(compoundTag);
            listTag.add(compoundTag);
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    public void load(ListTag listTag, CallbackInfo ci){
        this.flyItemSlot.clear();
        for (int i = 0; i < listTag.size(); i++){
            CompoundTag compoundTag = listTag.getCompound(1);
            int slot = compoundTag.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.of(compoundTag);
            if (!itemStack.isEmpty()){
                if (slot >= 110 && slot < this.flyItemSlot.size() + 110) {
                    this.flyItemSlot.set(slot - 110, itemStack);
                }
            }
        }
    }
}
