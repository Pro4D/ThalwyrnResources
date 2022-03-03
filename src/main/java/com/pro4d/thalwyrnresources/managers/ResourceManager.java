package com.pro4d.thalwyrnresources.managers;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.holograms.ProHologram;
import com.pro4d.thalwyrnresources.holograms.ProHologramLine;
import com.pro4d.thalwyrnresources.resources.ThalwyrnResource;
import com.pro4d.thalwyrnresources.utils.ThalwyrnResourcesUtils;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"CommentedOutCode", "SwitchStatementWithTooFewBranches", "ConstantConditions"})
public class ResourceManager {

    private final List<ThalwyrnResource> allResources;
    private final List<Integer> takenIDs;
    public static List<String> enumJobNames;
    private final ConstructionManager constructionManager;
    private final Configuration config;
    static ThalwyrnResources plugin;
    private final DecimalFormat df;
    private final String configPath = "Resources.";

    public ResourceManager(ThalwyrnResources plugin) {
        ResourceManager.plugin = plugin;

        constructionManager = ThalwyrnResources.getConstructionManager();
        allResources = new ArrayList<>();
        enumJobNames = new ArrayList<>();
        takenIDs = new ArrayList<>();
        config = ThalwyrnResources.getResourceConfig();

        df = new DecimalFormat("0.00");
        for (JobTypes jobTypes : JobTypes.values()) {
            enumJobNames.add(jobTypes.getJobName());
        }
    }

    //create/spawn resources
    public ThalwyrnResource createResource(int level, JobTypes job, Location location, String type, String extra, int tempID) {
        if(tempID == -4469) {
            boolean idSet = false;
//            int id = -4469;
            while (!idSet) {
                int t = ThalwyrnResourcesUtils.randomInteger(1, 4469);
                if (!getTakenIDs().contains(t)) {
                    tempID = t;
                    idSet = true;
                }
            }
        }

        ThalwyrnResource resource = new ThalwyrnResource(level, job, location, type, extra, tempID);
        allResources.add(resource);
        spawnResource(resource, location);

        return resource;
    }

    //keep track of each MMOCore job


    public boolean isAResource(int id) {
        for(ThalwyrnResource resource : allResources) {
            if(resource.getId() == id) {
                return true;
            }
        }
        return false;
    }

    //Handle spawning for each job type
    public void spawnResource(ThalwyrnResource resource, Location loc) {
        switch (resource.getJob()) {

//            case MINING:
//                Location loc = new Location(resource.getLocation().getWorld(), resource.getLocation().getX(), resource.getLocation().getY() - 1.44, resource.getLocation().getZ());
//                Location loc1 = new Location(loc.getWorld(),loc.getX() - 0.2,loc.getY() - 0.19,loc.getZ() - 0.4);
//                Location loc2 = new Location(loc.getWorld(),loc.getX() + 0.34,loc.getY() - 0.35,loc.getZ() - 0.3);
//
//                Entity entity = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
//
//                ArmorStand armorStand = (ArmorStand) entity;
//                armorStand.getEquipment().setHelmet(ConstructionManager.getHeadMap().get(MiningOre.COAL_BLOCK));
//                armorStand.setInvisible(true);
//                armorStand.setMarker(true);
//
//                Entity entity2 = loc1.getWorld().spawnEntity(loc1, EntityType.ARMOR_STAND);
//
//                ArmorStand armorStand2 = (ArmorStand) entity2;
//                armorStand2.getEquipment().setHelmet(ConstructionManager.getHeadMap().get(MiningOre.STONE));
//                armorStand2.setInvisible(true);
//                armorStand2.setMarker(true);
//
//                Entity entity3 = loc2.getWorld().spawnEntity(loc2, EntityType.ARMOR_STAND);
//
//                ArmorStand armorStand3 = (ArmorStand) entity3;
//                armorStand3.getEquipment().setHelmet(ConstructionManager.getHeadMap().get(MiningOre.STONE));
//                armorStand3.setInvisible(true);
//                armorStand3.setMarker(true);
//                break;

            case WOODCUTTING:
                constructionManager.pasteSchematic(resource.getType(), loc);
                Clipboard clipboard = constructionManager.getClipboard(resource.getType());
                Region region = clipboard.getRegion();
                BlockVector3 origin = clipboard.getOrigin();

                for(BlockVector3 v : region) {
                    Vector3 difference = origin.subtract(v).multiply(-1).toVector3();
                    Vector vector = new Vector(difference.getX(), difference.getY(), difference.getZ());

                    Location location = new Location(loc.getWorld(), loc.getBlockX() + vector.getBlockX(), loc.getBlockY() + vector.getBlockY(), loc.getBlockZ() + vector.getBlockZ());

                    if(loc.getWorld().getBlockAt(location).getType() != Material.AIR) {
                        resource.getInteractBlocks().add(loc.getWorld().getBlockAt(location));
                    }
                }


                ProHologram proHologram = new ProHologram(resource, loc);
                proHologram.setName("Tree");


                loc.getWorld().getPlayers().forEach(player -> {
                    if(!resource.getPlayerRespawnTime().containsKey(player.getUniqueId())) {
                        proHologram.spawnHologram(player);
                    }
                });

                break;
        }
    }

    public void despawnResource(ThalwyrnResource resource, Player player) {
        for(Block block : resource.getInteractBlocks()) {
            //block.setType(Material.AIR);
            player.sendBlockChange(block.getLocation(), plugin.createBlockData(Material.AIR));

        }
    }

