package com.greenfoxacademy.islandfoxtribes.models.kingdom.LeaderboardsDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class KingdomResultsLeaderboard {

    private List<KingdomLeaderboardDTO> results;
}
