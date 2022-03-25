package com.pro4d.thalwyrnresources.enums;

public enum MiningOre {

    STONE,
    COPPER_BLOCK,
    COAL_BLOCK,
    IRON_BLOCK,
    GOLD_BLOCK,
    LAPIS_BLOCK,
    REDSTONE_BLOCK,
    DIAMOND_BLOCK,
    ANCIENT_DEBRIS;

    public String getOreName() {
        return this.toString();
    }

    public static MiningOre getMatching(String s) {
        for(MiningOre ore : MiningOre.values()) {
            if(ore.getOreName().equalsIgnoreCase(s)) return ore;
        }
        return null;
    }

}
