package com.greenfoxacademy.islandfoxtribes.repositories.battle;

import com.greenfoxacademy.islandfoxtribes.models.battle.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {
}
