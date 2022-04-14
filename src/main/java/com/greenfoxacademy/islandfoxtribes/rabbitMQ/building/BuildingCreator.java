package com.greenfoxacademy.islandfoxtribes.rabbitMQ.building;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Transactional
@Component
public class BuildingCreator {

    private final KingdomRepository kingdomRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingFactory buildingFactory;

    @Autowired
    public BuildingCreator(KingdomRepository kingdomRepository,
                           BuildingRepository buildingRepository,
                           BuildingFactory buildingFactory) {
        this.kingdomRepository = kingdomRepository;
        this.buildingRepository = buildingRepository;
        this.buildingFactory = buildingFactory;
    }

    public void createOrUpgradeBuilding(Long kingdomId, Long buildingId, String buildingType, String command) {
        Kingdom kingdom = this.kingdomRepository.getById(kingdomId);
        if ("create".equals(command)) {
            Building newBuilding = this.buildingFactory.createBuilding(BuildingType.fromLabel(buildingType));
            kingdom.addBuilding(newBuilding);
            newBuilding.setKingdom(kingdom);
        } else if ("upgrade".equals(command)) {
            Building buildingToUpgrade = this.buildingRepository.getById(buildingId);
            buildingToUpgrade.setLevel(buildingToUpgrade.getLevel() + 1);
        }
        // You can only be building one building at a time. So after we finish we change status to false.
        kingdom.setBuildingStatus(false);
        this.kingdomRepository.save(kingdom);
    }

}
