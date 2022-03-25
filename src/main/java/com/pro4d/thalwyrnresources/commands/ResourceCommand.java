package com.pro4d.thalwyrnresources.commands;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.enums.TWCommands;
import com.pro4d.thalwyrnresources.managers.ResourceManager;
import com.pro4d.thalwyrnresources.resources.ThalwyrnResource;
import com.pro4d.thalwyrnresources.utils.TWMessages;
import com.pro4d.thalwyrnresources.utils.TWUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ResourceCommand implements CommandExecutor {

    private final ResourceManager resourceManager;
    private final ThalwyrnResources plugin;
    public ResourceCommand(ThalwyrnResources plugin) {
        this.resourceManager = ThalwyrnResources.getResourceManager();
        this.plugin = plugin;
        Bukkit.getPluginCommand("resource").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(TWMessages.invalidCommandUsage());
            return false;
        }
        if(args[0].equals("reload") && args.length == 1) {
            resourceManager.cleanup();
            resourceManager.validateConfig();
            sender.sendMessage(TWMessages.reloadMessage());
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(TWMessages.notAPlayer());
            return true;
        }
        Player player = (Player) sender;

        switch (args[0]) {

            case "place":
                //create
                if(TWUtils.isInt(args[1])) {
                    int level = Integer.parseInt(args[1]);

                    if(resourceManager.getEnumJobNames().contains(args[2].toUpperCase())) {

                        for(String name : resourceManager.getEnumJobNames()) {

                            if(name.equalsIgnoreCase(args[2])) {
                                JobTypes job = JobTypes.getMatching(name);
                                switch (job) {
                                    case WOODCUTTING:
                                        if(plugin.getConstructionManager().isSchematic(args[3])) {
                                            if(plugin.getConstructionManager().isSchematic(args[4])) {
                                                ThalwyrnResource resource = resourceManager.createResource(level, job, player.getLocation(), args[3], args[4], -4469);
                                                player.sendMessage(TWMessages.createResource().replace("%id%", String.valueOf(resource.getId())));
                                                return true;
                                            }
                                        }

                                }
                            }
                        }
                    }
                }
                return true;

            case "delete":
                if(args.length == 2) {
                    if (TWUtils.isInt(args[1])) {
                        if (resourceManager.isAResource(Integer.parseInt(args[1]))) {
                            //delete
                            ThalwyrnResource resource = resourceManager.getResource(Integer.parseInt(args[1]));
                            resource.delete();
                            ThalwyrnResources.getResourceManager().delete(resource);
                            player.sendMessage(TWMessages.deletedResource().replace("%id%", String.valueOf(resource.getId())));
                        } else {
                            player.sendMessage(TWMessages.notAResource());
                        }
                    }
                }
                return true;

            case "edit":
                if(TWUtils.isInt(args[1])) {
                    if (resourceManager.isAResource(Integer.parseInt(args[1]))) {
                        if (args.length >= 3) {
                            ThalwyrnResource resource = resourceManager.getResource(Integer.parseInt(args[1]));
                            switch (args[2]) {

                                case "xp":
                                    if (TWUtils.isInt(args[3])) {
                                        resource.setXp(Integer.parseInt(args[3]));
                                        player.sendMessage(TWMessages.editedResource().replace("%edited_value%", "xp").replace("%new_value%", args[3]).replace("%id%", String.valueOf(resource.getId())));
                                    } else {
                                        player.sendMessage(TWMessages.invalidNumber().replace("%value_entered%", args[3]));
                                    }
                                    return true;

                                case "job":
                                    if (resourceManager.getEnumJobNames().contains(args[3].toLowerCase())) {
                                        for (String name : resourceManager.getEnumJobNames()) {
                                            if (name.equalsIgnoreCase(TWUtils.convertToMMOCoreJob(args[3]).getName())) {
                                                JobTypes job = JobTypes.getMatching(name);
                                                resource.setJob(job);
                                                player.sendMessage(TWMessages.editedResource().replace("%edited_value%", "job").replace("%new_value%", job.getJobName()).replace("%id%", String.valueOf(resource.getId())));
                                                return true;
                                            }
                                        }
                                    }
                                    player.sendMessage("Not a job");
                                    return true;

                                case "left-click":
                                    if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                        ItemStack item = player.getInventory().getItemInMainHand();
                                        resource.setLeftClick(item);

                                        String name;
                                        if(item.hasItemMeta()) {
                                            if(item.getItemMeta().hasDisplayName()) {
                                                name = item.getItemMeta().getDisplayName();
                                            } else name = WordUtils.capitalizeFully(item.getType().name());
                                        } else name = WordUtils.capitalizeFully(item.getType().name());

                                        player.sendMessage(TWMessages.editedResource().replace("%edited_value%", "Left Click Item").replace("%new_value%", name).replace("%id%", String.valueOf(resource.getId())));
                                    } else player.sendMessage("Hand is empty!");
                                    return true;

                                case "right-click":
                                    if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                        ItemStack item = player.getInventory().getItemInMainHand();
                                        resource.setRightClick(item);

                                        String name;
                                        if(item.hasItemMeta()) {
                                            if(item.getItemMeta().hasDisplayName()) {
                                                name = item.getItemMeta().getDisplayName();
                                            } else name = WordUtils.capitalizeFully(item.getType().name());
                                        } else name = WordUtils.capitalizeFully(item.getType().name());

                                        player.sendMessage(TWMessages.editedResource().replace("%edited_value%", "Right Click Item").replace("%new_value%", name).replace("%id%", String.valueOf(resource.getId())));
                                    } else player.sendMessage("Hand is empty!");
                                    return true;

                                case "level":
                                    if (TWUtils.isInt(args[3])) {
                                        resource.setLevel(Integer.parseInt(args[3]));
                                        player.sendMessage(TWMessages.editedResource().replace("%edited_value%", "level").replace("%new_value%", args[3]).replace("%id%", String.valueOf(resource.getId())));
                                        return true;
                                    } else player.sendMessage(TWMessages.invalidNumber().replace("%value_entered%", args[3]));

//                                case "health":
//                                    if(ThalwyrnResourcesUtils.isInt(args[3])) {
//                                        resource.setHealth(Integer.parseInt(args[3]));
//                                        player.sendMessage("The health for resource: " + resource.getInternalName() + " has been set to: " + args[3]);
//                                        return true;
//                                    } else {player.sendMessage(ThalwyrnResourcesUtils.notAInt());}

                            }
                        } else player.sendMessage(TWMessages.invalidCommandUsage());
                    } else {
                        player.sendMessage(TWMessages.notAResource());
                        return true;
                    }
                }
                return true;

