package com.greenfoxacademy.islandfoxtribes.services.leaderboards;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.LeaderboardsDto.*;

public interface LeaderboardsService {

    Long calculatePointsFromKingdomBuildings(Kingdom kingdom);

    Long calculatePointsFromKingdomTroops(Kingdom kingdom);

    BuildingLeaderboardDTO createBuildingDtoForLeaderboard(Kingdom kingdom);

    BuildingResultsLeaderboard showBuildingsLeaderboard();

    TroopLeaderboardDTO createTroopDtoForLeaderboard(Kingdom kingdom);

    TroopResultsLeaderboard showTroopLeaderboard();

    KingdomLeaderboardDTO createKingdomDtoForLeaderboards(Kingdom kingdom);

    KingdomResultsLeaderboard showKingdomLeaderboard();


}
