package com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle;

import com.greenfoxacademy.islandfoxtribes.models.battle.Battle;
import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.repositories.battle.BattleRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Component

public class EndBattleHandler {

    private final KingdomRepository kingdomRepository;
    private final TroopFactory troopFactory;
    private final TroopRepository troopRepository;
    private final BattleRepository battleRepository;

    @Autowired
    public EndBattleHandler(KingdomRepository kingdomRepository, TroopFactory troopFactory,
                            TroopRepository troopRepository, BattleRepository battleRepository) {
        this.kingdomRepository = kingdomRepository;
        this.troopFactory = troopFactory;
        this.troopRepository = troopRepository;
        this.battleRepository = battleRepository;
    }


    public void endBattle(Long kingdomId, int food, int gold, Long battleId) {

        List<Troop> attackersArmy = this.battleRepository.getById(battleId).getTroops();
        Kingdom kingdom = this.kingdomRepository.getById(kingdomId);
        int foodCapacity = countCapacity(kingdom, BuildingType.GRANARY);
        int goldCapacity = countCapacity(kingdom, BuildingType.TREASURY);

        int newAmount1 = Math.min(foodCapacity, kingdom.findFoodResource().getAmount() + food);
        kingdom.findFoodResource().setAmount(newAmount1);

        int newAmount2 = Math.min(goldCapacity, kingdom.findGoldResource().getAmount() + gold);
        kingdom.findGoldResource().setAmount(newAmount2);


        Building fakeBuilding = new Building();
        fakeBuilding.setLevel(1);
        for (Troop troop : attackersArmy) {
            troop.setBattle(null);
            Troop fakeTroop = this.troopFactory.createTroop(fakeBuilding, troop.getTroopType());
            troop.setHp(troop.getLevel() * fakeTroop.getHp());
            this.troopRepository.save(troop);
        }
        Battle battle = this.battleRepository.getById(battleId);
        battle.getTroops().clear();
        this.battleRepository.delete(battle);
    }

    private int countCapacity(Kingdom kingdom, BuildingType buildingType) {
        List<Building> storages = kingdom.findStorages(buildingType);
        int capacity = 0;
        for (Building storage : storages) {
            capacity += storage.getLevel() * 1000;
        }
        return capacity;
    }
}
