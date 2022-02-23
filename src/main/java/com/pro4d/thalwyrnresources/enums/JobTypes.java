package com.pro4d.thalwyrnresources.enums;

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
        return this.toString();
    }

    public JobTypes getMatching(String s) {
        for(JobTypes jobTypes : JobTypes.values()) {
            if(jobTypes.getJobName().equals(s)) return jobTypes;
        }
        return null;
    }

}
