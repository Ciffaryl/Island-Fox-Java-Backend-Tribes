package com.greenfoxacademy.islandfoxtribes.models.building.buildBuildingDtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class RequestNewBuildingDto {

    private String type;

    @JsonCreator
    public RequestNewBuildingDto(String type) {
        this.type = type;
    }
}
