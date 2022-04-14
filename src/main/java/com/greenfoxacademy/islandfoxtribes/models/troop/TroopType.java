package com.greenfoxacademy.islandfoxtribes.models.troop;

public enum TroopType {

    SWORDSMAN("Swordsman"),
    ARCHER("Archer"),
    KNIGHT("Knight"),
    SPY("Spy"),
    SENATOR("Senator");

    public final String label;

    TroopType(String label) {
        this.label = label;
    }

    public static TroopType fromLabel(String label) {
        for (TroopType type : TroopType.values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        return null;
    }

}
