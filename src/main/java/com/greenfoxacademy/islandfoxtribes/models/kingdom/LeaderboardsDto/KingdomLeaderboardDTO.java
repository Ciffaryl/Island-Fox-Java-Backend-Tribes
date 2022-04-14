package com.greenfoxacademy.islandfoxtribes.models.kingdom.LeaderboardsDto;

import lombok.Data;

@Data
public class KingdomLeaderboardDTO {

    private String ruler;

    private String kingdom;

    private Integer buildings;

    private Integer troops;

    private Long points;
}
