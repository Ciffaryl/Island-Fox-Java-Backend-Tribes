package com.greenfoxacademy.islandfoxtribes.models.troop.troopDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpgradeTroopsDTO {

    private List<Long> troopsId;
}
