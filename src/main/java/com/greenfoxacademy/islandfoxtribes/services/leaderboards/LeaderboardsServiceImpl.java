package com.greenfoxacademy.islandfoxtribes.services.leaderboards;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.LeaderboardsDto.*;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaderboardsServiceImpl implements LeaderboardsService {

    private final KingdomRepository kingdomRepository;

    @Autowired
    public LeaderboardsServiceImpl(KingdomRepository kingdomRepository) {
        this.kingdomRepository = kingdomRepository;
    }

    @Override
    public BuildingLeaderboardDTO createBuildingDtoForLeaderboard(Kingdom kingdom) {
        BuildingLeaderboardDTO kingdomDto = new BuildingLeaderboardDTO();
        kingdomDto.setKingdom(kingdom.getName());
        kingdomDto.setRuler(kingdom.getPlayer().getUserName());
        kingdomDto.setBuildings(kingdom.getBuildingList().size());
        kingdomDto.setPoints(calculatePointsFromKingdomBuildings(kingdom));
        return kingdomDto;
    }

    @Override
    public BuildingResultsLeaderboard showBuildingsLeaderboard() {

        List<BuildingLeaderboardDTO> results = new ArrayList<>();

        for (Kingdom kingdom : kingdomRepository.findAll()) {
            BuildingLeaderboardDTO kingdomDto = createBuildingDtoForLeaderboard(kingdom);
            results.add(kingdomDto);
        }
        List<BuildingLeaderboardDTO> sortedResults = results.stream()
                .sorted(Comparator.comparingLong(BuildingLeaderboardDTO::getPoints).reversed())
                .collect(Collectors.toList());


        return new BuildingResultsLeaderboard(sortedResults);
    }

    @Override
    public TroopLeaderboardDTO createTroopDtoForLeaderboard(Kingdom kingdom) {
        TroopLeaderboardDTO kingdomDto = new TroopLeaderboardDTO();
        kingdomDto.setKingdom(kingdom.getName());
        kingdomDto.setRuler(kingdom.getPlayer().getUserName());
        kingdomDto.setTroops(kingdom.getTroopList().size());
        kingdomDto.setPoints(calculatePointsFromKingdomTroops(kingdom));
        return kingdomDto;
    }

    @Override
    public TroopResultsLeaderboard showTroopLeaderboard() {
        List<TroopLeaderboardDTO> results = new ArrayList<>();

        for (Kingdom kingdom : kingdomRepository.findAll()) {
            TroopLeaderboardDTO kingdomDto = createTroopDtoForLeaderboard(kingdom);
            results.add(kingdomDto);
        }
        List<TroopLeaderboardDTO> sortedResults = results.stream()
                .sorted(Comparator.comparingLong(TroopLeaderboardDTO::getPoints).reversed())
                .collect(Collectors.toList());

        return new TroopResultsLeaderboard(sortedResults);

    }

    @Override
    public KingdomLeaderboardDTO createKingdomDtoForLeaderboards(Kingdom kingdom) {
        KingdomLeaderboardDTO kingdomDto = new KingdomLeaderboardDTO();
        kingdomDto.setKingdom(kingdom.getName());
        kingdomDto.setRuler(kingdom.getPlayer().getUserName());
        kingdomDto.setTroops(kingdom.getTroopList().size());
        kingdomDto.setBuildings(kingdom.getBuildingList().size());
        kingdomDto.setPoints(calculatePointsFromKingdomTroops(kingdom) + calculatePointsFromKingdomBuildings(kingdom));
        return kingdomDto;
    }

    @Override
    public KingdomResultsLeaderboard showKingdomLeaderboard() {
        List<KingdomLeaderboardDTO> results = new ArrayList<>();

        for (Kingdom kingdom : kingdomRepository.findAll()) {
            KingdomLeaderboardDTO kingdomDto = createKingdomDtoForLeaderboards(kingdom);
            results.add(kingdomDto);
        }
        List<KingdomLeaderboardDTO> sortedResults = results.stream()
                .sorted(Comparator.comparingLong(KingdomLeaderboardDTO::getPoints).reversed())
                .collect(Collectors.toList());

        return new KingdomResultsLeaderboard(sortedResults);

    }

    @Override
    public Long calculatePointsFromKingdomBuildings(Kingdom kingdom) {
        long kingdomPoints = 0;

        for (Building building : kingdom.getBuildingList()) {
            if (BuildingType.TOWN_HALL.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.TOWN_HALL_VALUE * building.getLevel();
            } else if (BuildingType.ACADEMY.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.ACADEMY_VALUE * building.getLevel();
            } else if (BuildingType.BARRACKS.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.BARRACKS_VALUE * building.getLevel();
            } else if (BuildingType.FARM.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.FARM_VALUE * building.getLevel();
            } else if (BuildingType.MINE.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.MINE_VALUE * building.getLevel();
            } else if (BuildingType.TREASURY.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.TREASURY_VALUE * building.getLevel();
            } else if (BuildingType.GRANARY.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.GRANARY_VALUE * building.getLevel();
            } else if (BuildingType.WALL.equals(building.getBuildingType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.WALL_VALUE * building.getLevel();
            }
        }
        return kingdomPoints;
    }

    @Override
    public Long calculatePointsFromKingdomTroops(Kingdom kingdom) {
        long kingdomPoints = 0;

        for (Troop troop : kingdom.getTroopList()) {
            if (TroopType.SWORDSMAN.equals(troop.getTroopType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.SWORDSMAN_VALUE * troop.getLevel();
            } else if (TroopType.ARCHER.equals(troop.getTroopType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.ARCHER_VALUE * troop.getLevel();
            } else if (TroopType.KNIGHT.equals(troop.getTroopType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.KNIGHT_VALUE * troop.getLevel();
            } else if (TroopType.SPY.equals(troop.getTroopType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.SPY_VALUE * troop.getLevel();
            } else if (TroopType.SENATOR.equals(troop.getTroopType())) {
                kingdomPoints = kingdomPoints + (long) GameConstants.SENATOR_VALUE * troop.getLevel();
            }
        }
        return kingdomPoints;
    }
}
