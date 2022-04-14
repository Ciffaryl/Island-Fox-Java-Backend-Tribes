package com.greenfoxacademy.islandfoxtribes.models.kingdom;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.location.Location;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;

import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Kingdom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private Location location;

    private boolean buildingStatus;


    private int loyalty;

    @OneToMany(mappedBy = "kingdom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @ToString.Exclude

    private List<Resource> resourceList;

    @OneToMany(mappedBy = "kingdom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @ToString.Exclude
    private List<Building> buildingList;

    @OneToMany(mappedBy = "kingdom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @ToString.Exclude
    private List<Troop> troopList;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "player_id")
    private Player player;

    public Kingdom(String name, Player player) {
        this.name = name;
        this.player = player;
        this.loyalty = 100;
        this.buildingList = new ArrayList<>();
        this.troopList = new ArrayList<>();
    }

    public void addTroop(Troop troop) {
        this.troopList.add(troop);
    }

    public void addBuilding(Building building) {
        this.buildingList.add(building);
    }

    public void addResource(Resource resource) {
        this.resourceList.add(resource);
    }

    public Resource findFoodResource() {
        for (Resource resource : resourceList) {
            if (ResourceType.FOOD.equals(resource.getResourceType())) {
                return resource;
            }
        }
        return null;
    }

    public Resource findGoldResource() {
        for (Resource resource : resourceList) {
            if (ResourceType.GOLD.equals(resource.getResourceType())) {
                return resource;
            }
        }
        return null;
    }

    public Building findTownHall() {
        for (Building building : buildingList) {
            if (BuildingType.TOWN_HALL.equals(building.getBuildingType())) {
                return building;
            }
        }
        return null;
    }

    public Building findFarm() {
        for (Building building : buildingList) {
            if (BuildingType.FARM.equals(building.getBuildingType())) {
                return building;
            }
        }
        return null;
    }

    public Building findMine() {
        for (Building building : buildingList) {
            if (BuildingType.MINE.equals(building.getBuildingType())) {
                return building;
            }
        }
        return null;
    }

    public int sumFarmLevels() {
        int sumFarmLevels = 0;
        for (Building building : buildingList) {
            if (BuildingType.FARM.equals(building.getBuildingType())) {
                sumFarmLevels += building.getLevel();
            }
        }
        return sumFarmLevels;
    }

    public int sumMineLevels() {
        int sumMineLevels = 0;
        for (Building building : buildingList) {
            if (BuildingType.MINE.equals(building.getBuildingType())) {
                sumMineLevels += building.getLevel();
            }
        }
        return sumMineLevels;
    }

    public List<Troop> getTroopList() {
        return troopList;
    }

    public Integer getPopulation() {
        Integer result = 0;
        for (Building building : buildingList) {
            result += building.getLevel();
        }
        return result;
    }

    public int amountOfBuildingsOfType(String buildingTypeLabel) {
        int amount = 0;
        for (Building building : buildingList) {
            if (building.getBuildingType().label.equals(buildingTypeLabel)) {
                amount++;
            }
        }
        return amount;
    }

    public Building findBarracks() {
        for (Building building : buildingList) {
            if (BuildingType.BARRACKS.equals(building.getBuildingType())) {
                return building;
            }
        }
        return null;
    }

    public List<Troop> getSpies() {
        List<Troop> result = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getTroopType().equals(TroopType.SPY)) {
                result.add(troop);
            }
        }
        return result;
    }

    public List<Troop> getKnights() {
        List<Troop> result = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getTroopType().equals(TroopType.KNIGHT)) {
                result.add(troop);
            }
        }
        return result;
    }

    public List<Troop> getSwordsmen() {
        List<Troop> result = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getTroopType().equals(TroopType.SWORDSMAN)) {
                result.add(troop);
            }
        }
        return result;
    }

    public List<Troop> getArchers() {
        List<Troop> result = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getTroopType().equals(TroopType.ARCHER)) {
                result.add(troop);
            }
        }
        return result;
    }

    public List<Troop> getSenators() {
        List<Troop> result = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getTroopType().equals(TroopType.SENATOR)) {
                result.add(troop);
            }
        }
        return result;
    }

    public Building findWall() {
        for (Building building : buildingList) {
            if (BuildingType.WALL.equals(building.getBuildingType())) {
                return building;
            }
        }
        return null;
    }

    public List<Troop> getDefendersArmy() {
        List<Troop> defendingArmy = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getBattle() == null) {
                defendingArmy.add(troop);
            }
        }
        return defendingArmy;
    }

    public List<Troop> getSpiesWithoutBattle() {
        List<Troop> result = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getTroopType().equals(TroopType.SPY) && troop.getBattle() == null) {
                result.add(troop);
            }
        }
        return result;
    }

    public List<Building> findStorages(BuildingType buildingType) {
        List<Building> result = new ArrayList<>();
        for (Building building : buildingList) {
            if (building.getBuildingType().equals(buildingType)) {
                result.add(building);
            }
        }
        return result;
    }
}
