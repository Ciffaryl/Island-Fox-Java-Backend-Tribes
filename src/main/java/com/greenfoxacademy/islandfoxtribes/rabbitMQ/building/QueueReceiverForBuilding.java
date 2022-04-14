package com.greenfoxacademy.islandfoxtribes.rabbitMQ.building;

import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueReceiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueReceiverForBuilding extends QueueReceiver {

    private final BuildingCreator buildingCreator;

    public QueueReceiverForBuilding(String qName, BuildingCreator buildingCreator)
            throws IOException, TimeoutException {
        super(qName);
        this.buildingCreator = buildingCreator;
    }

    @Override
    protected void doWork(String message) throws InterruptedException {
        String[] messageKey = message.split("/");
        String command = messageKey[0];
        Long buildingId = Long.parseLong(messageKey[1]);
        Long kingdomId = Long.parseLong(messageKey[2]);
        String buildingType = messageKey[3];
        long constructionTime = Long.parseLong(messageKey[4]);

        Thread.sleep(constructionTime);

        buildingCreator.createOrUpgradeBuilding(kingdomId, buildingId, buildingType, command);
    }
}
