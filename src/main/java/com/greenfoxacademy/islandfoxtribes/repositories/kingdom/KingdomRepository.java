package com.greenfoxacademy.islandfoxtribes.repositories.kingdom;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Repository
public interface KingdomRepository extends JpaRepository<Kingdom, Long> {

    Optional<Kingdom> findKingdomByName(String name);

    Kingdom findKingdomByPlayer(Optional<Player> player);

    Kingdom findKingdomById(Long id);

}
