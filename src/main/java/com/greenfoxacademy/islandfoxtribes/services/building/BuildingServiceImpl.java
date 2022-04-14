package com.greenfoxacademy.islandfoxtribes.services.building;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueSender;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.building.BuildingCreator;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.building.QueueReceiverForBuilding;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.upgrades.QueueReceiverForUpgrades;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.upgrades.UpgradeCreator;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Transactional
@Service
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingCreator buildingCreator;
    private final KingdomRepository kingdomRepository;
    private final BuildingFactory buildingFactory;
    private final UpgradeCreator upgradeCreator;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository,
                               BuildingCreator buildingCreator,
                               KingdomRepository kingdomRepository,
                               BuildingFactory buildingFactory,
                               UpgradeCreator upgradeCreator) {
        this.buildingRepository = buildingRepository;
        this.buildingCreator = buildingCreator;
        this.kingdomRepository = kingdomRepository;
        this.buildingFactory = buildingFactory;
        this.upgradeCreator = upgradeCreator;
    }

    @Override
    public Errors buildBuilding(Long buildingId, Kingdom kingdom)
            throws IOException, TimeoutException {

        return buildBuilding(buildingId, kingdom, false);

    }

    @Override
    public Errors buildBuilding(Long buildingId, Kingdom kingdom, boolean skipQueue)
            throws IOException, TimeoutException {
        Building actualBuilding = buildingRepository.getById(buildingId);
        Integer buildingCost = createBuilding(buildingId).getCost();
        String messageKey;
        if (kingdom.isBuildingStatus()) {
            return new Errors("You are already building!");
        } else if (!(actualBuilding.canBuildingBeBuilt(kingdom.findTownHall()))) {
            return new Errors("This building can't have higher level than Town Hall!");
        } else if (kingdom.findGoldResource().getAmount() < buildingCost) {
            return new Errors("You don't have enough gold to build that!");
        } else {

            Integer constructionTime = this.buildingRepository.getById(buildingId).getConstructionTime();
            messageKey =
                    "upgrade/" + buildingId + "/" + kingdom.getId() + "/" + actualBuilding.getBuildingType().label
                            + "/" + constructionTime;

            rabbitMQ(skipQueue, messageKey, kingdom);

            kingdom.findGoldResource().setAmount(kingdom.findGoldResource().getAmount() - buildingCost);
            kingdomRepository.save(kingdom);
            return null;
        }

    }

    private Building createBuilding(Long buildingId) {
        Building building = this.buildingRepository.getById(buildingId);
        return this.buildingFactory.createBuilding(building.getBuildingType());
    }

    @Override
    public Errors buildBuilding(String buildingTypeLabel, Kingdom kingdom) throws IOException, TimeoutException {

        return buildBuilding(buildingTypeLabel, kingdom, false);

    }

    @Override
    public Errors buildBuilding(String buildingTypeLabel, Kingdom kingdom, boolean skipQueue)
            throws IOException, TimeoutException {
        Integer buildingCost = this.buildingFactory.createBuilding(BuildingType.fromLabel(buildingTypeLabel)).getCost();
        String messageKey;
        Long id = kingdom.getId();
        if (kingdom.isBuildingStatus()) {
            return new Errors("You are already building!");
        } else if (kingdom.findGoldResource().getAmount() < buildingCost) {
            return new Errors("You don't have enough gold to build that!");
        } else if (!(buildingCapacityAvailable(id, buildingTypeLabel))) {
            return new Errors("You have full capacity of this type of building");
        } else {
            Integer constructionTime = this.buildingFactory.createBuilding(
                    BuildingType.fromLabel(buildingTypeLabel)).getConstructionTime();
            messageKey = "create/" + 0 + "/" + kingdom.getId() + "/" + buildingTypeLabel + "/" + constructionTime;

            rabbitMQ(skipQueue, messageKey, kingdom);

            kingdom.findGoldResource().setAmount(kingdom.findGoldResource().getAmount() - buildingCost);
            kingdomRepository.save(kingdom);
            return null;
        }
    }

    private boolean buildingCapacityAvailable(Long kingdomId, String buildingTypeLabel) {
        Integer capacity = this.buildingFactory.createBuilding(BuildingType.fromLabel(buildingTypeLabel)).getCapacity();
        Kingdom kingdom = this.kingdomRepository.getById(kingdomId);
        if (kingdom.amountOfBuildingsOfType(buildingTypeLabel) >= capacity) {
            return false;
        } else return true;
    }

    @Override
    public void rabbitMQ(boolean skipQueue, String messageKey, Kingdom kingdom) throws IOException, TimeoutException {
        if (!skipQueue) {
            kingdom.setBuildingStatus(true);
            kingdomRepository.save(kingdom);

            QueueReceiverForBuilding buildingReceiver = new QueueReceiverForBuilding(messageKey, buildingCreator);
            QueueSender buildingSender = new QueueSender(messageKey);
            buildingSender.sendMessage(messageKey);
            buildingReceiver.receiveMessage();
        }

    }

    @Override
    public void academyUpgrades(Long kingdomId, Long buildingId, String upgradeName)
            throws IOException, TimeoutException {

        academyUpgrades(kingdomId, buildingId, upgradeName, false);

    }

    @Override
    public void academyUpgrades(Long kingdomId, Long buildingId, String upgradeName, boolean skipQueue)
            throws IOException, TimeoutException {

        Kingdom kingdom = kingdomRepository.getById(kingdomId);

        Resource kingdomGold = kingdomRepository.getById(kingdomId).findGoldResource();

        int level = Character.getNumericValue(upgradeName.charAt(upgradeName.length() - 1));

        int upgradeCost = level * GameConstants.UPGRADE_COST_RATE;

        if (kingdomGold.getAmount() > upgradeCost || !buildingRepository.getById(buildingId).isCreating()) {

            String messageKey = "" + kingdomId
                    + "/" + buildingId
                    + "/" + upgradeName;

            if (!skipQueue) {

                QueueReceiverForUpgrades queueReceiverForUpgrades = new QueueReceiverForUpgrades(
                        messageKey, upgradeCreator);
                QueueSender queueSenderUpgrades = new QueueSender(messageKey);

                queueSenderUpgrades.sendMessage(messageKey);
                queueReceiverForUpgrades.receiveMessage();

            }

            kingdomGold.setAmount(kingdomGold.getAmount() - upgradeCost);
            kingdomRepository.save(kingdom);

        }

    }

}
