package com.pro4d.thalwyrnresources.utils;

public class TWMessages {

    public static String createResource() {
        return TWUtils.formattedColors("&2Created resource with id: &r%id%&2!");
    }

    public static String invalidCommandUsage() {
        return TWUtils.formattedColors("&cInvalid command usage!");
    }

    public static String reloadMessage() {
        return TWUtils.formattedColors("&aReloaded plugin!");
    }

    public static String notAPlayer() {
        return TWUtils.formattedColors("&4Only players can use this command.");
    }

    public static String deletedResource() {
        return TWUtils.formattedColors("&4Resource with the ID: &r%id%&4 has been deleted.");
    }

    public static String notAResource() {
        return TWUtils.formattedColors("&4No resource found with the ID: &r%id%");
    }

    public static String editedResource() {
        return TWUtils.formattedColors("&9The &r%edited_value%&9 for resource &r%id% &9has been set to &r%new_value%");
    }

    public static String invalidNumber() {
        return TWUtils.formattedColors("&7 &r' %value_entered% '&7 is not a valid number!");
    }




}
