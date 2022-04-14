package com.greenfoxacademy.islandfoxtribes.rabbitMQ.upgrades;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopService;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Transactional
@Component
public class UpgradeCreator {

    private final TroopService troopService;
    private final BuildingRepository buildingRepository;

    public UpgradeCreator(TroopService troopService, BuildingRepository buildingRepository) {
        this.troopService = troopService;
        this.buildingRepository = buildingRepository;
    }

    public void upgradingTroops(Long kingdomId, Long buildingId, String upgradeName) {

        String[] upgradeNameList = upgradeName.split("/");

        String troopType = upgradeNameList[0];
        String stat = upgradeNameList[1];
        int level = Integer.parseInt((upgradeNameList)[3]);

        buildingRepository.getById(buildingId).setCreating(false);

        troopService.upgradeTroops(kingdomId, troopType, stat, level);

    }

    public void setAcademyCreatingToTrue(Long buildingId) {
        Building building = buildingRepository.getById(buildingId);

        building.setCreating(true);
        buildingRepository.save(building);
    }

}
