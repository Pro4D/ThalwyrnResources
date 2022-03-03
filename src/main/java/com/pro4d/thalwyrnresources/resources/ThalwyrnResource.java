package com.pro4d.thalwyrnresources.resources;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.holograms.ProHologram;
import com.pro4d.thalwyrnresources.holograms.ProHologramLine;
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
        updateResource();
    }

    public void setRightClick(ItemStack rightClick) {
        this.rightClick = rightClick;
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

            if(leftClick != null) {
                if(leftClick.getItemMeta() != null) {
                    if(getHologram().getLeftClickHologram() == null) getHologram().setLeftClickHologram(new ProHologramLine(getHologram(), getHologram().getLocation()));
                    Location loc = new Location(getHologram().getLocation().getWorld(), getHologram().getLocation().getX(), getHologram().getLocation().getY() - .7, getHologram().getLocation().getZ());
                    getHologram().getLeftClickHologram().setLocation(loc);

                    if (leftClick.getItemMeta().hasDisplayName()) {
                        getHologram().getLeftClickHologram().setName("Left-Click for " + leftClick.getItemMeta().getDisplayName());
                    } else {
                        getHologram().getLeftClickHologram().setName("Left-Click for " + ThalwyrnResourcesUtils.formatMessage(leftClick.getType().name()));
                    }
                }
            }

            if(rightClick != null) {
                if(rightClick.getItemMeta() != null) {
                    if(getHologram().getRightClickHologram() == null) getHologram().setRightClickHologram(new ProHologramLine(getHologram(), getHologram().getLocation()));
                    Location loc = new Location(getHologram().getLocation().getWorld(), getHologram().getLocation().getX(), getHologram().getLocation().getY() - .952, getHologram().getLocation().getZ());
                    getHologram().getRightClickHologram().setLocation(loc);

                    if (rightClick.getItemMeta().hasDisplayName()) {
                        getHologram().getRightClickHologram().setName("Right-Click for " + rightClick.getItemMeta().getDisplayName());

                    } else {
                        getHologram().getRightClickHologram().setName("Right-Click for " + ThalwyrnResourcesUtils.formatMessage(rightClick.getType().name()));

                    }
                }
            }

            getHologram().updateHologram(this);
        }
    }

}
