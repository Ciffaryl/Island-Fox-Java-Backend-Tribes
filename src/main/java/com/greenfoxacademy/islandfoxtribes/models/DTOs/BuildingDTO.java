package com.greenfoxacademy.islandfoxtribes.models.DTOs;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import lombok.Data;

@Data
public class BuildingDTO {

    private Long id;
    private String type;
    private int level;

    public BuildingDTO(Building building) {
        this.id = building.getId();
        this.type = building.getBuildingType().label;
        this.level = building.getLevel();
    }
}
