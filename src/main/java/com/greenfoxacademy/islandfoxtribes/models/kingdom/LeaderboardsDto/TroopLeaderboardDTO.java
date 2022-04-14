package com.greenfoxacademy.islandfoxtribes.models.kingdom.LeaderboardsDto;

import lombok.Data;

@Data
public class TroopLeaderboardDTO {

    private String ruler;

    private String kingdom;

    private Integer troops;

    private Long points;
}
