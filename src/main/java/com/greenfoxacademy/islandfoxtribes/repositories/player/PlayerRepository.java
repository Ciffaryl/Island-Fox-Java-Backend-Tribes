package com.greenfoxacademy.islandfoxtribes.repositories.player;

import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUserName(String username);

    Player findPlayerByUserName(String username);

    Boolean existsPlayerByUserName(String username);

    Player findPlayerByEmail(String email);

    Optional<Player> findByEmail (String email);

}
