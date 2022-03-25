package com.pro4d.thalwyrnresources.enums;

public enum TWCommands {

    PLACE("place", "Place a new resource."),
    DELETE("delete", "Completely delete a existing resource "),
    EDIT("edit", "Edit a existing resource's values"),
    COPY("copy", "Create a new resource from a existing resource."),
    GROUP("group", "Manage item groups for resources."),
    REGION("region-place", "Randomly place copies of a existing resource inside a WorldGuard Region");

    private final String name;
    private final String description;

    TWCommands(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
