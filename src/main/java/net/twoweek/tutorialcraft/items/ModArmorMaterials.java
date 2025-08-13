// ModArmorMaterials.java
package net.twoweek.tutorialcraft.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.twoweek.tutorialcraft.TutorialCraft;
import org.jetbrains.annotations.NotNull;

public enum ModArmorMaterials implements ArmorMaterial {
    RUBY(
            new int[]{3, 6, 8, 3},  // protection for BOOTS, LEGGINGS, CHESTPLATE, HELMET? (see note below)
            15,                     // enchantment
            2.5F,                   // toughness
            0.5F,                   // knockback resistance
            "ruby",                 // texture name key
            Ingredient.EMPTY        // set to Ingredient.of(ModItems.RUBY.get()) or similar
    );

    private static final java.util.Map<ArmorItem.Type, Integer> TYPE_INDEX = java.util.Map.of(
            ArmorItem.Type.BOOTS, 0,
            ArmorItem.Type.LEGGINGS, 1,
            ArmorItem.Type.CHESTPLATE, 2,
            ArmorItem.Type.HELMET, 3
    );

    private final int[] protectionByType;
    private final int enchantmentValue;
    private final float toughness;
    private final float knockbackRes;
    private final String nameKey;
    private final Ingredient repair;

    ModArmorMaterials(int[] protectionByType, int enchantValue, float toughness, float knockbackRes,
                      String nameKey, Ingredient repair) {
        this.protectionByType = protectionByType;
        this.enchantmentValue = enchantValue;
        this.toughness = toughness;
        this.knockbackRes = knockbackRes;
        this.nameKey = nameKey;
        this.repair = repair;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        int base;
        switch (type) {
            case BOOTS -> base = 13;
            case LEGGINGS -> base = 15;
            case CHESTPLATE -> base = 16;
            case HELMET -> base = 11;
            default -> base = 0;
        }
        int durabilityFactor = 33;
        return base * durabilityFactor;
    }

    @Override
    public int getDefenseForType(ArmorItem.@NotNull Type type) {
        return protectionByType[TYPE_INDEX.get(type)];
    }

    @Override
    public int getEnchantmentValue() { return enchantmentValue; }

    @Override
    public net.minecraft.sounds.@NotNull SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() { return repair; }

    @Override
    public @NotNull String getName() {
        return ResourceLocation.fromNamespaceAndPath(TutorialCraft.MOD_ID, nameKey).toString();
    }

    @Override
    public float getToughness() { return toughness; }

    @Override
    public float getKnockbackResistance() { return knockbackRes; }
}