    public void respawnResource(ThalwyrnResource resource, Player player) {
        for(Block block : resource.getInteractBlocks()) {
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        }

        resource.getHologram().spawnHologram(player);
        for(ProHologramLine line : resource.getHologram().getLines()) {
            line.spawnLine(player);
        }

    }

    public ThalwyrnResource getResource(int id) {
        for(ThalwyrnResource resource : allResources) {
            if(resource.getId() == id) {
                return resource;
            }
        }
        return null;
    }

    public List<ThalwyrnResource> getAllResources() {
        return allResources;
    }

    public List<Integer> getTakenIDs() {
        return takenIDs;
    }

    public void writeToConfig(ThalwyrnResource resource) {
        if(!config.isConfigurationSection("Resources")) config.createSection("Resources");
        String path = configPath + resource.getId();
        String locationPath = path + ".locations";

        config.set(locationPath + ".world", resource.getLocation().getWorld().getName());

        config.set(locationPath + ".x", Double.parseDouble(df.format(resource.getLocation().getX())));
        config.set(locationPath + ".y", Double.parseDouble(df.format(resource.getLocation().getY())));
        config.set(locationPath + ".z", Double.parseDouble(df.format(resource.getLocation().getZ())));

        config.set(path + ".levels", resource.getLevel());
        config.set(path + ".health", resource.getHealth());
        config.set(path + ".xp", resource.getXp());

        config.set(path + ".jobs", resource.getJob().getJobName());
        switch (resource.getJob()) {

            case WOODCUTTING:
                config.set(path + ".type", "Schematic");
                config.set(path + ".type.Schem-1", resource.getType());
                config.set(path + ".type.Schem-2", resource.getExtra());
                break;

            default:
                config.set(path + ".type", "Preset");
                config.set(path + ".type.preset", resource.getType());
                break;
        }

        if(resource.getLeftClick() != null || resource.getRightClick() != null) {
            String itemPath = path + ".items";
            if(resource.getLeftClick() != null) {
                config.set(itemPath + ".left-click", resource.getLeftClick());
            }
            if(resource.getRightClick() != null) {
                config.set(itemPath + ".right-click", resource.getRightClick());
            }
        }

//        //FIX BELOW
//        if(resource.getHologram() != null) {
//            config.set(resourceSection + ".hologram-location.x", resource.getHologram().getLocation().getBlockX());
//            config.set(resourceSection + ".hologram-location.y", resource.getHologram().getLocation().getBlockY());
//            config.set(resourceSection + ".hologram-location.z", resource.getHologram().getLocation().getBlockZ());
//        }
        plugin.saveCustomConfig();
    }

    public void delete(ThalwyrnResource resource) {
        if(!config.isConfigurationSection("Resources.")) return;
        if(config.contains(configPath + resource.getId())) {
            config.set(configPath + resource.getId(), null);
            plugin.saveCustomConfig();
        }

    }

    @SuppressWarnings("ConstantConditions")
    public void validateConfig() {
        if(!config.isConfigurationSection(configPath)) return;
        allResources.clear();
        for(String id : config.getConfigurationSection(configPath).getKeys(false)) {
            if(!ThalwyrnResourcesUtils.isInt(id)) return;

            String path = configPath + id;

            String locPath = path + ".locations.";
            World world = null;
            double x = 0;
            double y = 0;
            double z = 0;
            if(config.contains(locPath)) {
                for(String locs : config.getConfigurationSection(locPath).getKeys(false)) {
                    if (config.isString(locPath + locs)) {
                        if(Bukkit.getWorld(config.getString(locPath + locs)) != null) {
                            world = Bukkit.getWorld(config.getString(locPath + locs));
                        }
                    } else {
                        switch (locs) {
                            case "x":
                                if (config.isDouble(locPath + locs)) {
                                    x = config.getDouble(locPath + locs);
                                }
                                break;
                            case "y":
                                if (config.isDouble(locPath + locs)) {
                                    y = config.getDouble(locPath + locs);
                                }
                                break;
                            case "z":
                                if (config.isDouble(locPath + locs)) {
                                    z = config.getDouble(locPath + locs);
                                }
                                break;
                        }
                    }

                }
            } else continue;
            Location location = new Location(world, x, y, z);

            int level = 0;
            if(config.contains(path + ".levels")) {
                level = config.getInt(path + ".levels");
            }

            JobTypes job = null;
            if(config.contains(path + ".jobs")) {
                job = JobTypes.valueOf(config.getString(path + ".jobs"));
            }

            String type = null;
            String extra = null;
            switch (job) {
                case WOODCUTTING:
                    if(config.contains(path + ".type.Schem-1")) {
                        type = config.getString(path + ".type.Schem-1");
                    }
                    if(config.contains(path + ".type.Schem-2")) {
                        extra = config.getString(path + ".type.Schem-2");
                    }
                    break;
            }

            ThalwyrnResource resource = new ThalwyrnResource(level, job, location, type, extra, Integer.parseInt(id));
            spawnResource(resource, resource.getLocation());

            if(config.contains(path + ".xp")) {
                if(config.isInt(path + ".xp")) {
                    if(config.getInt(path + ".xp") != 0) {
                        int xp = config.getInt(path + ".xp");
                        resource.setXp(xp);
                    }
                }
            }

            if(config.isConfigurationSection(path + ".items")) {
                String itemPath = path + ".items";
                if(config.contains(itemPath + ".left-click")) {
                    ItemStack item = config.getItemStack(itemPath + ".left-click");
                    resource.setLeftClick(item);
                }

                if(config.contains(itemPath + ".right-click")) {
                    ItemStack item = config.getItemStack(itemPath + ".right-click");
                    resource.setRightClick(item);
                }

            }



        }

    }

}
