package com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle;

import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueReceiver;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueReceiverForBattle extends QueueReceiver {

    private final BattleHandler battleHandler;

    public QueueReceiverForBattle(String qName, BattleHandler battleHandler)
            throws IOException, TimeoutException {
        super(qName);
        this.battleHandler = battleHandler;
    }


    @Override
    protected void doWork(String message) throws InterruptedException, IOException, TimeoutException {
        String[] messageKey = message.split("/");
        Long battleId = Long.parseLong(messageKey[1]);
        Long travelTime = Long.parseLong(messageKey[2]);

        Thread.sleep(travelTime);

        battleHandler.battleResolve(battleId, travelTime);
    }
}
