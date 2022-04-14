package com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class KingdomForRenameDTO {

    private Long kingdomId;
    private String kingdomName;

    public KingdomForRenameDTO(Kingdom kingdom) {
        this.kingdomId = kingdom.getId();
        this.kingdomName = kingdom.getName();
    }
}
