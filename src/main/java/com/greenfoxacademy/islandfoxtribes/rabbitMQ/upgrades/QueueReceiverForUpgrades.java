package com.greenfoxacademy.islandfoxtribes.rabbitMQ.upgrades;

import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueReceiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueReceiverForUpgrades extends QueueReceiver {

    private final UpgradeCreator upgradeCreator;

    public QueueReceiverForUpgrades(String qName, UpgradeCreator upgradeCreator)
            throws IOException, TimeoutException {
        super(qName);
        this.upgradeCreator = upgradeCreator;
    }

    @Override
    protected void doWork(String message) throws InterruptedException {
        String[] messageKey = message.split("/");
        Long kingdomId = Long.parseLong(messageKey[0]);
        Long buildingId = Long.parseLong(messageKey[1]);
        String upgradeName = messageKey[2] + "/" + messageKey[3] + "/" + messageKey[4] + "/" + messageKey[5];

        upgradeCreator.setAcademyCreatingToTrue(buildingId);

        Thread.sleep(Long.parseLong(String.valueOf(upgradeName.charAt(upgradeName.length() - 1)))
                * GameConstants.UPGRADE_CONSTRUCTION_TIME);

        upgradeCreator.upgradingTroops(kingdomId, buildingId, upgradeName);
    }
}
