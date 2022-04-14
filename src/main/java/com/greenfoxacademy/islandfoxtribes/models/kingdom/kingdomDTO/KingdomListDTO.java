package com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter

public class KingdomListDTO {
    private List<KingdomForListDTO> kingdoms;
}
