package brstudio.godzin.shadowindicator.util.handlers;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RegistryHandler {

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
    }
}
