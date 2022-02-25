package com.pro4d.thalwyrnresources.resources;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.holograms.ProHologram;
import com.pro4d.thalwyrnresources.utils.ThalwyrnResourcesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class ThalwyrnResource {

    private final int id;

    private int level;
    private final int health;
    private int xp;
    private String type;
    @Nullable private final String extra;
    private ItemStack leftClick;
    private ItemStack rightClick;
    private final Location location;
    private JobTypes job;
    private ProHologram hologram;


    private final Map<UUID, Integer> playerRespawnTime;
    private final List<Block> interactBlocks;

    List<Location> temp;

    public ThalwyrnResource(int level, JobTypes job, Location location, String type, @Nullable String extra, int id) {
        this.level = level;

        if(ThalwyrnResources.getLevelConfig().contains(String.valueOf(level))) {
            this.health = ThalwyrnResources.getLevelConfig().getInt(String.valueOf(level));
        } else {

            this.health = level * 100;
            //sender.sendMessage("levels.yml does not contain a value for level: " + level + ", automatically set health to 100 * level.");
        }

        this.xp = 0;
        this.job = job;
        this.location = location.clone();
        this.extra = extra;

        this.id = id;
        this.type = type;

        playerRespawnTime = new HashMap<>();
        interactBlocks = new ArrayList<>();
        temp = new ArrayList<>();

        ThalwyrnResources.getResourceManager().writeToConfig(this);
        ThalwyrnResources.getResourceManager().getAllResources().add(this);

    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        return health;
    }

    public int getXp() {
        return xp;
    }

    public JobTypes getJob() {
        return job;
    }

    public ItemStack getLeftClick() {
        return leftClick;
    }

    public ItemStack getRightClick() {
        return rightClick;
    }

    public Location getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    @Nullable
    public String getExtra() {
        return extra;
    }

    public Map<UUID, Integer> getPlayerRespawnTime() {
        return playerRespawnTime;
    }

    public ProHologram getHologram() {
        return hologram;
    }

    public List<Block> getInteractBlocks() {
        return interactBlocks;
    }

    public List<Location> getTemp() {
        return temp;
    }

    public void delete() {
        ThalwyrnResources.getResourceManager().delete(this);
        getInteractBlocks().forEach(block -> block.setType(Material.AIR));
        Bukkit.getOnlinePlayers().forEach(player -> getHologram().despawn(player));
    }

//    public void tempMethod() {
//        Clipboard clipboard = ThalwyrnResources.getConstructionManager().getClipboard(getExtra());
//        Region region = clipboard.getRegion();
//
//        BlockVector3 origin = clipboard.getOrigin();
//        Location resourceLoc = getLocation();
//        Vector3 resourceVector = Vector3.at(resourceLoc.getBlockX(), resourceLoc.getBlockY(), resourceLoc.getBlockZ());
//
//        List<Location> schematicOne = new ArrayList<>();
//
//        for (Block block : getBlocks()) {
//            schematicOne.add(block.getLocation());
//        }
//
//        for(BlockVector3 v : region) {
//            Vector3 distance = origin.subtract(v).multiply(-1).toVector3();
//            Location loc = new Location(resourceLoc.getWorld(), resourceVector.getX() + distance.getX(), resourceVector.getY() + distance.getY(),resourceVector.getZ() + distance.getZ());
//
//            if(!schematicOne.contains(loc)) {
//                ghostBlocks.add(loc);
//            }
//
//        }
//    }


    public void setType(String type) {
        this.type = type;
        updateResource();
    }

    public void setLeftClick(ItemStack leftClick) {
        this.leftClick = leftClick;
        if(leftClick.getItemMeta() != null) {
            if (this.leftClick.getItemMeta().hasDisplayName()) {
                if (getHologram() != null) {
                    getHologram().setLeftClickHologram("Left-Click for " + this.leftClick.getItemMeta().getDisplayName());
                }
            } else {
                if (getHologram() != null) {
                    getHologram().setLeftClickHologram("Left-Click for " + ThalwyrnResourcesUtils.formatMessage(this.leftClick.getType().name()));
                }
            }
        }
        updateResource();
    }

    public void setRightClick(ItemStack rightClick) {
        this.rightClick = rightClick;
        if(rightClick.getItemMeta() != null) {
            if (this.rightClick.getItemMeta().hasDisplayName()) {
                if (getHologram() != null) {
                    getHologram().setRightClickHologram("Right-Click for " + this.rightClick.getItemMeta().getDisplayName());
                }
            } else {
                if (getHologram() != null) {
                    getHologram().setRightClickHologram("Right-Click for " + ThalwyrnResourcesUtils.formatMessage(this.rightClick.getType().name()));
                }
            }
        }
        updateResource();
    }


    public void setLevel(int level) {
        this.level = level;
        updateResource();
    }

    public void setXp(int xp) {
        this.xp = xp;
        updateResource();
    }

    public void setJob(JobTypes job) {
        this.job = job;
        updateResource();
    }

    public int getId() {
        return id;
    }

    public void setHologram(ProHologram hologram) {
        this.hologram = hologram;
    }

    public void updateResource() {
        ThalwyrnResources.getResourceManager().writeToConfig(this);
        if(getHologram() != null) {
            Bukkit.getOnlinePlayers().forEach(player -> getHologram().updateHologram(this, player));
        }
    }

}
