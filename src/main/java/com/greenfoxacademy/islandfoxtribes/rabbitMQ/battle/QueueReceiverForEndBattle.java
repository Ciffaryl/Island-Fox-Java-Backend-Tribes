package com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueReceiver;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Transactional

public class QueueReceiverForEndBattle extends QueueReceiver {

    private EndBattleHandler endBattleHandler;

    public QueueReceiverForEndBattle(String qName, EndBattleHandler endBattleHandler)
            throws IOException, TimeoutException {
        super(qName);
        this.endBattleHandler = endBattleHandler;

    }

    @Override
    public void doWork(String message) throws InterruptedException {
        String[] messageKey = message.split("/");
        Long kingdomId = Long.parseLong(messageKey[0]);
        int food = Integer.parseInt(messageKey[1]);
        int gold = Integer.parseInt(messageKey[2]);
        Long travelTime = Long.parseLong(messageKey[3]);
        Long battleId = Long.parseLong(messageKey[4]);

        Thread.sleep(travelTime);

        this.endBattleHandler.endBattle(kingdomId, food, gold, battleId);
    }
}
