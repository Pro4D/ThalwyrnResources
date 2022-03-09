package com.pro4d.thalwyrnresources.holograms;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProHologramLine {

    private String name;
    private Location location;
    private ProHologram parentHologram;

    private int id;

    private final List<UUID> visibleTo;

    public ProHologramLine(ProHologram parent, Location lineLocation) {
        parentHologram = parent;
        parent.getLines().add(this);
        location = lineLocation;
        visibleTo = new ArrayList<>();

        name = "Hologram Line";
    }

    public void spawnLine(Player player) {
        if(visibleTo.contains(player.getUniqueId())) return;
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        armorStand.j(true); //invisible
        armorStand.n(true); //name visible
        armorStand.s(false); //base_plate
        armorStand.e(true); //gravity
        armorStand.t(true); //marker

        armorStand.a(IChatBaseComponent.a(name)); //set name

        armorStand.a(true); //small

        id = armorStand.ae(); //id

        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(armorStand);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(id, armorStand.ai(), true);

        CraftPlayer craftPlayer = (CraftPlayer) player;

        craftPlayer.getHandle().b.a(spawnPacket);
        craftPlayer.getHandle().b.a(metadataPacket);

        visibleTo.add(player.getUniqueId());
    }

    public void despawn(Player player) {
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer) player).getHandle().b.a(destroyPacket);
        visibleTo.remove(player.getUniqueId());
    }

    public void updateLine() {
        parentHologram.getLocation().getWorld().getPlayers().forEach(p -> {
            if(!parentHologram.getParentResource().getPlayerRespawnTime().containsKey(p.getUniqueId())) {
                despawn(p);
                spawnLine(p);
            }
        });
    }

    public String getName() {
        return name;
    }

    public ProHologram getParentHologram() {
        return parentHologram;
    }

    public Location getLocation() {
        return location;
    }


    public void setLocation(Location location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentHologram(ProHologram parentHologram) {
        this.parentHologram = parentHologram;
    }


}
