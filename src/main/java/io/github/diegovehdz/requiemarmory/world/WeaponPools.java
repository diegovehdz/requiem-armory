package io.github.diegovehdz.requiemarmory.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import io.github.diegovehdz.requiemarmory.registry.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

/**
 * Picks weapons for the systems that hand them out at runtime — mob equipment, chest loot and
 * villager trades. A pool is a material list × shape list cross product; combinations that were never
 * registered (there is no stone bow, and no wooden bow of ours — that one is vanilla) and anything
 * switched off in the config simply drop out.
 *
 * <p>Resolved on every call rather than cached: the config can change between calls, and the lists
 * involved are a handful of entries.</p>
 */
public final class WeaponPools {
    private WeaponPools() {}

    /** Every registered, config-enabled item in the {@code materials × shapes} cross product. */
    public static List<Item> resolve(List<String> materials, List<String> shapes) {
        List<Item> pool = new ArrayList<>();
        for (String shape : shapes) {
            for (String material : materials) {
                String path = material + "_" + shape;
                if (RequiemArmoryConfig.isWeaponEnabled(path)) {
                    ModItems.find(path).ifPresent(pool::add);
                }
            }
        }
        return pool;
    }

    /** One random item from the pool, or empty when every candidate is missing or disabled. */
    public static Optional<Item> pick(RandomSource random, List<String> materials, List<String> shapes) {
        List<Item> pool = resolve(materials, shapes);
        return pool.isEmpty() ? Optional.empty() : Optional.of(pool.get(random.nextInt(pool.size())));
    }
}
