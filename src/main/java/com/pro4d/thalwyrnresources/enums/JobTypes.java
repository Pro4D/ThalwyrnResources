package com.pro4d.thalwyrnresources.enums;

import org.apache.commons.lang.WordUtils;

public enum JobTypes {

//    ALCHEMY,
//    ENCHANTING,
//    FARMING,
//    FISHING,
//    MINING,
//    SMELTING,
//    SMITHING,
    WOODCUTTING;

    public String getJobName() {
        return WordUtils.capitalizeFully(this.toString());
    }

    public static JobTypes getMatching(String s) {
        for(JobTypes jobTypes : JobTypes.values()) {
            if(jobTypes.getJobName().equalsIgnoreCase(s)) return jobTypes;
        }
        return null;
    }

}
