package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.google.common.base.Stopwatch;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModelHandler {
    private static final Logger LOGGER = LogManager.getLogger(IronJetpacks.NAME);

    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(new ResourceLocation(IronJetpacks.MOD_ID, "item/cell"));
        ModelLoader.addSpecialModel(new ResourceLocation(IronJetpacks.MOD_ID, "item/capacitor"));
        ModelLoader.addSpecialModel(new ResourceLocation(IronJetpacks.MOD_ID, "item/thruster"));
        ModelLoader.addSpecialModel(new ResourceLocation(IronJetpacks.MOD_ID, "item/jetpack"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        var stopwatch = Stopwatch.createStarted();
        var registry = event.getModelRegistry();

        var cell = registry.get(new ResourceLocation(IronJetpacks.MOD_ID, "item/cell"));
        var capacitor = registry.get(new ResourceLocation(IronJetpacks.MOD_ID, "item/capacitor"));
        var thruster = registry.get(new ResourceLocation(IronJetpacks.MOD_ID, "item/thruster"));
        var jetpack = registry.get(new ResourceLocation(IronJetpacks.MOD_ID, "item/jetpack"));

        JetpackRegistry.getInstance().getAllJetpacks().forEach(pack -> {
            var cellLocation = pack.cell.getRegistryName();
            if (cellLocation != null) {
                var location = new ModelResourceLocation(cellLocation, "inventory");
                registry.replace(location, cell);
            }

            var capacitorLocation = pack.capacitor.getRegistryName();
            if (capacitorLocation != null) {
                var location = new ModelResourceLocation(capacitorLocation, "inventory");
                registry.replace(location, capacitor);
            }

            var thrusterLocation = pack.thruster.getRegistryName();
            if (thrusterLocation != null) {
                var location = new ModelResourceLocation(thrusterLocation, "inventory");
                registry.replace(location, thruster);
            }

            var jetpackLocation = pack.item.getRegistryName();
            if (jetpackLocation != null) {
                var location = new ModelResourceLocation(jetpackLocation, "inventory");
                registry.replace(location, jetpack);
            }
        });

        stopwatch.stop();

        LOGGER.info("Model replacement took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
