package com.pro4d.thalwyrnresources.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.pro4d.thalwyrnresources.ThalwyrnResources;
import com.pro4d.thalwyrnresources.enums.MiningOre;
import com.pro4d.thalwyrnresources.utils.TWUtils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ConstructionManager {
    private static final Map<MiningOre, ItemStack> headMap = new HashMap<>();
    //private final Map<List<Block>, Integer> resourceBlockMap = new HashMap<>();

    private final String path;
    public ConstructionManager(ThalwyrnResources plugin) {
        String worldEditPath = plugin.getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder().getAbsolutePath();
        path = worldEditPath + "/" + "schematics";


        headMap.put(MiningOre.COAL_BLOCK, createSkull("ewogICJ0aW1lc3RhbXAiIDogMTY0MDQ5MjI2NDQyMywKICAicHJvZmlsZUlkIiA6ICIxYWEzNWI4NzAwYWU0MTM5ODIxZjM4NDM0ZGQ5ZGEyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQcm80RCIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZDZhNDdkMzg5Yjc4NTg4YTc1MzBiNmE1MTNhNzEyMmE0YjBjYjA1YTliNGNhODIzNTU2N2E3YTM4OTQ5MTA1IgogICAgfQogIH0KfQ=="));

        headMap.put(MiningOre.DIAMOND_BLOCK, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I1ZmFmNGNkODcxMzhjODcxY2M2YTg2NzU4MTdhODk5ODVhM2NiODk3MjFhNGM3NjJmZTY2NmZmNjE4MWMyNCJ9fX0="));

        headMap.put(MiningOre.REDSTONE_BLOCK, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZhYjAwODgxOTgxYzk5Yzk0OGMzZmY4NGRmMmY2YWZhNWQ3YzNhNTI2YzMxMjMxMjRhYzM1YTQxZDY4ZWY1NSJ9fX0="));

        headMap.put(MiningOre.GOLD_BLOCK, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M4MTRhNTI0NGFiNDhmNDIyMGRmM2E4N2NmNzdhYTE3MjA3NjVkZmUzMWE3YjJiYmI4OThiZjFhZWY1ZGIzNSJ9fX0="));

        headMap.put(MiningOre.IRON_BLOCK, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjg3ZDA2ZjFmMWRkODRkMzlmNmE5ZDEwNTgzNDg0NmFjMzZhYzhkZTBiMjUwNGFlMjc2YzFhZGYyYjMxZmU1NSJ9fX0="));

        headMap.put(MiningOre.STONE, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThiYzc2YjVjZGYxYTU4YTExZTI5ZGFlY2QxMWVkNTVkMWI5ZTBlMWYzNTllZTQ0ZDZiNDJiMjg2NjY3MTM0MiJ9fX0="));

        headMap.put(MiningOre.LAPIS_BLOCK, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY1NjA3NDE4ODU0M2Y5YjRhZGNkNjQ1Mjc4MzIwNjhjYmUwOGYxNTZlOWVlOGVkOWMyMDNiOGFkODVhNzZmNyJ9fX0="));

        headMap.put(MiningOre.ANCIENT_DEBRIS, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU3YTEyMDA1Nzg5MWNkMjQyZWMzNDgzOTE1YjNmMWI1MDIzYTZiYTZlYTNmMzA3MmFlNmY1NWRmZGVkZmZhOCJ9fX0="));

        headMap.put(MiningOre.COPPER_BLOCK, createSkull("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJjOGRkOWEyMDk3NWE2NTVlNGQ2MGU0MWY2MTIyNTQxODBjZDllMTE0YWE4NGNmYTJlOGQwMWY5YWUwMGMifX19"));

    }

    public ItemStack createSkull(String textureValue) {

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        byte[] bytes = Base64.decodeBase64(textureValue);
        String decoded = new String(bytes);

        JsonObject jsonObject = (JsonObject) new JsonParser().parse(decoded);
        String textureURL = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

        byte[] encodedTexture = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", textureURL).getBytes());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", new String(encodedTexture)));

        Field skullProfileField = null;
        try {
            skullProfileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        assert skullProfileField != null;
        skullProfileField.setAccessible(true);

        try {
            skullProfileField.set(skullMeta, profile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);

        return skull;
    }

    public static Map<MiningOre, ItemStack> getHeadMap() {return headMap;}


    public boolean isSchematic(String name) {
        File schemFile = new File(path + "/" + name + ".schem");
        return schemFile.exists();
    }


    public void pasteSchematic(String name, Location location) {
        //Bukkit.broadcastMessage("Getting ready to paste");
        File schemFile = new File(path + "/" + name + ".schem");

        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(location.getWorld());

        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        assert format != null;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        try {

            ClipboardReader reader = format.getReader(new FileInputStream(schemFile));
            Clipboard clipboard = reader.read();
            
            try {
                EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);

                ClipboardHolder holder = new ClipboardHolder(clipboard);
                Operation operation = holder
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, y, z))
                        .ignoreAirBlocks(true)
                        .build();

                try {
                    Operations.complete(operation);
                    editSession.close();

                } catch (WorldEditException e) {
                    TWUtils.log(Level.SEVERE, TWUtils.formattedColors("&cError with world edit"));
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Clipboard getClipboard(String name) {
        File schemFile = new File(path + "/" + name + ".schem");

        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        assert format != null;

        try {

            ClipboardReader reader = format.getReader(new FileInputStream(schemFile));
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public Map<List<Block>, Integer> getResourceBlockMap() {
//        return resourceBlockMap;
//    }

//    public ItemStack createCustomSkull(String texture) {
//        texture = "http://textures.minecraft.net/texture/" + texture;
//
//        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
//        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
//
//        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
//
//
//
//            //DEBUG THIS (CHECK WHAT IT OUTPUTS)
//        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture).getBytes());
//
//
//
//
//        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
//        Field profileField = null;
//
//        try {
//            profileField = skullMeta.getClass().getDeclaredField("profile");
//        } catch (NoSuchFieldException | SecurityException e) {
//            e.printStackTrace();
//        }
//
//        assert profileField != null;
//        profileField.setAccessible(true);
//
//        try {
//            profileField.set(skullMeta, profile);
//        } catch (IllegalArgumentException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        skull.setItemMeta(skullMeta);
//        return skull;
//    }

}
