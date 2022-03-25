package com.pro4d.thalwyrnresources;

import com.pro4d.thalwyrnresources.commands.ResourceCommand;
import com.pro4d.thalwyrnresources.commands.TabCompletor;
import com.pro4d.thalwyrnresources.listeners.ResourceListener;
import com.pro4d.thalwyrnresources.managers.ConstructionManager;
import com.pro4d.thalwyrnresources.managers.ResourceManager;
import com.pro4d.thalwyrnresources.resources.ItemGroups;
import com.pro4d.thalwyrnresources.utils.TWUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    private ConstructionManager constructionManager;
    private ResourceListener resourceListener;
    private ItemGroups itemGroups;

    private FileConfiguration resourceConfig;
    private File resourceFile;

    private static FileConfiguration optionsConfig;
    private File optionsFile;

    private FileConfiguration itemGroupsConfig;
    private File itemGroupsFile;

    private boolean worldGuardEnabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic

        if(Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            TWUtils.log(Level.SEVERE, TWUtils.formattedColors("&cThis plugin REQUIRES WorldEdit, please install it."));
            TWUtils.log(Level.SEVERE, TWUtils.formattedColors("&cDisabling plugin..."));
            getServer().getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().getPlugin("MMOCore") == null) {
            TWUtils.log(Level.SEVERE, TWUtils.formattedColors("&cThis plugin REQUIRES MMOCore, please install it."));
            TWUtils.log(Level.SEVERE, TWUtils.formattedColors("&cDisabling plugin..."));
            getServer().getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            TWUtils.log(Level.WARNING, TWUtils.formattedColors("&cWorldGuard is not installed, you will not be able to use region features."));
        } else {
            worldGuardEnabled = true;
        }

        createResourceConfig();
        saveResourceConfig();
        reloadResourceConfig();

        createOptionsConfig();
        saveOptionsConfig();
        reloadOptionsConfig();

        createItemGroupConfig();
        saveItemGroupConfig();
        reloadItemGroupConfig();

        constructionManager = new ConstructionManager(this);
        resourceManager = new ResourceManager(this);
        resourceListener = new ResourceListener(this);
        itemGroups = new ItemGroups(this);

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
        resourceManager.cleanup();
    }

    public static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ConstructionManager getConstructionManager() {return constructionManager;}

    public ResourceListener getResourceListener() {return resourceListener;}

    public ItemGroups getItemGroups() {
        return itemGroups;
    }

    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }


    public void saveResourceConfig() {
        try {
            resourceConfig.save(resourceFile);
        } catch (IOException e)
        {
            Bukkit.getLogger().log(Level.CONFIG, "Could not save 'resources.yml' ");
        }
    }

    public void reloadResourceConfig() {
        try {
            if(resourceFile.exists()) {
                resourceConfig.load(resourceFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createResourceConfig() {
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

    public FileConfiguration getResourceConfig() {
        return resourceConfig;
    }


    public void saveOptionsConfig() {
        try {
            optionsConfig.save(optionsFile);
        } catch (IOException e)
        {
            Bukkit.getLogger().log(Level.CONFIG, "Could not save 'levels.yml' ");
        }
    }

    public void reloadOptionsConfig() {
        try {
            if(optionsFile.exists()) {
                optionsConfig.load(optionsFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createOptionsConfig() {
        String customConfigName = "options.yml";
        optionsFile = new File(getDataFolder(), customConfigName);
        if(!optionsFile.exists())
        {
            optionsFile.getParentFile().mkdirs();
            saveResource(customConfigName, false);
            Bukkit.getLogger().log(Level.CONFIG, "Could not find 'options.yml', creating one now...");
        }
        optionsConfig = YamlConfiguration.loadConfiguration(optionsFile);
    }

    public static FileConfiguration getOptionsConfig() {
        return optionsConfig;
    }

    public void saveItemGroupConfig() {
        try {
            itemGroupsConfig.save(itemGroupsFile);
        } catch (IOException e)
        {
            Bukkit.getLogger().log(Level.CONFIG, "Could not save 'itemgroups.yml' ");
        }
    }

    public void reloadItemGroupConfig() {
        try {
            if(itemGroupsFile.exists()) {
                itemGroupsConfig.load(itemGroupsFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createItemGroupConfig() {
        String customConfigName = "itemgroups.yml";
        itemGroupsFile = new File(getDataFolder(), customConfigName);
        if(!itemGroupsFile.exists())
        {
            itemGroupsFile.getParentFile().mkdirs();
            saveResource(customConfigName, false);
            Bukkit.getLogger().log(Level.CONFIG, "Could not find 'itemgroups.yml', creating one now...");
        }
        itemGroupsConfig = YamlConfiguration.loadConfiguration(itemGroupsFile);
    }

    public FileConfiguration getItemGroupsConfig() {
        return itemGroupsConfig;
    }


    public BlockData createBlockData(Material mat) {
        return getServer().createBlockData(mat);
    }



    //TYPES
    // - SCHEMATIC
    //   - SCHEM-1
    //   - SCHEM-2
    // - PRESET
    //   - COAL_ORE/WHEAT_CROP
}