//            case "info":
//                if(args.length == 2) {
//                    if (ThalwyrnResourcesUtils.isInt(args[1])) {
//                        if (resourceManager.isAResource(Integer.parseInt(args[1]))) {
//                            //send player all info for resource
//                            player.sendMessage("Info command has been run");
//                        } else {
//                            player.sendMessage("Not a resource");
//                        }
//                    }
//                }
//                return true;

            case "copy":
                if(args.length == 2) {
                    if (TWUtils.isInt(args[1])) {
                        if (resourceManager.isAResource(Integer.parseInt(args[1]))) {

                            ThalwyrnResource copyFrom = resourceManager.getResource(Integer.parseInt(args[1]));
                            //ThalwyrnResource copied = resourceManager.createResource(copyFrom.getLevel(), copyFrom.getJob(), player.getLocation(), copyFrom.getType(), copyFrom.getExtra(), -4469);
                            ThalwyrnResource copied =  resourceManager.copyResource(copyFrom, player.getLocation());
                            resourceManager.spawnResource(copied, player.getLocation());
                            player.sendMessage("Resource: " + resourceManager.getResource(Integer.parseInt(args[1])).getId() + " has been copied. New ID is: " + copied.getId());
                        } else player.sendMessage(TWMessages.notAResource());
                    }
                }
                return true;

                //resource region-place <region-name> <id> <number of resources> <distance between>
            case "region-place":
                if(!plugin.isWorldGuardEnabled()) {
                    player.sendMessage("WorldGuard is not installed, cannot use region features");
                    return false;
                }
                if(args.length == 5) {
                    if (!TWUtils.isInt(args[2])) {
                        player.sendMessage(TWMessages.invalidNumber().replace("value_entered", args[2]));
                        return false;
                    }
                    if (!TWUtils.isInt(args[3])) {
                        player.sendMessage(TWMessages.invalidNumber().replace("value_entered", args[3]));
                        return false;
                    }
                    if (!TWUtils.isInt(args[4])) {
                        player.sendMessage(TWMessages.invalidNumber().replace("value_entered", args[4]));
                        return false;
                    }
                    if (!resourceManager.isAResource(Integer.parseInt(args[2]))) {
                        player.sendMessage(TWMessages.notAResource().replace("%id%", args[2]));
                        return false;
                    }

                    RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
                    if(manager == null) {
                        player.sendMessage("&4Could not find any region data for your current world");
                        return false;
                    }
                    if(manager.getRegion(args[1]) == null) {
                        player.sendMessage("&4Could not find any region with the id: &l&n" + args[1] + "&r&4 in your current world");
                        return false;
                    }

                    BlockVector3 min = manager.getRegion(args[1]).getMinimumPoint();
                    BlockVector3 max = manager.getRegion(args[1]).getMaximumPoint();

                    int minX = min.getBlockX();
                    int minZ = min.getBlockZ();

                    int maxX = max.getBlockX();
                    int maxZ = max.getBlockZ();

                    int minY = min.getBlockY();
                    int maxY = max.getBlockY();

                    int height = maxY - minY;

                    World world = player.getWorld();
                    List<Location> possibleLocations = new ArrayList<>();

                    for(int x = minX; x <= maxX; x++) {
                        for(int z = minZ; z <= maxZ; z++) {
                            int lY = world.getHighestBlockAt(x, z).getLocation().getBlockY();
                            Location l = new Location(world, x, lY, z);
                            if(lY >= minY && lY <= maxY) {
                                possibleLocations.add(l);
                            } else {
                                int dist = lY - max.getBlockY();
                                for(int loop = 0; loop <= height; loop++) {
                                    int n = dist + loop;
                                    int fY = lY - n;
                                    if(world.getBlockAt(x, fY, z).getType() == Material.AIR) {
                                        Location location = new Location(world, x, fY, z);
                                        possibleLocations.add(location);
                                        break;
                                    }

                                }
                            }
                        }
                    }

                    if(possibleLocations.isEmpty()) {
                        player.sendMessage(TWUtils.formattedColors("&cNo available air blocks in this region!"));
                        return false;
                    }

                    ThalwyrnResource resource = resourceManager.getResource(Integer.parseInt(args[2]));
                    int count = Integer.parseInt(args[3]);
                    int distance = Integer.parseInt(args[4]);
                    List<Location> chosenLocations = new ArrayList<>();

                    outer: for(int c = 1; c <= count; c++) {
                        int r = TWUtils.randomInteger(0, possibleLocations.size() - 1);

                        if(chosenLocations.isEmpty()) {
                            Location loc = possibleLocations.get(r);
                            //ThalwyrnResource copied = resourceManager.createResource(resource.getLevel(), resource.getJob(), loc, resource.getType(), resource.getExtra(), -4469);
                            ThalwyrnResource copied = resourceManager.copyResource(resource, loc);
                            resourceManager.spawnResource(copied, loc);
                            chosenLocations.add(loc);
                            possibleLocations.remove(loc);
                            continue;
                        }

                        List<Location> invalid = new ArrayList<>();
                        boolean locationPicked = false;

                        while(!locationPicked) {
                            Location location = possibleLocations.get(r);
                            if(chosenLocations.isEmpty()) {
                                player.sendMessage("Not enough possible locations.");
                                break outer;
                            }
                            for (Location loc : chosenLocations) {
                                if (!(location.distanceSquared(loc) <= distance)) {
                                    //ThalwyrnResource copied = resourceManager.createResource(resource.getLevel(), resource.getJob(), location, resource.getType(), resource.getExtra(), -4469);
                                    ThalwyrnResource copied = resourceManager.copyResource(resource, location);
                                    resourceManager.spawnResource(copied, location);
                                    possibleLocations.remove(location);
                                    locationPicked = true;
                                    break;
                                } else invalid.add(loc);
                            }
                            chosenLocations.removeAll(invalid);
                        }

                    }

                }
                return true;

            //resource group <job> <level>
            case "group":
                if(args.length != 3) {
                    player.sendMessage(TWMessages.invalidCommandUsage());
                    return false;
                }
                if(!TWUtils.isInt(args[2])) {
                    player.sendMessage(TWMessages.invalidNumber());
                    return false;
                }
                if(!resourceManager.getEnumJobNames().contains(args[1])) {
                    player.sendMessage(TWUtils.formattedColors("&cNo valid job with the name &r%job%").replace("%job%", args[1]));
                    return false;
                }
                if(player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    player.sendMessage(TWUtils.formattedColors("&cHand is empty!"));
                    return false;
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                JobTypes job = JobTypes.getMatching(args[1]);
                plugin.getItemGroups().addItemToGroup(job, Integer.parseInt(args[2]), item);

                //FINISH BELOW
                for(JobTypes jobTypes : plugin.getItemGroups().getItemGroups().keySet()) {
                    player.sendMessage("J: " + jobTypes.getJobName());
                    for(Integer integer : plugin.getItemGroups().getItemGroups().get(jobTypes).keySet()) {
                        player.sendMessage("L: " + integer + " ,J-" + jobTypes.getJobName());
                        for(ItemStack itemStack : plugin.getItemGroups().getItemGroups().get(jobTypes).get(integer)) {
                            player.sendMessage("I: " + itemStack.getType() + " ,L- " + integer + ", J- " + jobTypes.getJobName());
                        }
                    }
                }
                return true;

            case "help":
                player.sendMessage(TWUtils.formattedColors("&e&l&m---------------&r Help &e&l&m---------------"));
                for(TWCommands commands : TWCommands.values()) {
                    player.sendMessage(TWUtils.formattedColors("&6/resource " + commands.getName() + ": &r" + commands.getDescription()));
                }
                player.sendMessage(TWUtils.formattedColors("&e&l&m-----------------------------------"));
                return true;
        }
        return false;
    }

}
