package net.twoweek.tutorialcraft.items;

import net.minecraft.world.item.Item;
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

    // Ruby is added to the game, with no texture, no name, not even in creative mode
    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
