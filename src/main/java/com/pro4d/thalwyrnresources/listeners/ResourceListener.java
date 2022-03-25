package com.pro4d.thalwyrnresources.listeners;

import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.holograms.ProHologramLine;
import com.pro4d.thalwyrnresources.managers.ResourceManager;
import com.pro4d.thalwyrnresources.resources.ThalwyrnResource;
import com.pro4d.thalwyrnresources.utils.TWUtils;
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
        if(!(event.hasBlock())) return;
        if(event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        for (ThalwyrnResource resource : resourceManager.getAllResources()) {
            if (resource.getInteractBlocks() != null) {
                List<Block> listOfBlock = resource.getInteractBlocks();
                if (listOfBlock.contains(block)) {
                    if(resource.getPlayerRespawnTime().containsKey(player.getUniqueId())) {
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                        event.setCancelled(true);
                        return;
                    }

                    PlayerData playerData = PlayerData.get(player.getUniqueId());
                    Profession profession = TWUtils.convertToMMOCoreJob(resource.getJob().getJobName());
                    assert profession != null;

                    if(playerData.getCollectionSkills().getLevel(MMOCore.plugin.professionManager.get(profession.getId())) < resource.getLevel()) {
                        player.sendMessage("You do not meet the requirements to collect this resource.");
                        event.setCancelled(true);
                        return;
                    }

                    if(event.hasItem()) {
                        if(plugin.getItemGroups().getItemGroups().containsKey(resource.getJob())) {
                            if (!plugin.getItemGroups().getItemGroups().get(JobTypes.getMatching(profession.getName())).get(resource.getLevel()).contains(event.getItem())) {
                                player.sendMessage(TWUtils.formattedColors("&cIncorrect item used to collect this resource!"));
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }

                    if (!(interactedPlayers.contains(player.getUniqueId()))) interactedPlayers.add(player.getUniqueId());

                    decreaseHealth(resource, event.getAction(), player);

                    ItemStack item = null;
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        if(resource.getLeftClick() != null) {
                            resource.setLeftClick(resource.getLeftClick());
                            item = resource.getLeftClick();
                        }
                    } else {
                        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                            if (resource.getRightClick() != null) {
                                resource.setRightClick(resource.getRightClick());
                                item = resource.getRightClick();
                            }
                        }
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
                        player.sendMessage(TWUtils.formattedColors("&aYou have been given " + resource.getXp() + " in " + profession.getName()));
                    }
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    private void moveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if(from.getBlock() == to.getBlock()) return;
        if(interactedPlayers.contains(player.getUniqueId())) {
            if(to.getPitch() >= 20 || to.getYaw() >= 45) event.setCancelled(true);
            //PITCH UP & DOWN
            //YAW SIDE TO SIDE
        }

        for(ThalwyrnResource resource : resourceManager.getAllResources()) {
            if(resource.getLocation().distanceSquared(to) <= 1500) {
                resource.getHologram().spawnHologram(player);
            }
        }
    }

    @EventHandler
    private void joinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for(ThalwyrnResource resource : resourceManager.getAllResources()) {
            if(resource.getPlayerRespawnTime().containsKey(player.getUniqueId())) {
                startTimer(resource, player, resource.getPlayerRespawnTime().get(player.getUniqueId()));
            } else {
                if(resource.getLocation().distanceSquared(player.getLocation()) <= 1000) {
                    resource.getHologram().spawnHologram(player);
                }
            }
        }
    }

    @EventHandler
    private void respawnEvent(PlayerRespawnEvent event) {
        for(ThalwyrnResource resource : resourceManager.getAllResources()) {
            if(resource.getPlayerRespawnTime().containsKey(event.getPlayer().getUniqueId())) {
                Player player = event.getPlayer();

                startTimer(resource, player, resource.getPlayerRespawnTime().get(player.getUniqueId()));
            }
        }
    }


    private void decreaseHealth(ThalwyrnResource resource, Action action, Player player) {
        int respawnTime = ThalwyrnResources.getOptionsConfig().getInt("respawn-time");
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        new BukkitRunnable() {
            int time = resource.getHealth();

            @Override
            public void run() {

                if(time != 0) {
                    PacketPlayOutAnimation armSwingPacket = new PacketPlayOutAnimation(entityPlayer, 0);
                    entityPlayer.b.a(armSwingPacket);
                    resource.getHologram().getLines().forEach(line -> line.despawn(player));
                    time--;

                    //resource.getHologram().getLines().get(2).despawn(player);
                    //resource.getHologram().getLines().get(2).setVariable("[ " + builder + " ]");

                } else {
                    interactedPlayers.remove(player.getUniqueId());
                    resource.getHologram().despawn(player);

                    resource.getPlayerRespawnTime().put(player.getUniqueId(), respawnTime);
                    startTimer(resource, player, resource.getPlayerRespawnTime().get(player.getUniqueId()));
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);


    }


    private void replaceBlocks(ThalwyrnResource resource, Player player) {
        resource.getHologram().despawn(player);
        Clipboard clipboard = plugin.getConstructionManager().getClipboard(resource.getExtra());
        Region region = clipboard.getRegion();

        BlockVector3 origin = clipboard.getOrigin();
        Location resourceLoc = resource.getLocation();

        for(BlockVector3 v : region) {
            Vector3 originalVector = origin.subtract(v).multiply(-1).toVector3();
            Vector3 vector = Vector3.at(originalVector.getX(), originalVector.getY(), originalVector.getZ());
            Location loc = new Location(resourceLoc.getWorld(), resourceLoc.getBlockX() + vector.getX(), (resourceLoc.getBlockY() + vector.getY() - 1),resourceLoc.getBlockZ() + vector.getZ());

            BlockData data = BukkitAdapter.adapt(clipboard.getFullBlock(v));
            //if(data.getMaterial() != Material.AIR) {
            resource.getTemp().add(loc);
            player.sendBlockChange(loc, data);
            //}

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
                        resourceManager.respawnResource(resource, player);
                        interactedPlayers.remove(player.getUniqueId());
                        resource.getPlayerRespawnTime().remove(player.getUniqueId());

                        resource.getHologram().spawnHologram(player);
                        for(ProHologramLine line : resource.getHologram().getLines()) {
                            line.spawnLine(player);
                        }

                        cancel();
                    }
                } else {
                    //Bukkit.broadcastMessage("Could not find " + player.getDisplayName() + ", they had " + resource.getPlayerRespawnTime().get(player.getUniqueId()) + " second(s) left on their timer.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

    }

    public List<UUID> getInteractedPlayers() {
        return interactedPlayers;
    }

}