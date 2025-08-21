package net.twoweek.tutorialcraft.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.twoweek.tutorialcraft.TutorialCraft;
import net.twoweek.tutorialcraft.items.ModItems;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TutorialCraft.MOD_ID);

    public static final RegistryObject<Block> RUBY_ORE = registerBlock(
            "ruby_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE))
    );
    public static final RegistryObject<Block> SAPPHIRE_ORE = registerBlock(
            "sapphire_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .friction(Blocks.ICE.getFriction())
                    .sound(SoundType.AMETHYST)
                    .strength(6.0F, 6.5F))
    );

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> supplier) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        registerBlockItem(name, block);
        return block;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
