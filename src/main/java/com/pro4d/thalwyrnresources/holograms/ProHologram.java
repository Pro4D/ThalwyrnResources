package com.pro4d.thalwyrnresources.holograms;

import com.pro4d.thalwyrnresources.resources.ThalwyrnResource;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ProHologram {

    private Location location;

    private String name;

    private final ThalwyrnResource parentResource;

    private final List<ProHologramLine> lines;
    private int id;

    private ProHologramLine leftClickHologram;
    private ProHologramLine rightClickHologram;

    public ProHologram(ThalwyrnResource resource, Location loc) {
        location = loc.clone().add(0, 1.2, 0);

        parentResource = resource;
        lines = new ArrayList<>();

        name = "Hologram";
        resource.setHologram(this);
    }

    public void spawnHologram(Player player) {
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

        Location loc = new Location(location.getWorld(), location.getX(), location.getY() - 0.249, location.getZ());

        ProHologramLine line = new ProHologramLine(this, loc);
        line.setName(WordUtils.capitalizeFully(parentResource.getJob().getJobName()) + " Lv. Min: " + parentResource.getLevel());
        //line.spawnLine(player);

        for(ProHologramLine hologramLine : lines) {
            hologramLine.updateLine();
        }
        Bukkit.broadcastMessage("LS: " + lines.size());
    }

    public void despawn(Player player) {
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer) player).getHandle().b.a(destroyPacket);
        if(!(lines.isEmpty())) {
            lines.forEach(hologramLine -> hologramLine.despawn(player));
        }
    }

    public void updateHologram(ThalwyrnResource resource) {
        resource.getLocation().getWorld().getPlayers().forEach(p -> {
            if(!resource.getPlayerRespawnTime().containsKey(p.getUniqueId())) {
                resource.getHologram().despawn(p);
                spawnHologram(p);
            }
        });
    }

    public ProHologramLine getLeftClickHologram() {
        return leftClickHologram;
    }

    public ProHologramLine getRightClickHologram() {
        return rightClickHologram;
    }

    public String getName() {
        return name;
    }

    public void setLeftClickHologram(ProHologramLine line) {
        leftClickHologram = line;
    }

    public void setRightClickHologram(ProHologramLine line) {
        //Location loc = new Location(location.getWorld(), location.getX(), location.getY() - .98, location.getZ());

        //rightClickHologram.setLocation(loc);
        //rightClickHologram.setName(variable);

        rightClickHologram = line;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public List<ProHologramLine> getLines() {
        return lines;
    }

    public ThalwyrnResource getParentResource() {
        return parentResource;
    }


//        switch (job) {
//            case WOODCUTTING:
//                armorStand.setCustomName(IChatBaseComponent.a(WordUtils.capitalize(job.getJobName())));
//                break;
//            case MINING:
//                armorStand.setCustomName(IChatBaseComponent.a(WordUtils.capitalize(ThalwyrnResourcesUtils.formatMessage(type))));

//    public int getId() {
//        return id;
//    }
}
