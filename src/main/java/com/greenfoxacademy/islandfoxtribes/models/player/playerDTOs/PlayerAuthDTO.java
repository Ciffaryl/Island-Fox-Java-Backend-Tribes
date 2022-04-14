package com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerAuthDTO {

    @JsonProperty("ruler")
    private String ruler;

    @JsonProperty("kingdomId")
    private Long kingdomId;

    @JsonProperty("kingdomName")
    private String kingdomName;

}
