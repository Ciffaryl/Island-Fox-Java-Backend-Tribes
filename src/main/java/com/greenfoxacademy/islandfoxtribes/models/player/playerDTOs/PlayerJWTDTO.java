package com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs;

import lombok.Data;


@Data
public class PlayerJWTDTO {

    private String status;
    private String token;
    private String refresh_Token;

    public PlayerJWTDTO(String token, String refreshToken) {
        this.status = "ok";
        this.token = token;
        this.refresh_Token = refreshToken;
    }
}
