package com.greenfoxacademy.islandfoxtribes.models.DTOs;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomForListDTO;
import lombok.Data;

import java.util.List;

@Data
public class KingdomDetailsDTO {

    private KingdomForListDTO kingdom;
    private List<ResourceDTO> resources;
    private List<BuildingDTO> buildings;
    private List<TroopDTO> troops;

}
