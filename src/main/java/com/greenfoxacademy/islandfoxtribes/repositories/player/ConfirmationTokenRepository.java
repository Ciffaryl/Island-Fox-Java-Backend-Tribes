package com.greenfoxacademy.islandfoxtribes.repositories.player;

import com.greenfoxacademy.islandfoxtribes.models.player.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, String> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
}
