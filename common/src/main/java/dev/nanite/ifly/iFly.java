package dev.nanite.ifly;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.nanite.ifly.items.Items;
import dev.nanite.ifly.trims.FlyTrim;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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
        LifecycleEvent.SETUP.register(iFly::onSetup);
    }

    private static void onSetup() {
        CreativeTabRegistry.append(CreativeModeTabs.INGREDIENTS, Items.FLY_ITEM.get());
    }

    static ObjectSet<UUID> alreadyFlying = new ObjectOpenHashSet<>();
    static ObjectSet<UUID> weMadeFlying = new ObjectOpenHashSet<>();

    public static void canFly(RegistryAccess levelRegistryAccess, Entity entity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot){
        if (equipmentSlot != null && !equipmentSlot.isArmor()) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        // Maintain safe state
        if (player.getAbilities().mayfly && !weMadeFlying.contains(player.getUUID())) {
            alreadyFlying.add(player.getUUID());
        } else if (!player.getAbilities().mayfly && weMadeFlying.contains(player.getUUID())) {
            alreadyFlying.remove(player.getUUID());
        }

        boolean playerHasTrim = false;
        for (ItemStack armourStack : player.getInventory().armor) {
            if (armourStack.isEmpty()) continue;
            Optional<ArmorTrim> armorTrim = ArmorTrim.getTrim(levelRegistryAccess, armourStack);
            if (armorTrim.isEmpty()) continue;
            if (armorTrim.get().pattern().is(FlyTrim.FLY_TRIM)){
                playerHasTrim = true;
            }
        }

        if (playerHasTrim) {
            weMadeFlying.add(player.getUUID());
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        } else {
            if (alreadyFlying.contains(player.getUUID()) && !weMadeFlying.contains(player.getUUID())) return;

            weMadeFlying.remove(player.getUUID());
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}
