package com.greenfoxacademy.islandfoxtribes.schedules;

import com.greenfoxacademy.islandfoxtribes.services.kingdom.KingdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

// This class is responsible for refreshing of the game.
@Transactional
@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true)
public class Scheduling {

    private final KingdomService kingdomService;

    @Autowired
    public Scheduling(KingdomService kingdomService) {
        this.kingdomService = kingdomService;
    }

    @Scheduled(fixedRateString = "${TICK_LENGTH}000")
    public void scheduled() {
        kingdomService.scheduledRefreshing();
    }

    @Scheduled(fixedRateString = "${LOYALTY_TICK_LENGTH}00000")
    public void scheduledLoyaltyTick() {
        kingdomService.scheduledLoyaltyIncrease();
    }

}
