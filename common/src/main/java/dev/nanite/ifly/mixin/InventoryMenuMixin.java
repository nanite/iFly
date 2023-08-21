package dev.nanite.ifly.mixin;

import com.mojang.datafixers.util.Pair;
import dev.nanite.ifly.item.FlyItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<TransientCraftingContainer> {

    public InventoryMenuMixin(MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Inventory inventory, boolean bl, Player player, CallbackInfo ci){
        int flyItemSlotX = 0;
        int flyItemSlotY = 0;

        this.addSlot(new Slot(inventory, 41, 77 + flyItemSlotX, 44 + flyItemSlotY){
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack itemStack) {
//                todo check if its allowed blah blah blah netowrk
                if (itemStack.getItem() instanceof FlyItem)
                    return true;
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                ItemStack itemStack = this.getItem();
                return !itemStack.isEmpty() && player.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack) ? false : super.mayPickup(player);
            }

            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }
}
