package com.greenfoxacademy.islandfoxtribes.models.building;

public enum BuildingType {

    TOWN_HALL("Town hall"),
    FARM("Farm"),
    ACADEMY("Academy"),
    BARRACKS("Barracks"),
    MINE("Mine"),
    WALL("Wall"),
    GRANARY("Granary"),
    TREASURY("Treasury");

    public final String label;

    BuildingType(String label) {
        this.label = label;
    }

    public static BuildingType fromLabel(String label) {
        for (BuildingType type : BuildingType.values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        return null;
    }
}

