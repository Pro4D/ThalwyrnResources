package com.pro4d.thalwyrnresources.holograms;

import com.pro4d.thalwyrnresources.enums.JobTypes;
import com.pro4d.thalwyrnresources.resources.ThalwyrnResource;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ProHologram {

    private final Location location;
    private int health;
    private int level;
    private String type;
    private JobTypes job;

    private final List<ProHologramLine> lines;
    private int id;
    private Entity entity;

    private final ProHologramLine leftClickHologram;
    private final ProHologramLine rightClickHologram;

    public ProHologram(ThalwyrnResource resource, Location loc) {
        this.location = loc.add(0, 1.2, 0);
        this.health = resource.getHealth();
        this.level = resource.getLevel();
        this.type = resource.getType();
        this.job = resource.getJob();

        lines = new ArrayList<>();

        leftClickHologram = new ProHologramLine(null, this, null);
        rightClickHologram = new ProHologramLine(null, this, null);


    }

    public void spawnHologram(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        armorStand.j(true);

        armorStand.n(true);
        //a r s t


        armorStand.s(false);

        armorStand.e(true);

        armorStand.t(true);

        switch (job) {
            case WOODCUTTING:
                //armorStand.setCustomName(IChatBaseComponent.a(WordUtils.capitalize(job.getJobName())));
                armorStand.a(IChatBaseComponent.a("Tree"));
                break;
//            case MINING:
//                armorStand.setCustomName(IChatBaseComponent.a(WordUtils.capitalize(ThalwyrnResourcesUtils.formatMessage(type))));
        }

        armorStand.a(true);

        entity = armorStand;
        id = armorStand.ae();

        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(armorStand);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), true);

        CraftPlayer craftPlayer = (CraftPlayer) player;

        craftPlayer.getHandle().b.a(spawnPacket);
        craftPlayer.getHandle().b.a(metadataPacket);

        Location loc = new Location(location.getWorld(), location.getX(), location.getY() - 0.237, location.getZ());
        new ProHologramLine(WordUtils.capitalizeFully(job.getJobName()) + " Lv. Min: " + level, this, loc).spawnLine(player);

    }

    public void despawn(Player player) {
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer) player).getHandle().b.a(destroyPacket);
        if(!(lines.isEmpty())) {
            lines.forEach(hologramLine -> hologramLine.despawn(player));
        }
    }

    public void updateHologram(ThalwyrnResource resource, Player player) {
        Bukkit.getOnlinePlayers().forEach(this::despawn);

        this.health = resource.getHealth();
        this.level = resource.getLevel();
        this.type = resource.getType();
        this.job = resource.getJob();

        spawnHologram(player);
    }


    public void setLeftClickHologram(String variable) {
        Location loc = new Location(location.getWorld(), location.getX(), location.getY() - .7, location.getZ());

        leftClickHologram.setLineLocation(loc);
        leftClickHologram.setVariable(variable);
    }

    public void setRightClickHologram(String variable) {
        Location loc = new Location(location.getWorld(), location.getX(), location.getY() - .98, location.getZ());

        rightClickHologram.setLineLocation(loc);
        rightClickHologram.setVariable(variable);
    }

    public Location getLocation() {
        return location;
    }

    public List<ProHologramLine> getLines() {
        return lines;
    }

    public int getId() {
        return id;
    }

    public Entity getEntity() {
        return entity;
    }
}
