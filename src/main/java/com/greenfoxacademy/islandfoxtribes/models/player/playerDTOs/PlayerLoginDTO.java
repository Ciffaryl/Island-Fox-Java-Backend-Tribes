package com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PlayerLoginDTO {

    private String username;
    private String password;

}
