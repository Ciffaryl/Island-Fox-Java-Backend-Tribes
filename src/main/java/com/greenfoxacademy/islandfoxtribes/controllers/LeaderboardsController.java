package com.greenfoxacademy.islandfoxtribes.controllers;

import com.greenfoxacademy.islandfoxtribes.services.leaderboards.LeaderboardsService;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Import(SecurityConfig.class)

@RestController
public class LeaderboardsController {

    private final LeaderboardsService leaderboardsService;

    public LeaderboardsController(LeaderboardsService leaderboardsService) {
        this.leaderboardsService = leaderboardsService;
    }

    @GetMapping("/leaderboards/buildings")
    public ResponseEntity buildingsLeaderboard() {
        return ResponseEntity.status(HttpStatus.OK).body(leaderboardsService.showBuildingsLeaderboard());
    }

    @GetMapping("/leaderboards/troops")
    public ResponseEntity troopsLeaderboard() {
        return ResponseEntity.status(HttpStatus.OK).body(leaderboardsService.showTroopLeaderboard());
    }

    @GetMapping("/leaderboards/kingdoms")
    public ResponseEntity kingdomsLeaderboards() {
        return ResponseEntity.status(HttpStatus.OK).body(leaderboardsService.showKingdomLeaderboard());
    }
}
