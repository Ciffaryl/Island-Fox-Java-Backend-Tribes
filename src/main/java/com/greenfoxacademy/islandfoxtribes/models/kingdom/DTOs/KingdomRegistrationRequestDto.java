package com.greenfoxacademy.islandfoxtribes.models.kingdom.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class KingdomRegistrationRequestDto {

    private Integer coordinateY;
    private Integer coordinateX;
    private Long kingdomId;

}
