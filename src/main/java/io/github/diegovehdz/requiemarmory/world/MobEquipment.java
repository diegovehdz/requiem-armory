package io.github.diegovehdz.requiemarmory.world;

import java.util.List;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

/**
 * Gives a small share of newly spawned mobs one of this mod's weapons, in the spirit of vanilla's own
 * "armed zombie" roll — the chance scales with the chunk's difficulty, so it stays rare on easy and
 * gets noticeable on hard.
 *
 * <p><b>Timing:</b> {@link FinalizeSpawnEvent} fires <em>before</em> {@code Mob#finalizeSpawn}, which
 * is where vanilla rolls its own equipment — arming the mob inside the handler would just be
 * overwritten. The swap is therefore queued onto the server thread and runs once finalizeSpawn has
 * returned.</p>
 *
 * <p><b>Mob AI:</b> skeletons and pillagers keep their ranged behaviour with our bows and crossbows —
 * NeoForge patches the skeleton's weapon-goal check, and the crossbow goal was always
 * {@code instanceof}-based. The only vanilla check that is still item-identity is
 * {@code canFireProjectileWeapon}, which merely governs picking a <em>dropped</em> ranged weapon up.</p>
 */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID)
public final class MobEquipment {
    private MobEquipment() {}

    /** What a given mob may be handed, and how likely that is at difficulty multiplier 1.0. */
    private record Loadout(float chance, List<String> materials, List<String> shapes) {}

    private static final List<String> STONE_IRON = List.of("stone", "iron");
    private static final List<String> GOLD = List.of("golden");

    // Rarer, tougher mobs get a higher roll — you meet far fewer brutes than zombies.
    private static final Loadout ZOMBIE = new Loadout(0.05f, STONE_IRON,
            List.of("dagger", "saber", "mace", "hatchet", "spear"));
    private static final Loadout SKELETON = new Loadout(0.05f, List.of("wooden", "iron"),
            List.of("bow", "longbow"));
    private static final Loadout WITHER_SKELETON = new Loadout(0.10f, List.of("stone"),
            List.of("spear", "halberd", "glaive", "warhammer"));
    private static final Loadout PILLAGER = new Loadout(0.05f, List.of("wooden", "iron"),
            List.of("crossbow", "heavy_crossbow"));
    private static final Loadout VINDICATOR = new Loadout(0.05f, STONE_IRON,
            List.of("battle_axe", "hatchet"));
    private static final Loadout PIGLIN = new Loadout(0.05f, GOLD,
            List.of("dagger", "saber", "mace", "spear"));
    private static final Loadout PIGLIN_BRUTE = new Loadout(0.10f, GOLD,
            List.of("battle_axe", "mace", "warhammer"));

    @SubscribeEvent
    static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (!RequiemArmoryConfig.COMMON_SPEC.isLoaded() || !RequiemArmoryConfig.COMMON.armMobs.get()) {
            return;
        }
        Mob mob = event.getEntity();
        Loadout loadout = loadoutFor(mob);
        if (loadout == null) {
            return;
        }

        float chance = loadout.chance()
                * event.getDifficulty().getSpecialMultiplier()
                * RequiemArmoryConfig.COMMON.armMobsChanceMultiplier.get().floatValue();
        if (mob.getRandom().nextFloat() >= chance) {
            return;
        }

        MinecraftServer server = event.getLevel().getLevel().getServer();
        server.execute(() -> arm(mob, loadout));
    }

    /** Which table applies, or null for a mob we leave alone. Order matters: several of these extend
     *  each other ({@code ZombifiedPiglin} and {@code Drowned} are both zombies, {@code WitherSkeleton}
     *  is a skeleton), so the specific cases have to be tested before the general ones. */
    private static Loadout loadoutFor(Mob mob) {
        if (mob instanceof PiglinBrute) return PIGLIN_BRUTE;
        if (mob instanceof Piglin) return PIGLIN;
        if (mob instanceof ZombifiedPiglin) return PIGLIN;
        if (mob instanceof Drowned) return null;           // the trident is their whole identity
        if (mob instanceof Zombie) return ZOMBIE;
        if (mob instanceof WitherSkeleton) return WITHER_SKELETON;
        if (mob instanceof AbstractSkeleton) return SKELETON;
        if (mob instanceof Pillager) return PILLAGER;
        if (mob instanceof Vindicator) return VINDICATOR;
        return null;
    }

    private static void arm(Mob mob, Loadout loadout) {
        if (!mob.isAlive() || mob.isRemoved()) {
            return;
        }
        Item weapon = WeaponPools.pick(mob.getRandom(), loadout.materials(), loadout.shapes()).orElse(null);
        if (weapon == null) {
            return; // every candidate disabled in the config
        }

        // Straight overwrite, as vanilla's own equipment roll does. Whatever was there was created
        // moments ago by finalizeSpawn and has never been in the world, so there is nothing to salvage
        // — dropping it would just litter every spawn point. The slot keeps its vanilla drop chance.
        mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));

        // A skeleton picks melee or bow goals from what it is holding; re-run that now the hand changed.
        if (mob instanceof AbstractSkeleton skeleton) {
            skeleton.reassessWeaponGoal();
        }
    }
}
