package com.fleetingdream.notorchspam;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = NoTorchSpam.MODID, name = NoTorchSpam.NAME, version = NoTorchSpam.VERSION)
public class NoTorchSpam {

    public static final String MODID = "notorchspam";
    public static final String NAME = "No Torch Spam";
    public static final String VERSION = "0.1";

    private static Logger logger;
    public static Configuration config;

    // Config values
    public static int maxSpawnLightLevel = 0; // default vanilla limit

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);

        File configFile = new File(event.getModConfigurationDirectory(), MODID + ".cfg");
        config = new Configuration(configFile);
        syncConfig();
    }

    @SubscribeEvent
    public void onHostileSpawn(LivingSpawnEvent.CheckSpawn event) {
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());

        if (event.getSpawner() == null && event.getEntityLiving() instanceof EntityMob) {
            int blockLight = event.getWorld().getLightFor(EnumSkyBlock.BLOCK, pos);

            if (blockLight > NoTorchSpam.maxSpawnLightLevel) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    public static void syncConfig() {
        try {
            config.load();

            maxSpawnLightLevel = config.getInt(
                    "minBlockLight",
                    Configuration.CATEGORY_GENERAL,
                    maxSpawnLightLevel,             // default
                    -1,                             // min
                    7,                              // max
                    "Maximum block light level for hostile mobs to spawn. -1 disables hostile mob spawning."
            );
        } catch (Exception e) {
            logger.error("Failed to load config for " + MODID, e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
