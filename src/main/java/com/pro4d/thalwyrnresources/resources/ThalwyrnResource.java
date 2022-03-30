package com.pro4d.thalwyrnresources.resources;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.holograms.ProHologram;
import com.pro4d.thalwyrnresources.holograms.ProHologramLine;
import com.pro4d.thalwyrnresources.utils.TWUtils;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
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
    private int health;
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
    List<Trigger> triggers;

    public ThalwyrnResource(int level, JobTypes job, Location location, String type, @Nullable String extra, int id) {
        this.level = level;

        if(ThalwyrnResources.getOptionsConfig().contains(String.valueOf(level))) {
            this.health = ThalwyrnResources.getOptionsConfig().getInt(String.valueOf(level));
        } else {
            double h = level * .6;
            if(level < 50) {
                this.health = (int) Math.round(level - h);
            } else {
                this.health = (int) Math.round((level - h) * .85);
            }

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
        triggers = new ArrayList<>();
        ThalwyrnResources.getResourceManager().writeToConfig(this);

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

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void delete() {
        //ThalwyrnResources.getResourceManager().delete(this);
        getInteractBlocks().forEach(block -> block.setType(Material.AIR));
        Bukkit.getOnlinePlayers().forEach(player -> getHologram().despawn(player));
    }

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

    public void setHealth(int health) {
        this.health = health;
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

                    String itemName = TWUtils.itemStackName(leftClick);
//                    if (leftClick.getItemMeta().hasDisplayName()) {
//                        getHologram().getLeftClickHologram().setName("Left-Click for " + leftClick.getItemMeta().getDisplayName());
//                    } else {
//                        getHologram().getLeftClickHologram().setName("Left-Click for " + WordUtils.capitalizeFully(TWUtils.formatMessage(leftClick.getType().name())));
//                    }
                    getHologram().getLeftClickHologram().setName("Left-Click for " + itemName);
                }
            }

            if(rightClick != null) {
                if(rightClick.getItemMeta() != null) {
                    if(getHologram().getRightClickHologram() == null) getHologram().setRightClickHologram(new ProHologramLine(getHologram(), getHologram().getLocation()));
                    Location loc = new Location(getHologram().getLocation().getWorld(), getHologram().getLocation().getX(), getHologram().getLocation().getY() - .952, getHologram().getLocation().getZ());
                    getHologram().getRightClickHologram().setLocation(loc);

                    String itemName = TWUtils.itemStackName(rightClick);
//                    if (rightClick.getItemMeta().hasDisplayName()) {
//                        getHologram().getRightClickHologram().setName("Right-Click for " + rightClick.getItemMeta().getDisplayName());
//                    } else {
//                        getHologram().getRightClickHologram().setName("Right-Click for " + WordUtils.capitalizeFully(TWUtils.formatMessage(rightClick.getType().name())));
//                    }
                    getHologram().getRightClickHologram().setName("Right-Click for " + itemName);
                }
            }

            getHologram().updateHologram(this);
        }
    }

}
