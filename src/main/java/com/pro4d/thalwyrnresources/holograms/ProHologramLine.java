package com.pro4d.thalwyrnresources.holograms;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ProHologramLine {

    private String variable;
    private Location lineLocation;


    private int id;

    public ProHologramLine(String variable, ProHologram parentHologram, Location location) {
        this.variable = variable;
        parentHologram.getLines().add(this);
        lineLocation = location;

        //spawnLine(player);
    }

    public void spawnLine(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) lineLocation.getWorld()).getHandle(), lineLocation.getX(), lineLocation.getY(), lineLocation.getZ());
        armorStand.j(true);
        armorStand.n(true);
        armorStand.s(false);
        armorStand.e(true);
        armorStand.t(true);

        armorStand.a(IChatBaseComponent.a(variable));

        armorStand.a(true);

        id = armorStand.ae();

        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(armorStand);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), true);

        CraftPlayer craftPlayer = (CraftPlayer) player;

        craftPlayer.getHandle().b.a(spawnPacket);
        craftPlayer.getHandle().b.a(metadataPacket);

    }

    public void despawn(Player player) {
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer) player).getHandle().b.a(destroyPacket);
    }

    public void updateLine() {
        Bukkit.getOnlinePlayers().forEach(this::despawn);
        Bukkit.getOnlinePlayers().forEach(this::spawnLine);
    }

    public Location getLineLocation() {
        return lineLocation;
    }

    public int getId() {
        return id;
    }

    public void setVariable(String variable) {
        this.variable = variable;
        updateLine();
    }

    public void setLineLocation(Location location) {
        this.lineLocation = location;
        updateLine();
    }

}
