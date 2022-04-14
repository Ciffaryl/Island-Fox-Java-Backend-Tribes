package com.greenfoxacademy.islandfoxtribes.services.troop;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.models.troop.troopDTOs.RequestNewTroopsDTO;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueSender;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.troop.QueueReceiverForTroop;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.troop.TroopCreator;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class TroopServiceImpl implements TroopService {

    private final TroopRepository troopRepository;
    private final TroopFactory troopFactory;
    private final TroopCreator troopCreator;
    private final KingdomRepository kingdomRepository;

    @Autowired
    public TroopServiceImpl(TroopRepository troopRepository, TroopFactory troopFactory,
                            TroopCreator troopCreator,
                            KingdomRepository kingdomRepository) {
        this.troopRepository = troopRepository;
        this.troopFactory = troopFactory;
        this.troopCreator = troopCreator;
        this.kingdomRepository = kingdomRepository;
    }

    @Override
    public Errors createTroops(RequestNewTroopsDTO requestNewTroopsDTO, Kingdom kingdom)
            throws IOException, TimeoutException {

        return createTroops(requestNewTroopsDTO, kingdom, false);

    }

    @Override
    public Errors createTroops(RequestNewTroopsDTO requestNewTroopsDTO, Kingdom kingdom, Boolean skipQueue)
            throws IOException, TimeoutException {

        Building building = kingdom.findBarracks();
        String troopTypeLabel = requestNewTroopsDTO.getType();
        int i = requestNewTroopsDTO.getQuantity();
        Player player = kingdom.getPlayer();

        if (building == null) {
            return new Errors("You can build only in barracks!");
        }

        Troop troop = troopFactory.createTroop(building, TroopType.fromLabel(troopTypeLabel));

        int kingdomFoodAmount = kingdom.findFoodResource().getAmount();
        int allTroopsCost = troop.getCost() * i;

        if (kingdomFoodAmount < allTroopsCost) {
            return new Errors("You don't have food for this!");
        } else if (building.isCreating()) {
            return new Errors("Your barracks are already making new troops!");
        } else {

            Long troopConstructionTime = (long) troopFactory.createTroop(building,
                    TroopType.fromLabel(troopTypeLabel)).getConstructionTime();

            // Creating messageKey which looks like this: "123/456/789/Archer/15000".
            // This message holds key values for our needs. It works as key and as queue name.
            String messageKey = "" + player.getId()
                    + "/" + building.getId()
                    + "/" + kingdom.getId()
                    + "/" + troopTypeLabel
                    + "/" + troopConstructionTime
                    + "/create";

            if (!skipQueue) {
                rabbitMQ(i, messageKey, building);
            }

            // Setting the food amount to the correct value.
            kingdom.findFoodResource().setAmount(kingdomFoodAmount - allTroopsCost);
            kingdomRepository.save(kingdom);

            return null;
        }
    }

    @Override
    public Errors upgradeTroops(Kingdom kingdom, List<Long> troopsToUpgrade) {

        if (!(troopsBelongsToKingdomValidation(troopsToUpgrade, kingdom))) {
            return new Errors("All of upgraded troops need to belong to your kingdom!");
        }
        Resource gold = kingdom.findGoldResource();
        Building barracks = kingdom.findBarracks();
        int price = countPriceOfUpgrade(troopsToUpgrade, barracks);

        if (price > gold.getAmount()) {
            return new Errors("You don't have enough money for this!");
        }

        for (Long troopId : troopsToUpgrade) {
            Troop troop = this.troopRepository.getById(troopId);
            troop.increaseLevelByBarrack(barracks.getLevel());
        }
        gold.setAmount(gold.getAmount() - price);

        this.kingdomRepository.save(kingdom);
        return null;
    }

    private int countPriceOfUpgrade(List<Long> troopsToUpgrade, Building barracks) {
        int price = 0;
        for (Long troopId : troopsToUpgrade) {
            Troop troop = this.troopRepository.getById(troopId);
            int lvlCap = barracks.getLevel() - troop.getLevel();
            int troopPrice = lvlCap * GameConstants.ONE_LVL_UPGRADE_PRICE;
            price += troopPrice;
        }
        return price;
    }

    private boolean troopsBelongsToKingdomValidation(List<Long> troopsToUpgrade, Kingdom kingdom) {
        for (Long troopId : troopsToUpgrade) {
            Troop troop = this.troopRepository.getById(troopId);
            if (!(troop.getKingdom().getId().equals(kingdom.getId()))) {
                return false;
            }
        }
        return true;
    }

    private void rabbitMQ(Integer i, String messageKey, Building building) throws IOException, TimeoutException {
        QueueReceiverForTroop queueReceiverTroop = new QueueReceiverForTroop(messageKey, troopCreator);
        QueueSender queueSenderTroop = new QueueSender(messageKey);

        for (int j = 0; j < i; j++) {
            building.setCreating(true);
            queueSenderTroop.sendMessage(messageKey);
        }

        queueReceiverTroop.receiveMessage();

    }

    @Override
    public void delete(Troop troop) {
        troopRepository.delete(troop);
    }

    @Override
    public void upgradeTroops(Long kingdomId, String troopType, String stat, int level) {

        for (Troop troop : kingdomRepository.getById(kingdomId).getTroopList()) {

            if ("Hp".equals(stat)) {
                if (troopType.equals(troop.getTroopType().label)) {
                    troop.setHp(troop.getHp() + level * GameConstants.HP_UPGRADE_RATE);
                }
            }
            if ("Attack".equals(stat)) {
                if (troopType.equals(troop.getTroopType().label)) {
                    troop.setAttack(troop.getAttack() + level * GameConstants.ATTACK_UPGRADE_RATE);
                }
            }
            if (stat.equals("Defense")) {
                if (troop.getTroopType().label.equals(troopType)) {
                    troop.setDefense(troop.getDefense() + level * GameConstants.DEFENSE_UPGRADE_RATE);
                }
            }

        }
    }

}
