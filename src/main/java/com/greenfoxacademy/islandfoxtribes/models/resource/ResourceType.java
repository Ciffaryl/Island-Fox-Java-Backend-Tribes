package com.greenfoxacademy.islandfoxtribes.models.resource;

public enum ResourceType {

    FOOD("Food"),
    GOLD("Gold");

    public final String label;

    ResourceType(String label) {
        this.label = label;
    }
}
