package brstudio.godzin.shadowindicator;

import brstudio.godzin.shadowindicator.config.ModConfig;
import brstudio.godzin.shadowindicator.proxy.CommonProxy;
import brstudio.godzin.shadowindicator.util.handlers.RegistryHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import brstudio.godzin.shadowindicator.util.References;

import static brstudio.godzin.shadowindicator.util.References.MODID;

@Mod(modid = MODID, name = References.NAME, version = "0.0.1")
public class ShadowIndicator {

    @Mod.Instance
    public static ShadowIndicator instance;


    @SidedProxy(clientSide = References.CLIENT, serverSide = References.COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(RegistryHandler.class);
        ModConfig.loadConfig();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event){
    }

}
