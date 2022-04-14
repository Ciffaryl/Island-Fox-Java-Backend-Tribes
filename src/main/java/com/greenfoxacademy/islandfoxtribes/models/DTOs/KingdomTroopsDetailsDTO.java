package com.greenfoxacademy.islandfoxtribes.models.DTOs;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomForListDTO;
import lombok.Data;

import java.util.List;

@Data
public class KingdomTroopsDetailsDTO {

    private KingdomForListDTO kingdom;
    private List<TroopDTO> troops;

}
