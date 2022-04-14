package com.greenfoxacademy.islandfoxtribes.services.resource;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.resource.ResourceRepository;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final KingdomRepository kingdomRepository;
    private final TroopService troopService;

    public ResourceServiceImpl(
            ResourceRepository resourceRepository, KingdomRepository kingdomRepository, TroopService troopService) {
        this.resourceRepository = resourceRepository;
        this.kingdomRepository = kingdomRepository;
        this.troopService = troopService;
    }

    @Override
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    @Override
    public void foodEaten() {
        int kingdomFoodAmount;

        for (Kingdom kingdom : kingdomRepository.findAll()) {
            int foodEaten = 0;

            for (Troop troop : kingdom.getTroopList()) {

                TroopType troopType = troop.getTroopType();
                int troopLevel = troop.getLevel();
                if (TroopType.SWORDSMAN.equals(troopType)) {
                    foodEaten += GameConstants.SWORDSMAN_FOOD * troopLevel;
                }
                if (TroopType.ARCHER.equals(troopType)) {
                    foodEaten += GameConstants.ARCHER_FOOD * troopLevel;
                }
                if (TroopType.KNIGHT.equals(troopType)) {
                    foodEaten += GameConstants.KNIGHT_FOOD * troopLevel;
                }
            }

            Resource kingdomFood = kingdom.findFoodResource();

            kingdomFoodAmount = kingdomFood.getAmount();

            kingdomFood.setAmount(kingdomFoodAmount - foodEaten);

            List<Troop> troopList = kingdom.getTroopList();

            if (kingdomFood.getAmount() < 0 && troopList.size() > 0) {
                troopService.delete(troopList.get(0));
                troopList.remove(0);
                kingdomFood.setAmount(0);
            }

        }

    }

    @Override
    public void foodFarmed() {
        int kingdomFoodAmount;
        for (Kingdom kingdom : kingdomRepository.findAll()) {
            int capacity = countCapacity(kingdom, BuildingType.GRANARY);
            if (kingdom.findFarm() != null) {
                Resource kingdomFood = kingdom.findFoodResource();
                kingdomFoodAmount = kingdomFood.getAmount();
                kingdomFood.setAmount(
                        kingdomFoodAmount + kingdom.sumFarmLevels() * GameConstants.FARM_RATE);
                if (kingdomFood.getAmount() > capacity) {
                    kingdomFood.setAmount(capacity);
                    this.kingdomRepository.save(kingdom);
                }
            }
        }
    }

    private int countCapacity(Kingdom kingdom, BuildingType buildingType) {
        List<Building> storages = kingdom.findStorages(buildingType);
        int capacity = 0;
        for (Building storage : storages) {
            capacity += storage.getLevel() * 1000;
        }
        return capacity;
    }


    @Override
    public void goldMined() {
        int kingdomGoldAmount;
        for (Kingdom kingdom : kingdomRepository.findAll()) {
            if (kingdom.findMine() != null) {
                int capacity = countCapacity(kingdom, BuildingType.TREASURY);
                Resource kingdomGold = kingdom.findGoldResource();
                kingdomGoldAmount = kingdomGold.getAmount();
                kingdomGold.setAmount(
                        kingdomGoldAmount + kingdom.sumMineLevels() * GameConstants.MINE_RATE);
                if (kingdomGold.getAmount() > capacity) {
                    kingdomGold.setAmount(capacity);
                    this.kingdomRepository.save(kingdom);
                }
            }
        }

    }
}
