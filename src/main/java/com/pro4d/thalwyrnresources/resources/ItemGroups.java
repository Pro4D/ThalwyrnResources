package com.pro4d.thalwyrnresources.resources;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGroups {

    private final Map<JobTypes, Map<Integer, List<ItemStack>>> itemGroups;
    private final FileConfiguration config;
    private final ThalwyrnResources plugin;
    public ItemGroups(ThalwyrnResources plugin) {
        config = plugin.getItemGroupsConfig();
        this.plugin = plugin;
        itemGroups = new HashMap<>();
    }

    public void addItemToGroup(JobTypes job, int level, ItemStack item) {
        String path = "ItemGroups." + job.getJobName() + ".Level-" + level;
        if(!config.isConfigurationSection(path)) config.createSection(path);
        List<ItemStack> itemList = new ArrayList<>();
        itemList.add(item);

        int itemCount = 1;

        //ItemGroups.WOODCUTTING.Level-4.item-1:

        //LOOP THROUGH JOB AND GET ALL KEYS
        //?? CALL VALIDATE METHOD ??

        for(String k : config.getConfigurationSection(path).getKeys(false)) {
            if(config.isItemStack(path + ".item-" + itemCount)) {
                ItemStack itemStack = config.getItemStack(path + ".item-" + itemCount);
                if(itemList.contains(itemStack)) {
                    itemList.remove(itemStack);
                    config.set(path + "." + k, null);
                    itemCount--; //???

                }
                if(!(itemStack == item)) {
                    itemList.add(itemStack);
                    itemCount++;

                }
            }
        }

        //hand is null check
        Map<Integer, List<ItemStack>> levelItemMap = new HashMap<>();

        levelItemMap.put(level, itemList);
        if(itemGroups.containsKey(job)) {
            for(JobTypes jobTypes : itemGroups.keySet()) {
                for(Integer integer: itemGroups.get(jobTypes).keySet()) {
                    if(integer == level && jobTypes == job) {
                        itemGroups.replace(job, levelItemMap);
                    }
                }
            }
        } else {
            itemGroups.put(job, levelItemMap);
        }

        config.set(path + ".item-" + itemCount, item);
        plugin.saveItemGroupConfig();
    }

    public void validateItemGroupConfig() {}

    public Map<JobTypes, Map<Integer, List<ItemStack>>> getItemGroups() {
        return itemGroups;
    }

    public boolean isItemInJobGroup(JobTypes job, ItemStack item) {
        if(!itemGroups.containsKey(job)) return false;
        for(JobTypes jobTypes : itemGroups.keySet()) {
            for(Integer integer : itemGroups.get(jobTypes).keySet()) {
                for(ItemStack items : itemGroups.get(jobTypes).get(integer)) {
                    if(items.equals(item)) return true;
                }
            }
        }
        return false;
    }


}
