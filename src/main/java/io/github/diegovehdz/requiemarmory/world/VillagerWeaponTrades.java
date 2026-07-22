package io.github.diegovehdz.requiemarmory.world;

import java.util.List;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Lets the relevant villagers deal in this mod's weapons. Each listing is added to the same career
 * level as the vanilla weapon it stands in for; because a villager rolls only a couple of listings per
 * level, adding to the pool means a given weaponsmith sells <em>either</em> the vanilla weapon or one
 * of ours — which is the "in place of" behaviour, without removing vanilla trades outright.
 */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID)
public final class VillagerWeaponTrades {
    private VillagerWeaponTrades() {}

    @SubscribeEvent
    static void onVillagerTrades(VillagerTradesEvent event) {
        if (!RequiemArmoryConfig.COMMON_SPEC.isLoaded() || !RequiemArmoryConfig.COMMON.weaponsInTrades.get()) {
            return;
        }
        VillagerProfession profession = event.getType();

        if (profession == VillagerProfession.WEAPONSMITH) {
            // Mirrors vanilla's iron sword at level 3 and diamond sword at level 4.
            add(event, 3, new WeaponForEmeralds(List.of("iron"), SWORDS, 7, 12, 1, 5));
            add(event, 4, new WeaponForEmeralds(List.of("diamond"), SWORDS, 17, 27, 1, 15));
        } else if (profession == VillagerProfession.TOOLSMITH) {
            add(event, 3, new WeaponForEmeralds(List.of("iron"), AXES, 6, 11, 1, 5));
            add(event, 4, new WeaponForEmeralds(List.of("diamond"), AXES, 16, 26, 1, 15));
        } else if (profession == VillagerProfession.FLETCHER) {
            // Vanilla fletchers sell a bow at 2 and a crossbow at 4.
            add(event, 2, new WeaponForEmeralds(List.of("wooden", "iron"), List.of("longbow"), 7, 12, 1, 5));
            add(event, 4, new WeaponForEmeralds(List.of("wooden", "iron"), List.of("heavy_crossbow"), 11, 17, 1, 15));
        }
    }

    private static final List<String> SWORDS =
            List.of("dagger", "rapier", "saber", "katana", "greatsword", "longsword");
    private static final List<String> AXES = List.of("battle_axe", "hatchet");

    private static void add(VillagerTradesEvent event, int level, VillagerTrades.ItemListing listing) {
        event.getTrades().get(level).add(listing);
    }

    /**
     * Sells one random enabled weapon from a pool for a randomised emerald price. Returning {@code null}
     * when the pool is empty makes the villager simply roll a different listing, so switching every
     * candidate off in the config degrades gracefully instead of producing a broken trade.
     */
    private record WeaponForEmeralds(List<String> materials, List<String> shapes,
                                     int minCost, int maxCost, int maxUses, int villagerXp)
            implements VillagerTrades.ItemListing {

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            var weapon = WeaponPools.pick(random, materials, shapes).orElse(null);
            if (weapon == null) {
                return null;
            }
            int cost = minCost + random.nextInt(Math.max(1, maxCost - minCost + 1));
            return new MerchantOffer(new net.minecraft.world.item.trading.ItemCost(Items.EMERALD, cost),
                    new ItemStack(weapon), maxUses, villagerXp, 0.2F);
        }
    }
}
