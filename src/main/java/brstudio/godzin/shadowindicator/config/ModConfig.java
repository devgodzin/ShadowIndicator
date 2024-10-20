package brstudio.godzin.shadowindicator.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.io.File;

public class ModConfig {
    public static Configuration config;
    private static final String CONFIG_FILE = "config/shadowindicator.cfg";

    public static void loadConfig() {
        config = new Configuration(new File(CONFIG_FILE));
        try {
            config.load();
            Property maxDistance = config.get(Configuration.CATEGORY_GENERAL, "MaxDistance", 5.0D);
            maxDistance.setComment("The maximum distance to check for entities in front of the player.");
            Property displayEntityName = config.get(Configuration.CATEGORY_GENERAL, "DisplayEntityName", true);
        } catch (Exception e) {
            System.out.printf("Error loading server config file: %s\n", CONFIG_FILE);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    // Changes
    public static double getMaxDistance() {
        return config.get(Configuration.CATEGORY_GENERAL, "MaxDistance", 5.0D).getDouble();
    }

    public static boolean displayEntityName() {
        return config.get(Configuration.CATEGORY_GENERAL, "DisplayEntityName", true).getBoolean();
    }
}
