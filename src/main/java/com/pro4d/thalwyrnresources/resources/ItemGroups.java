package com.pro4d.thalwyrnresources.resources;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.utils.TWMessages;
import com.pro4d.thalwyrnresources.utils.TWUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGroups {

    //private final Map<JobTypes, Map<Integer, List<ItemStack>>> itemGroups;
    private final Map<Integer, Map<JobTypes, List<ItemStack>>> itemGroups;

    private final FileConfiguration config;
    private final ThalwyrnResources plugin;
    private final String configPath;

    public ItemGroups(ThalwyrnResources plugin) {
        config = plugin.getItemGroupsConfig();
        this.plugin = plugin;
        itemGroups = new HashMap<>();
        configPath = "ItemGroups.";
    }

    public void addItemToGroup(JobTypes job, int level, ItemStack item, Player player) {
        String path = "ItemGroups." + job.getJobName();
        if(!config.isConfigurationSection(path)) config.createSection(path);

        validateItemGroupConfig();

        if(itemGroups.containsKey(level)) {
            if(itemGroups.get(level).containsKey(job)) {
                if(itemGroups.get(level).get(job) != null) {
                    if (itemGroups.get(level).get(job).contains(item)) {
                        player.sendMessage(TWMessages.cantAddItemToGroup());
                        return;
                    }
                }
            }
        }

        validateItemGroupConfig();

        int itemCount = 1;

        String levelPath = path + ".Level-" + level;
        if(config.isConfigurationSection(levelPath)) {
            for (String k : config.getConfigurationSection(levelPath).getKeys(false)) {
                if (config.isItemStack(levelPath + "." + k)) {
                    ItemStack itemStack = config.getItemStack(levelPath + "." + k);
                    if (itemStack == item) {
                        itemCount = (itemCount - 1);
                    } else {
                        itemCount = (itemCount + 1);
                    }
                }
            }
        }

        config.set(levelPath + ".item-" + itemCount, item);
        player.sendMessage(TWMessages.addedItemToGroup().replace("%item_name%", TWUtils.itemStackName(item)).replace("%job%", job.getJobName()).replace("%lvl%", String.valueOf(level)));
        plugin.saveItemGroupConfig();

        validateItemGroupConfig();
    }

    public void validateItemGroupConfig() {
        if(!config.isConfigurationSection(configPath)) return;
        itemGroups.clear();
        for(String j : config.getConfigurationSection(configPath).getKeys(false)) {
            if(!ThalwyrnResources.getResourceManager().getEnumJobNames().contains(j.toUpperCase())) continue;

            JobTypes job = JobTypes.getMatching(j);
            assert job != null;
            String path = configPath + job.getJobName();
            if(!config.isConfigurationSection(path)) continue;
            for(String l : config.getConfigurationSection(path).getKeys(false)) {
                String levelPath = path + "." + l;

                int level = Integer.parseInt(l.split("-")[1]);
                List<ItemStack> itemList = new ArrayList<>();

                for(String i : config.getConfigurationSection(levelPath).getKeys(false)) {
                    if(config.isItemStack(levelPath + "." + i)) {
                        ItemStack item = config.getItemStack(levelPath + "." + i);
                        if(itemList.contains(item)) {
                            config.set(levelPath + "." + i, null);
                        } else itemList.add(item);
                    }
                }

                Map<JobTypes, List<ItemStack>> levelItemMap = new HashMap<>();
                levelItemMap.put(job, itemList);

                itemGroups.put(level, levelItemMap);

//8:44
            }
        }

    }

    public Map<Integer, Map<JobTypes, List<ItemStack>>> getItemGroups() {
        return itemGroups;
    }

    //maybe use this method?
//    public boolean isItemInJobGroup(JobTypes job, ItemStack item) {
//        if(!itemGroups.containsKey(job)) return false;
//        for(JobTypes jobTypes : itemGroups.keySet()) {
//            for(Integer integer : itemGroups.get(jobTypes).keySet()) {
//                for(ItemStack items : itemGroups.get(jobTypes).get(integer)) {
//                    if(items.equals(item)) return true;
//                }
//            }
//        }
//        return false;
//    }


}
