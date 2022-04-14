package com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerRegisterResponseDTO {

    private String username;
    private Long kingdomId;
    private String verify;

    public PlayerRegisterResponseDTO(String username, Long kingdomId) {
        this.username = username;
        this.kingdomId = kingdomId;
        this.verify = "Welcome knight " + username
                + "! We are glad that you have decided to build your kingdom on our territory. "
                + "Before you can build your kingdom, you must verify your registration. "
                + "We'll send a verification code to your email address.";
    }
}
