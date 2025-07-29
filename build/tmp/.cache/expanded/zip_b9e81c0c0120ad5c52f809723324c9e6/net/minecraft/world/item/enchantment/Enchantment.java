package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Enchantment implements FeatureElement, net.minecraftforge.common.extensions.IForgeEnchantment {
    private final Enchantment.EnchantmentDefinition definition;
    @Nullable
    protected String descriptionId;
    private final Holder.Reference<Enchantment> builtInRegistryHolder = BuiltInRegistries.ENCHANTMENT.createIntrusiveHolder(this);

    public static Enchantment.Cost constantCost(int p_334530_) {
        return new Enchantment.Cost(p_334530_, 0);
    }

    public static Enchantment.Cost dynamicCost(int p_334326_, int p_335507_) {
        return new Enchantment.Cost(p_334326_, p_335507_);
    }

    public static Enchantment.EnchantmentDefinition definition(
        TagKey<Item> p_329090_,
        TagKey<Item> p_332240_,
        int p_328611_,
        int p_336009_,
        Enchantment.Cost p_330605_,
        Enchantment.Cost p_333983_,
        int p_327771_,
        EquipmentSlot... p_329538_
    ) {
        return new Enchantment.EnchantmentDefinition(
            p_329090_, Optional.of(p_332240_), p_328611_, p_336009_, p_330605_, p_333983_, p_327771_, FeatureFlags.DEFAULT_FLAGS, p_329538_
        );
    }

    public static Enchantment.EnchantmentDefinition definition(
        TagKey<Item> p_334656_, int p_335023_, int p_332990_, Enchantment.Cost p_328936_, Enchantment.Cost p_332239_, int p_332354_, EquipmentSlot... p_334822_
    ) {
        return new Enchantment.EnchantmentDefinition(
            p_334656_, Optional.empty(), p_335023_, p_332990_, p_328936_, p_332239_, p_332354_, FeatureFlags.DEFAULT_FLAGS, p_334822_
        );
    }

    public static Enchantment.EnchantmentDefinition definition(
        TagKey<Item> p_335329_,
        int p_329635_,
        int p_331888_,
        Enchantment.Cost p_328182_,
        Enchantment.Cost p_328787_,
        int p_333931_,
        FeatureFlagSet p_330633_,
        EquipmentSlot... p_330676_
    ) {
        return new Enchantment.EnchantmentDefinition(p_335329_, Optional.empty(), p_329635_, p_331888_, p_328182_, p_328787_, p_333931_, p_330633_, p_330676_);
    }

    @Nullable
    public static Enchantment byId(int p_44698_) {
        return BuiltInRegistries.ENCHANTMENT.byId(p_44698_);
    }

    public Enchantment(Enchantment.EnchantmentDefinition p_327760_) {
        this.definition = p_327760_;
    }

    public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity p_44685_) {
        Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

        for (EquipmentSlot equipmentslot : this.definition.slots()) {
            ItemStack itemstack = p_44685_.getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
                map.put(equipmentslot, itemstack);
            }
        }

        return map;
    }

    public final TagKey<Item> getSupportedItems() {
        return this.definition.supportedItems();
    }

    public final boolean isPrimaryItem(ItemStack p_334183_) {
        return this.definition.primaryItems.isEmpty() || p_334183_.is(this.definition.primaryItems.get());
    }

    public final int getWeight() {
        return this.definition.weight();
    }

    public final int getAnvilCost() {
        return this.definition.anvilCost();
    }

    public final int getMinLevel() {
        return 1;
    }

    public final int getMaxLevel() {
        return this.definition.maxLevel();
    }

    public final int getMinCost(int p_44679_) {
        return this.definition.minCost().calculate(p_44679_);
    }

    public final int getMaxCost(int p_44691_) {
        return this.definition.maxCost().calculate(p_44691_);
    }

    public int getDamageProtection(int p_44680_, DamageSource p_44681_) {
        return 0;
    }

    @Deprecated // Forge: Use ItemStack aware version in IForgeEnchantment
    public float getDamageBonus(int p_44682_, @Nullable EntityType<?> p_331633_) {
        return 0.0F;
    }

    public final boolean isCompatibleWith(Enchantment p_44696_) {
        return this.checkCompatibility(p_44696_) && p_44696_.checkCompatibility(this);
    }

    protected boolean checkCompatibility(Enchantment p_44690_) {
        return this != p_44690_;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getFullname(int p_44701_) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        if (this.isCurse()) {
            mutablecomponent.withStyle(ChatFormatting.RED);
        } else {
            mutablecomponent.withStyle(ChatFormatting.GRAY);
        }

        if (p_44701_ != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + p_44701_));
        }

        return mutablecomponent;
    }

    public boolean canEnchant(ItemStack p_44689_) {
        return p_44689_.getItem().builtInRegistryHolder().is(this.definition.supportedItems());
    }

    public void doPostAttack(LivingEntity p_44686_, Entity p_44687_, int p_44688_) {
    }

    public void doPostHurt(LivingEntity p_44692_, Entity p_44693_, int p_44694_) {
    }

    public void doPostItemStackHurt(LivingEntity p_335453_, Entity p_329978_, int p_331186_) {
    }

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTradeable() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    @Deprecated
    public Holder.Reference<Enchantment> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.definition.requiredFeatures();
    }

    public static record Cost(int base, int perLevel) {
        public int calculate(int p_333351_) {
            return this.base + this.perLevel * (p_333351_ - 1);
        }
    }

    public static record EnchantmentDefinition(
        TagKey<Item> supportedItems,
        Optional<TagKey<Item>> primaryItems,
        int weight,
        int maxLevel,
        Enchantment.Cost minCost,
        Enchantment.Cost maxCost,
        int anvilCost,
        FeatureFlagSet requiredFeatures,
        EquipmentSlot[] slots
    ) {
    }
}
