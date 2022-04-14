package com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class KingdomForListDTO {
    private Long kingdomId;
    private String kingdomName;
    private String ruler;
    private Integer population;
    private Location location;

    public KingdomForListDTO(Kingdom kingdom) {
        this.kingdomId = kingdom.getId();
        this.kingdomName = kingdom.getName();
        this.ruler = kingdom.getPlayer().getUserName();
        this.population = kingdom.getPopulation();
        this.location = kingdom.getLocation();
    }
}

