package com.pro4d.thalwyrnresources.listeners;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.managers.ResourceManager;
import com.pro4d.thalwyrnresources.resources.ThalwyrnResource;
import com.pro4d.thalwyrnresources.utils.ThalwyrnResourcesUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceListener implements org.bukkit.event.Listener {

    private final ThalwyrnResources plugin;
    private final ResourceManager resourceManager;
    private final List<UUID> interactedPlayers;

    //change how cooldown players are stored in this class

    public ResourceListener(ThalwyrnResources plugin) {
        this.plugin = plugin;

        interactedPlayers = new ArrayList<>();
        resourceManager = ThalwyrnResources.getResourceManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void detectClick(PlayerInteractEvent event) {
        if (!(event.hasBlock())) return;
        if(event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        for (ThalwyrnResource resource : resourceManager.getAllResources()) {
            if (resource.getInteractBlocks() != null) {
                List<Block> listOfBlock = resource.getInteractBlocks();
                if (listOfBlock.contains(block)) {

                    if(resource.getPlayerRespawnTime().containsKey(player.getUniqueId())) {
                        //player.sendBlockChange(block.getLocation(), block.getBlockData());
                        event.setCancelled(true);
                        return;
                    }

                    PlayerData playerData = PlayerData.get(player.getUniqueId());
                    Profession profession = ThalwyrnResourcesUtils.convertToMMOCoreJob(resource.getJob().getJobName());
                    assert profession != null;

                    if(playerData.getCollectionSkills().getLevel(MMOCore.plugin.professionManager.get(profession.getId())) < resource.getLevel()) {
                        player.sendMessage("You do not meet the requirements to collect this resource.");
                        return;
                    }


                    if (!(interactedPlayers.contains(player.getUniqueId()))) {
                        interactedPlayers.add(player.getUniqueId());
                    }

                    decreaseHealth(resource, player);

                    ItemStack item;
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {

                        item = resource.getLeftClick();

                    } else {

                        item = resource.getRightClick();

                    }
                    if (item != null) {

                        if (player.getInventory().firstEmpty() == -1) {
                            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
                        } else {
                            player.getInventory().addItem(item);
                        }

                    }

                    if (resource.getXp() != 0) {
                        playerData.getCollectionSkills().giveExperience(profession, resource.getXp(), EXPSource.OTHER);
                        player.sendMessage(ThalwyrnResourcesUtils.formattedColors("&aYou have been given " + resource.getXp() + " in " + profession.getName()));
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    //spawn resource (aka send packet) when close enough?

    @EventHandler
    private void moveEvent(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        if(interactedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            //PITCH UP & DOWN
            //YAW SIDE TO SIDE
        }

//        if(cooldownPlayers.containsKey(player.getUniqueId())) {
//            ThalwyrnResource resource = cooldownPlayers.get(player.getUniqueId());
//            Location to = event.getTo();
//            Location from = event.getFrom();
//            assert to != null;
//            World world = to.getWorld();
//            assert world != null;
//
//            float x = Float.parseFloat(df.format(to.getX() - from.getX()));
//            float y = Float.parseFloat(df.format(to.getY() - from.getY()));
//            float z = Float.parseFloat(df.format(to.getZ() - from.getZ()));
//
//            if(Math.abs(x) > y && Math.abs(x) > z) {
//                //player.sendMessage("X is the greatest");
//                if(x > 0) {
//                    Location loc = new Location(world, to.getBlockX() + 1, to.getBlockY(), to.getBlockZ());
//                    Block b = world.getBlockAt(loc);
//                    if(isGhostBlock(resource.getId(), b.getLocation())) {
//                        if (b.getType() != Material.AIR) {
//                            Bukkit.broadcastMessage("X: " + world.getBlockAt(new Location(world, to.getBlockX() + 1, to.getBlockY(), to.getBlockZ())).getType().name());
//                            replaceAndStore(b, player);
//                        }
//                    }
//
//                } else {
//                    Location loc = new Location(world, to.getBlockX() - 1, to.getBlockY(), to.getBlockZ());
//                    Block b = world.getBlockAt(loc);
//                    if(isGhostBlock(resource.getId(), b.getLocation())) {
//                        if (b.getType() != Material.AIR) {
//                            replaceAndStore(b, player);
//                        }
//                    }
//
//                }
//
//            } else if(Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)) {
//                //player.sendMessage("Z is the greatest");
//                if(z > 0 ) {
//                    Location loc = new Location(world, to.getBlockX(), to.getBlockY(), to.getBlockZ() + 1);
//                    Block b = world.getBlockAt(loc);
//                    if(isGhostBlock(resource.getId(), b.getLocation())) {
//                        if (b.getType() != Material.AIR) {
//                            replaceAndStore(b, player);
//                        }
//                    }
//
//                } else {
//                    Location loc = new Location(world, to.getBlockX(), to.getBlockY(), to.getBlockZ() - 1);
//                    Block b = world.getBlockAt(loc);
//                    if(isGhostBlock(resource.getId(), b.getLocation())) {
//                        if (b.getType() != Material.AIR) {
//                            replaceAndStore(b, player);
//                        }
//                    }
//                }
//
//            } else if(Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
//                //player.sendMessage("Y is the greatest");
//                Location loc = new Location(world, to.getBlockX(), to.getBlockY() - 1, to.getBlockZ());
//                Block b = world.getBlockAt(loc);
//                if(isGhostBlock(resource.getId(), b.getLocation())) {
//                    if (b.getType() != Material.AIR) {
//                        replaceAndStore(b, player);
//                    }
//                }
//            }
//
//        }
//
//        if(event.getFrom().getBlockX() != event.getTo().getBlockX() && event.getFrom().getBlockY() != event.getTo().getBlockY() && event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
//            if (playersWhoDeletedBlocks.contains(event.getPlayer().getUniqueId())) {
//                Location from = new Location(event.getFrom().getWorld(), event.getFrom().getBlockX(), event.getFrom().getBlockY(), event.getFrom().getBlockZ());
//                //Bukkit.broadcastMessage("X: " + from.getX() + " Y: " + from.getY() + " Z: " + from.getZ());
//
//                if (blocksFakeDestroyed.containsKey(from)) {
//                    from.getWorld().setBlockData(from, blocksFakeDestroyed.get(from));
//                    playersWhoDeletedBlocks.remove(player.getUniqueId());
//                    blocksFakeDestroyed.remove(from);
//                }
//            }
//        }

    }

    @EventHandler
    private void joinEvent(PlayerJoinEvent event) {
        for(ThalwyrnResource resource : resourceManager.getAllResources()) {
            if(resource.getPlayerRespawnTime().containsKey(event.getPlayer().getUniqueId())) {
                Player player = event.getPlayer();

                //resourceManager.despawnResource(resource, player);
                //replaceBlocks(resource, player);

                startTimer(resource, player, resource.getPlayerRespawnTime().get(player.getUniqueId()));

                //replaceBlocks(resource, player);

//                new BukkitRunnable() {
//                    int time = timeLeft;
//
//                    @Override
//                    public void run() {
//                        if (Bukkit.getPlayer(player.getUniqueId()) != null) {
//                            if (time == (timeLeft - 1)) {
//
//                                resourceManager.despawnResource(resource, player);
//                                replaceBlocks(resource, player);
//
//                            }
//                            if (time != 0) {
//                                resource.getPlayerRespawnTime().replace(player.getUniqueId(), time--);
//                            } else {
//
////                                resourceManager.despawnResource(resource, player);
//
//                                for(Location loc : resource.getTemp()) {
//                                    loc.getWorld().getBlockAt(loc).getState().update();
//                                }
//                                resourceManager.respawnResource(resource, player);
//                                resource.getPlayerRespawnTime().remove(player.getUniqueId());
//                                cancel();
//                            }
//                        } else {
//                            Bukkit.broadcastMessage("Could not find " + player.getDisplayName() + ", they had " + resource.getPlayerRespawnTime().get(player.getUniqueId()) + " second(s) left on their timer-C");
//                            cancel();
//                        }
//                    }
//                }.runTaskTimer(plugin, 0L, 20L);
//            } else {
//                if(event.getPlayer().getLocation().distanceSquared(resource.getHologram().getLocation()) <= 20) {
//                    resource.getHologram().spawnHologram(event.getPlayer());
//                }

            }
        }
    }

    @EventHandler
    private void respawnEvent(PlayerRespawnEvent event) {
        for(ThalwyrnResource resource : resourceManager.getAllResources()) {
            if(resource.getPlayerRespawnTime().containsKey(event.getPlayer().getUniqueId())) {
                Player player = event.getPlayer();

                //resourceManager.despawnResource(resource, player);
                //replaceBlocks(resource, player);

                startTimer(resource, player, resource.getPlayerRespawnTime().get(player.getUniqueId()));

                //replaceBlocks(resource, player);

//                new BukkitRunnable() {
//                    int time = timeLeft;
//
//                    @Override
//                    public void run() {
//                        if (Bukkit.getPlayer(player.getUniqueId()) != null) {
//                            if (time == (timeLeft - 1)) {
//
//                                resourceManager.despawnResource(resource, player);
//                                replaceBlocks(resource, player);
//
//                            }
//                            if (time != 0) {
//                                resource.getPlayerRespawnTime().replace(player.getUniqueId(), time--);
//                            } else {
//
////                                resourceManager.despawnResource(resource, player);
//
//                                for(Location loc : resource.getTemp()) {
//                                    loc.getWorld().getBlockAt(loc).getState().update();
//                                }
//                                resourceManager.respawnResource(resource, player);
//                                resource.getPlayerRespawnTime().remove(player.getUniqueId());
//                                cancel();
//                            }
//                        } else {
//                            Bukkit.broadcastMessage("Could not find " + player.getDisplayName() + ", they had " + resource.getPlayerRespawnTime().get(player.getUniqueId()) + " second(s) left on their timer-C");
//                            cancel();
//                        }
//                    }
//                }.runTaskTimer(plugin, 0L, 20L);
//            } else {
//                if(event.getPlayer().getLocation().distanceSquared(resource.getHologram().getLocation()) <= 20) {
//                    resource.getHologram().spawnHologram(event.getPlayer());
//                }

            }
        }
    }


    private void decreaseHealth(ThalwyrnResource resource, Player player) {
        int respawnTime = ThalwyrnResources.getLevelConfig().getInt("respawn-time");

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        new BukkitRunnable() {
            int timeLeft = resource.getHealth() / 100;

            @Override
            public void run() {

                if(timeLeft != 0) {
                    PacketPlayOutAnimation armSwingPacket = new PacketPlayOutAnimation(entityPlayer, 0);
                    entityPlayer.b.a(armSwingPacket);
                    resource.getHologram().getLines().forEach(line -> line.despawn(player));
                    timeLeft--;

                    //resource.getHologram().getLines().get(2).despawn(player);
                    //resource.getHologram().getLines().get(2).setVariable("[ " + builder + " ]");

                } else {
                    interactedPlayers.remove(player.getUniqueId());
                    resource.getHologram().despawn(player);

                    //resourceManager.despawnResource(resource, player);
                    //replaceBlocks(resource, player);

                    resource.getPlayerRespawnTime().put(player.getUniqueId(), respawnTime);
                    startTimer(resource, player, resource.getPlayerRespawnTime().get(player.getUniqueId()));

//                    new BukkitRunnable() {
//                        int time = respawnTime;
//
//                        @Override
//                        public void run() {
//                            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
//                                if(time != 0) {
//                                    resource.getPlayerRespawnTime().replace(player.getUniqueId(), time--);
//                                } else {
////                                    resourceManager.despawnResource(resource, player);
//
//                                    for(Location loc : resource.getTemp()) {
//                                        loc.getWorld().getBlockAt(loc).getState().update();
//                                    }
//                                    resourceManager.respawnResource(resource, player);
//                                    interactedPlayers.remove(player.getUniqueId());
//                                    resource.getPlayerRespawnTime().remove(player.getUniqueId());
//                                    cancel();
//                                }
//                            } else {
//                                Bukkit.broadcastMessage("Could not find " + player.getDisplayName() + ", they had " + resource.getPlayerRespawnTime().get(player.getUniqueId()) + " second(s) left on their timer.");
//                                cancel();
//                            }
//                        }
//                    }.runTaskTimer(plugin, 0L, 20L);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }

//    private void respawnTemp(ThalwyrnResource resource, Player player) {
//        int minX = resource.getMin().getBlockX();
//        int minY = resource.getMin().getBlockY();
//        int minZ = resource.getMin().getBlockZ();
//
//        int maxX = resource.getMax().getBlockX();
//        int maxY = resource.getMax().getBlockY();
//        int maxZ = resource.getMax().getBlockZ();
//
//
//        World world = resource.getMin().getWorld();
//        assert world != null;
//        List<Block> blockList = new ArrayList<>();
//
//        for (int x = minX; x != maxX + 1; x++) {
//            for (int y = minY; y != maxY + 1; y++) {
//                for (int z = minZ; z != maxZ + 1; z++) {
//                    Block b = world.getBlockAt(x, y, z);
//                    blockList.add(b);
//                }
//            }
//        }
//
//        resource.getGhostBlocks().forEach(block -> block.getState().update());
//        for (Block block : blockList) {
//            player.sendBlockChange(block.getLocation(), block.getBlockData());
//            //block.getState().update();
//        }
//
//    }


    private void replaceBlocks(ThalwyrnResource resource, Player player) {
        Clipboard clipboard = ThalwyrnResources.getConstructionManager().getClipboard(resource.getExtra());
        Region region = clipboard.getRegion();

        BlockVector3 origin = clipboard.getOrigin();
        Location resourceLoc = resource.getLocation();

        for(BlockVector3 v : region) {
            Vector3 originalVector = origin.subtract(v).multiply(-1).toVector3();
            Vector3 vector = Vector3.at(originalVector.getX(), originalVector.getY(), originalVector.getZ());
            Location loc = new Location(resourceLoc.getWorld(), resourceLoc.getBlockX() + vector.getX(), (resourceLoc.getBlockY() + vector.getY() - 1),resourceLoc.getBlockZ() + vector.getZ());

            resource.getTemp().add(loc);

            BlockData data = BukkitAdapter.adapt(clipboard.getFullBlock(v));
            player.sendBlockChange(loc, data);

        }
    }

    private void startTimer(ThalwyrnResource resource, Player player, int startTime) {
        new BukkitRunnable() {
            int time = startTime;


            @Override
            public void run() {
                if (Bukkit.getPlayer(player.getUniqueId()) != null) {

                    if (time == (startTime - 1)) {
                        resourceManager.despawnResource(resource, player);
                        replaceBlocks(resource, player);
                    }
                    if(time != 0) {
                        resource.getPlayerRespawnTime().replace(player.getUniqueId(), time--);
                    } else {
                        //resourceManager.despawnResource(resource, player);

                        for(Location loc : resource.getTemp()) {
                            loc.getWorld().getBlockAt(loc).getState().update();
                        }
                        resourceManager.respawnResource(resource, player);
                        interactedPlayers.remove(player.getUniqueId());
                        resource.getPlayerRespawnTime().remove(player.getUniqueId());
                        cancel();
                    }
                } else {
                    //Bukkit.broadcastMessage("Could not find " + player.getDisplayName() + ", they had " + resource.getPlayerRespawnTime().get(player.getUniqueId()) + " second(s) left on their timer.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }



//    private void replaceAndStore(Block block, Player player) {
//        Location bLoc = new Location(block.getLocation().getWorld(), block.getX(), block.getY(), block.getZ());
//        //blocksFakeDestroyed.put(bLoc, block.getBlockData());
//        //playersWhoDeletedBlocks.add(player.getUniqueId());
//        block.setType(Material.AIR);
//    }

//    private boolean isGhostBlock(int id, Location toCheck) {
//        if(!resourceManager.isAResource(id)) return false;
//        ThalwyrnResource resource = resourceManager.getResource(id);
//        if(!resource.getJob().equals(JobTypes.WOODCUTTING)) return false;
//        return resource.getGhostBlocks().contains(toCheck);
//    }

//    public Map<Location, BlockData> getBlocksFakeDestroyed() {
//        return blocksFakeDestroyed;
//    }



}