package net.twoweek.tutorialcraft.items;

import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.twoweek.tutorialcraft.TutorialCraft;

public class ModItems {
    // A deferred register is a long list of things (items) and they will be registered
    // when forge registers the items
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TutorialCraft.MOD_ID);

    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire",
            () -> new Item(new Item.Properties()));

    //
    // TOOLS
    //
    public static final RegistryObject<Item> RUBY_SWORD   = ITEMS.register("ruby_sword",
            () -> new SwordItem(ModToolTiers.RUBY,   3,   -2.4F, new Item.Properties()));

    public static final RegistryObject<Item> RUBY_PICKAXE = ITEMS.register("ruby_pickaxe",
            () -> new PickaxeItem(ModToolTiers.RUBY, 1,   -2.8F, new Item.Properties()));

    public static final RegistryObject<Item> RUBY_AXE     = ITEMS.register("ruby_axe",
            () -> new AxeItem(ModToolTiers.RUBY,     5.0F, -3.0F, new Item.Properties()));

    public static final RegistryObject<Item> RUBY_SHOVEL  = ITEMS.register("ruby_shovel",
            () -> new ShovelItem(ModToolTiers.RUBY,  1.5F, -3.0F, new Item.Properties()));

    public static final RegistryObject<Item> RUBY_HOE     = ITEMS.register("ruby_hoe",
            () -> new HoeItem(ModToolTiers.RUBY,     -3,   0.0F,  new Item.Properties()));


    //
    // ARMOR
    //
    public static final RegistryObject<Item> RUBY_HELMET = ITEMS.register("ruby_helmet",
            () -> new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> RUBY_CHESTPLATE = ITEMS.register("ruby_chestplate",
            () -> new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> RUBY_LEGGINGS = ITEMS.register("ruby_leggings",
            () -> new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> RUBY_BOOTS = ITEMS.register("ruby_boots",
            () -> new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
