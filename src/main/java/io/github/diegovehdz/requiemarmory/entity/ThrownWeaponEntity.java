package io.github.diegovehdz.requiemarmory.entity;

import javax.annotation.Nullable;

import io.github.diegovehdz.requiemarmory.weapon.WeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A thrown melee weapon. Trident-like: it flies, sticks, and can be picked back up (there is no
 * ammo — the projectile <i>is</i> the weapon). Loyalty/riptide integration can be added later.
 */
public class ThrownWeaponEntity extends AbstractArrow {
    private boolean dealtDamage;

    public ThrownWeaponEntity(EntityType<? extends ThrownWeaponEntity> type, Level level) {
        super(type, level);
    }

    public ThrownWeaponEntity(Level level, LivingEntity shooter, ItemStack weapon) {
        super(ModEntities.THROWN_WEAPON.get(), shooter, level, weapon, weapon);
        this.pickup = AbstractArrow.Pickup.ALLOWED;
    }

    private float throwDamage() {
        return getWeaponItem().getItem() instanceof WeaponItem weapon
                ? weapon.abilities().throwDamage
                : 5.0f;
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        super.tick();
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().trident(this, owner == null ? this : owner);

        float damage = throwDamage();
        if (this.level() instanceof ServerLevel serverLevel) {
            damage = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), target, source, damage);
        }

        this.dealtDamage = true;
        if (target.hurt(source, damage) && target instanceof LivingEntity living) {
            this.doKnockback(living, source);
            this.doPostHurtEffects(living);
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.STICK);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    /** The stack the client should render as the flying weapon. */
    public ItemStack getRenderStack() {
        return getWeaponItem();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.dealtDamage = tag.getBoolean("DealtDamage");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("DealtDamage", this.dealtDamage);
    }
}
