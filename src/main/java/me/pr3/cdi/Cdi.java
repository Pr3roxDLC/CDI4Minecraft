package me.pr3.cdi;

import me.pr3.cdi.extensions.events.EventManager;
import me.pr3.cdi.extensions.events.EventSystemExtension;
import me.pr3.cdi.extensions.settings.SettingsExtension;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static me.pr3.cdi.extensions.events.annotations.filters.If.PLAYER_NON_NULL;

@Mod(
        modid = Cdi.MOD_ID,
        name = Cdi.MOD_NAME,
        version = Cdi.VERSION
)
public class Cdi {

    public static final String MOD_ID = "cdi";
    public static final String MOD_NAME = "CDIForMinecraft";
    public static final String VERSION = "1.0";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static Cdi INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        EntryPoint.init(new EventSystemExtension());
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }
}
