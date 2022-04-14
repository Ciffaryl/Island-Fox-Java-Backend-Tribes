package com.greenfoxacademy.islandfoxtribes.services.player;

import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerRegisterRequestDTO;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerRegisterResponseDTO;


public interface PlayerService {

    PlayerRegisterResponseDTO registration(PlayerRegisterRequestDTO player);

    Boolean validation(Long kingdomId);

}
