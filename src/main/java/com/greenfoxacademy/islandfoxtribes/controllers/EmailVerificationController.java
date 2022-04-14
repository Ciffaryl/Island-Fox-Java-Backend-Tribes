package com.greenfoxacademy.islandfoxtribes.controllers;

import com.greenfoxacademy.islandfoxtribes.models.player.ConfirmationToken;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.repositories.player.ConfirmationTokenRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailVerificationController {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PlayerRepository playerRepository;

    public EmailVerificationController(ConfirmationTokenRepository confirmationTokenRepository,
                                       PlayerRepository playerRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.playerRepository = playerRepository;
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            Player player = playerRepository.findPlayerByEmail(token.getPlayer().getEmail());
            player.setEnabled(true);
            playerRepository.save(player);
            return ResponseEntity.status(HttpStatus.OK).body("Registration successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cant confirm your verification token.");
        }
    }
}
