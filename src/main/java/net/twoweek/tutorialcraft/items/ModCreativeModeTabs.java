package net.twoweek.tutorialcraft.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.twoweek.tutorialcraft.TutorialCraft;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATITVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TutorialCraft.MOD_ID);

    // creating the custom tab in our minecraft menu
    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATITVE_MODE_TABS.register("tutorial_tab",
            () -> CreativeModeTab.builder()
                    // add a icon to the tab
                    .icon(() -> new ItemStack(ModItems.RUBY.get()))
                    // connect the en_us translation to the title
                    .title(Component.translatable("creativetab.tutorial_tab"))
                    // display the two items of our mod
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.RUBY.get());
                        pOutput.accept(ModItems.SAPPHIRE.get());
                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATITVE_MODE_TABS.register(eventBus);
    }
}
