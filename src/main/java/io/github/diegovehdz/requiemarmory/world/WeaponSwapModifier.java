package io.github.diegovehdz.requiemarmory.world;

import java.util.List;
import java.util.Map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Swaps vanilla weapons rolled by any loot table for one of this mod's equivalents at the same
 * material and in the same family, so the arsenal turns up in chests rather than being craft-only. An
 * iron sword in a dungeon chest becomes an iron saber, katana, rapier…; a diamond axe becomes a
 * diamond battle axe or hatchet; a bow becomes a longbow.
 *
 * <p>Written as a swap rather than as extra pool entries so the amount of loot a chest yields does not
 * change, and so it applies to modded and datapack loot tables too without naming any of them.</p>
 */
public class WeaponSwapModifier extends LootModifier {
    public static final MapCodec<WeaponSwapModifier> CODEC = RecordCodecBuilder.mapCodec(
            instance -> codecStart(instance).apply(instance, WeaponSwapModifier::new));

    /** Vanilla weapon → the shapes it may become. The material is read off the vanilla item's name. */
    private static final Map<String, List<String>> FAMILIES = Map.of(
            "sword", List.of("dagger", "rapier", "saber", "katana", "greatsword", "longsword"),
            "axe", List.of("battle_axe", "hatchet"));

    public WeaponSwapModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!RequiemArmoryConfig.COMMON_SPEC.isLoaded() || !RequiemArmoryConfig.COMMON.weaponsInLoot.get()) {
            return generatedLoot;
        }
        float chance = RequiemArmoryConfig.COMMON.weaponsInLootChance.get().floatValue();
        if (chance <= 0.0f) {
            return generatedLoot;
        }
        // Chests only. Without this the modifier would run against every block, mob, fishing and
        // bartering roll in the game — the request was for chest loot, and staying out of the other
        // tables avoids surprising interactions (and pointless work on every broken block).
        if (!context.getQueriedLootTableId().getPath().startsWith("chests/")) {
            return generatedLoot;
        }

        RandomSource random = context.getRandom();
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            List<String> shapes = shapesFor(stack);
            if (shapes == null || random.nextFloat() >= chance) {
                continue;
            }
            Item replacement = WeaponPools.pick(random, materialsFor(stack), shapes).orElse(null);
            if (replacement == null) {
                continue;
            }
            // Carry enchantments and the custom name across; a swapped-in weapon should not be a
            // downgrade on an enchanted dungeon find.
            ItemStack swapped = stack.transmuteCopy(replacement, 1);
            generatedLoot.set(i, swapped);
        }
        return generatedLoot;
    }

    /** The shape list this stack may turn into, or null if it is not a weapon we substitute for. */
    private static List<String> shapesFor(ItemStack stack) {
        if (stack.is(Items.BOW)) {
            return List.of("longbow");
        }
        if (stack.is(Items.CROSSBOW)) {
            return List.of("heavy_crossbow");
        }
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        for (Map.Entry<String, List<String>> family : FAMILIES.entrySet()) {
            if (path.endsWith("_" + family.getKey())) {
                return family.getValue();
            }
        }
        return null;
    }

    /** The material to stay within: the vanilla item's own prefix, or wooden for the bow/crossbow. */
    private static List<String> materialsFor(ItemStack stack) {
        if (stack.is(Items.BOW) || stack.is(Items.CROSSBOW)) {
            return List.of("wooden");
        }
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        int split = path.lastIndexOf('_');
        return split < 0 ? List.of() : List.of(path.substring(0, split));
    }

    @Override
    public MapCodec<? extends net.neoforged.neoforge.common.loot.IGlobalLootModifier> codec() {
        return CODEC;
    }
}
