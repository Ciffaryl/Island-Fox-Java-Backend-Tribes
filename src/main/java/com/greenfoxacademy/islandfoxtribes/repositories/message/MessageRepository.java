package com.greenfoxacademy.islandfoxtribes.repositories.message;

import com.greenfoxacademy.islandfoxtribes.models.message.Message;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findByPlayerAndId(Player player, Long messageId);
}
