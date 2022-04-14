package com.greenfoxacademy.islandfoxtribes.rabbitMQ.troop;

import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueReceiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueReceiverForTroop extends QueueReceiver {

    private final TroopCreator troopCreator;

    public QueueReceiverForTroop(String qName, TroopCreator troopCreator)
            throws IOException, TimeoutException {
        super(qName);
        this.troopCreator = troopCreator;
    }

    @Override
    protected void doWork(String message) throws InterruptedException {
        String[] messageKey = message.split("/");
        Long buildingId = Long.parseLong(messageKey[1]);
        Long kingdomId = Long.parseLong(messageKey[2]);
        String troopType = messageKey[3];
        Long constructionTime = Long.parseLong(messageKey[4]);
        String task = messageKey[5];

        troopCreator.setBuildingCreatingToTrue(buildingId);

        Thread.sleep(constructionTime);

        troopCreator.createTroops(kingdomId, buildingId, troopType, task);
    }
}
