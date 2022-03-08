package com.pro4d.thalwyrnresources.commands;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.managers.ResourceManager;
import com.pro4d.thalwyrnresources.resources.ThalwyrnResource;
import com.pro4d.thalwyrnresources.utils.ThalwyrnResourcesUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ResourceCommand implements CommandExecutor {

    private final ResourceManager resourceManager;
    private final ThalwyrnResources plugin;
    //private RegionContainer container;
    public ResourceCommand(ThalwyrnResources plugin) {
        this.resourceManager = ThalwyrnResources.getResourceManager();
        this.plugin = plugin;
//        if(plugin.isWorldGuardEnabled()) {
//            container = WorldGuard.getInstance().getPlatform().getRegionContainer();
//        }
        Bukkit.getPluginCommand("resource").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Invalid command usage");
            return false;
        }
        if(args[0].equals("reload") && args.length == 1) {
            resourceManager.cleanup();
            resourceManager.validateConfig();
            sender.sendMessage(ThalwyrnResourcesUtils.formattedColors("&aReloaded configs"));
            return true;
        }


        if(!(sender instanceof Player)) {
            sender.sendMessage(ThalwyrnResourcesUtils.formattedColors("&cOnly players can use this command!"));
            return true;
        }
        Player player = (Player) sender;

        //          0     1             2              3
        //resource edit <id> <value (ie. job/xp)> new value

        //          0      1     2      3            4
        //resource place level  job <value1> <OPTIONAL:value2>

        //           0      1
        //resource delete <id>

        //          0      1
        //resource info <id>

        //            0    1      2
        //resource region <id> <count>

        // -place
        // -delete
        // -edit
        // -info

        switch (args[0]) {

            case "place":
                //create
                if(ThalwyrnResourcesUtils.isInt(args[1])) {
                    int level = Integer.parseInt(args[1]);

                    if(ResourceManager.enumJobNames.contains(args[2].toUpperCase())) {

                        for(String name : ResourceManager.enumJobNames) {

                            if(name.equalsIgnoreCase(args[2].toUpperCase())) {
                                JobTypes job = JobTypes.valueOf(name);
                                switch (job) {
                                    case WOODCUTTING:
                                        if(ThalwyrnResources.getConstructionManager().isSchematic(args[3])) {
                                            if(ThalwyrnResources.getConstructionManager().isSchematic(args[4])) {
                                                ThalwyrnResource resource = resourceManager.createResource(level, job, player.getLocation(), args[3], args[4], -4469);
                                                player.sendMessage("Created resource with id: " + resource.getId());

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
                    if (ThalwyrnResourcesUtils.isInt(args[1])) {
                        if (resourceManager.isAResource(Integer.parseInt(args[1]))) {
                            //delete
                            resourceManager.getResource(Integer.parseInt(args[1])).delete();
                            player.sendMessage("Resource: " + resourceManager.getResource(Integer.parseInt(args[1])).getId() + " has been deleted");
                        } else {
                            player.sendMessage("No resource with that id");
                        }
                    }
                }
                return true;

            case "edit":
                if(ThalwyrnResourcesUtils.isInt(args[1])) {
                    if (resourceManager.isAResource(Integer.parseInt(args[1]))) {
                        if (args.length >= 3) {
                            ThalwyrnResource resource = resourceManager.getResource(Integer.parseInt(args[1]));
                            switch (args[2]) {

                                case "xp":
                                    if (ThalwyrnResourcesUtils.isInt(args[3])) {
                                        resource.setXp(Integer.parseInt(args[3]));
                                        player.sendMessage("The xp for resource: " + resource.getId() + " has been set to: " + args[3]);
                                    } else {
                                        player.sendMessage(ThalwyrnResourcesUtils.notAInt());
                                    }
                                    return true;

                                case "job":
                                    if (ResourceManager.enumJobNames.contains(args[3].toLowerCase())) {
                                        for (String name : ResourceManager.enumJobNames) {
                                            if (name.equalsIgnoreCase(ThalwyrnResourcesUtils.convertToMMOCoreJob(args[3]).getName())) {
                                                JobTypes job = JobTypes.valueOf(name);
                                                resource.setJob(job);
                                                player.sendMessage("The job for resource: " + resource.getId() + " has been set to: " + job.getJobName());
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
                                        player.sendMessage("Left Click item for resource: " + resource.getId() + " has been set to: " + item.getType().name().toUpperCase());
                                    } else {
                                        player.sendMessage("Hand is empty!");
                                    }
                                    return true;

                                case "right-click":
                                    if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                        ItemStack item = player.getInventory().getItemInMainHand();
                                        resource.setRightClick(item);
                                        player.sendMessage("Right click item for resource: " + resource.getId() + " has been set to: " + item.getType().name().toUpperCase());
                                    } else {
                                        player.sendMessage("Hand is empty!");
                                    }
                                    return true;

                                case "level":
                                    if (ThalwyrnResourcesUtils.isInt(args[3])) {
                                        resource.setLevel(Integer.parseInt(args[3]));
                                        player.sendMessage("The level for resource: " + resource.getId() + " has been set to: " + args[3]);
                                        return true;
                                    } else {
                                        player.sendMessage(ThalwyrnResourcesUtils.notAInt());
                                    }

//                                case "health":
//                                    if(ThalwyrnResourcesUtils.isInt(args[3])) {
//                                        resource.setHealth(Integer.parseInt(args[3]));
//                                        player.sendMessage("The health for resource: " + resource.getInternalName() + " has been set to: " + args[3]);
//                                        return true;
//                                    } else {player.sendMessage(ThalwyrnResourcesUtils.notAInt());}

                            }
                        } else {
                            player.sendMessage(ThalwyrnResourcesUtils.invalidUsage());
                        }
                    } else {
                        player.sendMessage("Not a resource");
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
                    if (ThalwyrnResourcesUtils.isInt(args[1])) {
                        if (resourceManager.isAResource(Integer.parseInt(args[1]))) {

                            ThalwyrnResource copyFrom = resourceManager.getResource(Integer.parseInt(args[1]));
                            ThalwyrnResource copied = resourceManager.createResource(copyFrom.getLevel(), copyFrom.getJob(), player.getLocation(), copyFrom.getType(), copyFrom.getExtra(), -4469);
                            resourceManager.spawnResource(copied, player.getLocation());
                            player.sendMessage("Resource: " + resourceManager.getResource(Integer.parseInt(args[1])).getId() + " has been copied. New ID is: " + copied.getId());
                        } else {
                            player.sendMessage("No resource with that id");
                        }
                    }
                }
                return true;

                //resource region-place <region-name> <id> <number of resources> <distance between>
//            case "region-place":
//                if(!plugin.isWorldGuardEnabled()) {
//                    player.sendMessage("WorldGuard is not installed, cannot use region features");
//                    return false;
//                }
//                if(args.length == 5) {
//                    if (!ThalwyrnResourcesUtils.isInt(args[2])) {
//                        player.sendMessage("Not a valid number");
//                        return false;
//                    }
//                    if (!ThalwyrnResourcesUtils.isInt(args[3])) {
//                        player.sendMessage("Not a valid number");
//                        return false;
//                    }
//                    if (!ThalwyrnResourcesUtils.isInt(args[4])) {
//                        player.sendMessage("Not a valid number");
//                        return false;
//                    }
//                    if (!resourceManager.isAResource(Integer.parseInt(args[1]))) {
//                        player.sendMessage("No resource with that id");
//                        return false;
//                    }
//
////                    RegionManager manager = container.get(BukkitAdapter.adapt(player.getWorld()));
////                    if(manager == null) {
////                        player.sendMessage("Could not find any region data for your current world");
////                        return false;
////                    }
////                    if(manager.getRegion(args[1]) == null) {
////                        player.sendMessage("Could not find any region with the id: " + args[1] + " in your current world");
////                        return false;
////                    }
//
//                    //BlockVector3 min = manager.getRegion(args[1]).getMinimumPoint();
//                    //BlockVector3 max = manager.getRegion(args[1]).getMaximumPoint();
//
//                    //Bukkit.broadcastMessage("Min for region: " + args[1] + " = " + min);
//                    //Bukkit.broadcastMessage("Max for region: " + args[1] + " = " + max);
//
//                    //ThalwyrnResource copyFrom = resourceManager.getResource(Integer.parseInt(args[1]));
//
//
//                }
//                return true;

        }
        return false;
    }
}
