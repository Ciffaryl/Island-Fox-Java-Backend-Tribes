package com.greenfoxacademy.islandfoxtribes.models.troop.troopDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RequestNewTroopsDTO {

    private String type;
    private int quantity;
}
