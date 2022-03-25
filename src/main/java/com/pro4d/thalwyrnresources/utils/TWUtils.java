package com.pro4d.thalwyrnresources.utils;

import com.sk89q.worldedit.math.Vector3;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.Profession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TWUtils {

    private static final Logger logger = Bukkit.getLogger();

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static String formattedColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    //Replace underscores and dashes with a space
    public static String formatMessage(String text) {
        if(text.contains("-")) {
            text = text.replace("-", " ");
            return text;
        }
        if(text.contains("_")) {
            text = text.replace("_", " ");
            return text;
        }
        return text;
    }


    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Profession convertToMMOCoreJob(String name) {
        for(Profession profession : MMOCore.plugin.professionManager.getAll()) {
            if(profession.getId().equalsIgnoreCase(name)) {
                return profession;
            }
        }
        return null;
    }

    public static int randomInteger(int min, int max) {
        return (int)Math.floor(Math.random()*(max-min+1) + min);
    }

}
