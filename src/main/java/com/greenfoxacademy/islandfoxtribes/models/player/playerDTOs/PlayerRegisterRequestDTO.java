package com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerRegisterRequestDTO {

    private String username;
    private String password;
    private String email;
    private String kingdomName;

}
