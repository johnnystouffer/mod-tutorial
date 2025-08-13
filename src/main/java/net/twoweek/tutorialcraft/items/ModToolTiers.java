package net.twoweek.tutorialcraft.items;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public enum ModToolTiers implements Tier {
    RUBY(
            1850, // uses (durability)
            8.8F,       // mining speed
            3.2F,       // base attack damage bonus
            3,          // harvest level (diamond-like)
            15,         // enchantability
            () -> Ingredient.of(ModItems.RUBY.get()) // repair material
    );

    private final int uses, level, enchant;
    private final float speed, attack;
    private final java.util.function.Supplier<Ingredient> repair;

    ModToolTiers(int uses, float speed, float attack, int level, int enchant,
                 java.util.function.Supplier<Ingredient> repair) {
        this.uses = uses; this.speed = speed; this.attack = attack;
        this.level = level; this.enchant = enchant; this.repair = repair;
    }
    @Override public int getUses() { return uses; }
    @Override public float getSpeed() { return speed; }
    @Override public float getAttackDamageBonus() { return attack; }
    @Override public int getLevel() { return level; }
    @Override public int getEnchantmentValue() { return enchant; }
    @Override public @NotNull Ingredient getRepairIngredient() { return repair.get(); }
}
