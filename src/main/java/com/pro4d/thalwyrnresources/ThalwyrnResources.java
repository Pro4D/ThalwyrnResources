package com.pro4d.thalwyrnresources;

import com.pro4d.thalwyrnresources.commands.ResourceCommand;
import com.pro4d.thalwyrnresources.commands.TabCompletor;
import com.pro4d.thalwyrnresources.listeners.ResourceListener;
import com.pro4d.thalwyrnresources.managers.ConstructionManager;
import com.pro4d.thalwyrnresources.managers.ResourceManager;
import com.pro4d.thalwyrnresources.utils.ThalwyrnResourcesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class ThalwyrnResources extends JavaPlugin {

    private static ResourceManager resourceManager;
    private static ConstructionManager constructionManager;
    private static FileConfiguration resourceConfig;
    private static ResourceListener resourceListener;
    private File resourceFile;

    private File levelFile;
    private static FileConfiguration levelConfig;

    private boolean worldGuardEnabled = false;
    private Server server;

    @Override
    public void onEnable() {
        // Plugin startup logic

        if(Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            ThalwyrnResourcesUtils.log(Level.SEVERE, ThalwyrnResourcesUtils.formattedColors("&cThis plugin REQUIRES WorldEdit, please install it."));
            ThalwyrnResourcesUtils.log(Level.SEVERE, ThalwyrnResourcesUtils.formattedColors("&cDisabling plugin..."));
            getServer().getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().getPlugin("MMOCore") == null) {
            ThalwyrnResourcesUtils.log(Level.SEVERE, ThalwyrnResourcesUtils.formattedColors("&cThis plugin REQUIRES MMOCore, please install it."));
            ThalwyrnResourcesUtils.log(Level.SEVERE, ThalwyrnResourcesUtils.formattedColors("&cDisabling plugin..."));
            getServer().getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            ThalwyrnResourcesUtils.log(Level.WARNING, ThalwyrnResourcesUtils.formattedColors("&cWorldGuard is not installed, you will not be able to use region features."));
        } else {
            worldGuardEnabled = true;
//            ThalwyrnResourcesUtils.log(Level.WARNING, ThalwyrnResourcesUtils.formattedColors("&cV" + WorldGuard.getInstance().getPlatform().getPlatformVersion()));
        }

        createCustomConfig();
        saveCustomConfig();
        reloadCustomConfig();

        createLevelConfig();
        saveLevelConfig();
        reloadLevelConfig();

        constructionManager = new ConstructionManager(this);
        resourceManager = new ResourceManager(this);
        resourceListener = new ResourceListener(this);
        server = getServer();


        new ResourceCommand(this);
        new TabCompletor(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                resourceManager.validateConfig();
            }
        }.runTaskLater(this, 1);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        reloadCustomConfig();
//        reloadLevelConfig();
//        resourceManager.getAllResources().forEach(resource -> Bukkit.getOnlinePlayers().forEach(player -> resource.getHologram().despawn(player)));
        resourceManager.cleanup();


//        for (Location loc : resourceListener.getBlocksFakeDestroyed().keySet()) {
//            //        for (Location loc : resourceListener.getBlocksFakeDestroyed().keySet()) {
//            World world = loc.getWorld();
//            Bukkit.getOnlinePlayers().forEach(player -> {
//                Location pLoc = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
//
//                if (pLoc.equals(loc)) {
//                    //check if player's location matches then move them to a safe spot
//                    Bukkit.broadcastMessage("PLAYER LOGGED OUT IN UNSAFE SPOT");
//                    Bukkit.broadcastMessage("DEBUG: " + resourceListener.getBlocksFakeDestroyed().get(loc).getMaterial().name());
//                    boolean locPicked = false;
//
//                    Location min = loc.subtract(1, 0, 1);
//                    Location max = loc.add(1, 0, 1);
//
//                    while (!locPicked) {
//                        int minX = min.getBlockX();
//                        int minY = min.getBlockY();
//                        int minZ = min.getBlockZ();
//
//                        int maxX = max.getBlockX();
//                        int maxY = max.getBlockY();
//                        int maxZ = max.getBlockZ();
//
//                        for (int x = minX; x != maxX + 1; x++) {
//                            for (int y = minY; y != maxY + 1; y++) {
//                                for (int z = minZ; z != maxZ + 1; z++) {
//
//                                    if (isBlockSafe(world.getBlockAt(x, y, z))) {
//                                        player.teleport(new Location(world, x, y, z));
//                                        locPicked = true;
//                                        player.sendMessage("TELEPORTED TO SAFE LOCATION");
//                                        break;
//
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                    world.setBlockData(loc, resourceListener.getBlocksFakeDestroyed().get(loc));
//                    resourceListener.getBlocksFakeDestroyed().remove(loc);
//                    Bukkit.broadcastMessage("REPLACING BLOCK");
//
//
//                }
//            });
//        }
    }

    public static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public static ConstructionManager getConstructionManager() {return constructionManager;}

    public static ResourceListener getResourceListener() {return resourceListener;}

    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }

    public void saveCustomConfig()
    {
        try {
            resourceConfig.save(resourceFile);
        } catch (IOException e)
        {
            Bukkit.getLogger().log(Level.CONFIG, "Could not save 'resources.yml' ");
        }
    }

    public void reloadCustomConfig() {
        try {
            if(resourceFile.exists()) {
                resourceConfig.load(resourceFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createCustomConfig()
    {
        String customConfigName = "resources.yml";
        resourceFile = new File(getDataFolder(), customConfigName);
        if(!resourceFile.exists())
        {
            resourceFile.getParentFile().mkdirs();
            saveResource(customConfigName, false);
            Bukkit.getLogger().log(Level.CONFIG, "Could not find 'resources.yml', creating one now...");
        }
        resourceConfig = YamlConfiguration.loadConfiguration(resourceFile);
    }

    public static FileConfiguration getResourceConfig() {
        return resourceConfig;
    }

    public void saveLevelConfig()
    {
        try {
            levelConfig.save(levelFile);
        } catch (IOException e)
        {
            Bukkit.getLogger().log(Level.CONFIG, "Could not save 'levels.yml' ");
        }
    }

    public void reloadLevelConfig() {
        try {
            if(levelFile.exists()) {
                levelConfig.load(levelFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createLevelConfig()
    {
        String customConfigName = "options.yml";
        levelFile = new File(getDataFolder(), customConfigName);
        if(!levelFile.exists())
        {
            levelFile.getParentFile().mkdirs();
            saveResource(customConfigName, false);
            Bukkit.getLogger().log(Level.CONFIG, "Could not find 'options.yml', creating one now...");
        }
        levelConfig = YamlConfiguration.loadConfiguration(levelFile);
    }

    public static FileConfiguration getLevelConfig() {
        return levelConfig;
    }


    public BlockData createBlockData(Material mat) {
        return server.createBlockData(mat);
    }


//    public boolean isBlockSafe(Block block) {
//        List<Material> blocked = new ArrayList<>();
//        blocked.add(Material.WATER);
//        blocked.add(Material.LAVA);
//        blocked.add(Material.MAGMA_BLOCK);
//
//        return !blocked.contains(block.getType()) && !blocked.contains(block.getWorld().getBlockAt(new Location(block.getWorld(), block.getX(), block.getY() + 1, block.getZ())).getType());
//    }


    //MAKE SURE BLOCK DATA TRANSFERS
    //LOOP THROUGH CONFIG
    //TYPES
    // - SCHEMATIC
    //   - SCHEM-1
    //   - SCHEM-2
    // - PRESET
    //   - COAL_ORE/WHEAT_CROP
}
