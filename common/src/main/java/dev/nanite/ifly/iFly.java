package dev.nanite.ifly;

import dev.nanite.ifly.items.Items;
import dev.nanite.ifly.trims.FlyTrim;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.Optional;

public class iFly {
    public static final String MOD_ID = "ifly";

//    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(iFly.MOD_ID, Registries.CREATIVE_MODE_TAB);

//    public static final RegistrySupplier<CreativeModeTab> TAB = CREATIVE_TAB.register(iFly.MOD_ID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
//            .title(Component.translatable("itemGroup.ifly.creative_tab"))
//            .displayItems(
//                    (itemDisplayParameters, output) -> {
//                        Items.ITEMS.forEach(e -> output.accept(e.get()));
//                    })
//            .icon(() -> new ItemStack(Items.FLY_ITEM.get())).build());
    
    public static void init() {
        Items.ITEMS.register();
    }


    public static void canFly(RegistryAccess levelRegistryAccess, Entity entity, ItemStack itemStack, EquipmentSlot equipmentSlot){
        if (equipmentSlot.isArmor()){
            if (entity instanceof Player player){
                Optional<ArmorTrim> trim = ArmorTrim.getTrim(levelRegistryAccess, itemStack);
                if (trim.isPresent()) {
                    ArmorTrim armorTrim = trim.get();
                    if (armorTrim.pattern().is(FlyTrim.FLY_TRIM)) {
                        player.getAbilities().mayfly = true;
                        player.onUpdateAbilities();
                    }
                }else{
                    boolean keepFlight = false;
                    for (ItemStack armourStack : player.getInventory().armor) {
                        if (armourStack.isEmpty()) continue;
                        Optional<ArmorTrim> trim1 = ArmorTrim.getTrim(levelRegistryAccess, armourStack);
                        if (trim1.isEmpty()) continue;
                        if (trim1.get().pattern().is(FlyTrim.FLY_TRIM)){
                            keepFlight = true;
                        }
                    }
                    if (!keepFlight){
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }
                }
            }
        }
    }
}
