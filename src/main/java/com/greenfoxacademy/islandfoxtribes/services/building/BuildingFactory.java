package com.greenfoxacademy.islandfoxtribes.services.building;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import org.springframework.stereotype.Component;

@Component
public class BuildingFactory {

    public Building createBuilding(BuildingType buildingType) {

        if (BuildingType.TOWN_HALL.equals(buildingType)) {
            return new Building(
                    BuildingType.TOWN_HALL,
                    GameConstants.TOWN_HALL_COST,
                    GameConstants.TOWN_HALL_CONSTRUCTION_TIME,
                    GameConstants.TOWN_HALL_CAPACITY);

        } else if (BuildingType.FARM.equals(buildingType)) {
            return new Building(
                    BuildingType.FARM,
                    GameConstants.FARM_COST,
                    GameConstants.FARM_CONSTRUCTION_TIME,
                    GameConstants.FARM_CAPACITY);

        } else if (BuildingType.ACADEMY.equals(buildingType)) {
            return new Building(
                    BuildingType.ACADEMY,
                    GameConstants.ACADEMY_COST,
                    GameConstants.ACADEMY_CONSTRUCTION_TIME,
                    GameConstants.ACADEMY_CAPACITY);

        } else if (BuildingType.MINE.equals(buildingType)) {
            return new Building(
                    BuildingType.MINE,
                    GameConstants.MINE_COST,
                    GameConstants.MINE_CONSTRUCTION_TIME,
                    GameConstants.MINE_CAPACITY);

        } else if (BuildingType.BARRACKS.equals(buildingType)) {
            return new Building(BuildingType.BARRACKS,
                    GameConstants.BARRACKS_COST,
                    GameConstants.BARRACKS_CONSTRUCTION_TIME,
                    GameConstants.BARRACKS_CAPACITY);
        } else if (BuildingType.WALL.equals(buildingType)) {
            return new Building(BuildingType.WALL,
                    GameConstants.WALL_COST,
                    GameConstants.WALL_CONSTRUCTION_TIME,
                    GameConstants.WALL_CAPACITY);
        } else if (BuildingType.GRANARY.equals(buildingType)) {
            return new Building(BuildingType.GRANARY,
                    GameConstants.GRANARY_COST,
                    GameConstants.GRANARY_CONSTRUCTION_TIME,
                    GameConstants.GRANARY_CAPACITY);
        } else if (BuildingType.TREASURY.equals(buildingType)) {
            return new Building(BuildingType.TREASURY,
                    GameConstants.TREASURY_COST,
                    GameConstants.TREASURY_CONSTRUCTION_TIME,
                    GameConstants.TREASURY_CAPACITY);
        }
        return null;
    }
}
