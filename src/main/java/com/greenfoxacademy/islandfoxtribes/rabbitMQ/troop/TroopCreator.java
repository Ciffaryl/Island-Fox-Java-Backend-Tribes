package com.greenfoxacademy.islandfoxtribes.rabbitMQ.troop;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

// This class is responsible for building the units.

@Transactional
@Component
public class TroopCreator {

    private final KingdomRepository kingdomRepository;
    private final BuildingRepository buildingRepository;
    private final TroopFactory troopFactory;

    @Autowired
    public TroopCreator(KingdomRepository kingdomRepository,
                        BuildingRepository buildingRepository,
                        TroopFactory troopFactory) {
        this.kingdomRepository = kingdomRepository;
        this.buildingRepository = buildingRepository;
        this.troopFactory = troopFactory;
    }

    public void createTroops(Long kingdomId, Long buildingId, String troopTypeLabel, String task) {

        Kingdom kingdom = kingdomRepository.getById(kingdomId);
        Building building = buildingRepository.getById(buildingId);
        if ("create".equals(task)) {
            Troop actualTroop =
                    troopFactory.createTroop(buildingRepository.getById(buildingId),
                            TroopType.fromLabel(troopTypeLabel));
            actualTroop.setKingdom(kingdom);
            kingdom.addTroop(actualTroop);
            building.setCreating(false);
            kingdomRepository.save(kingdom);
        }
    }

    public void setBuildingCreatingToTrue(Long buildingId) {

        buildingRepository.getById(buildingId).setCreating(true);
        buildingRepository.save(buildingRepository.getById(buildingId));

    }

}
