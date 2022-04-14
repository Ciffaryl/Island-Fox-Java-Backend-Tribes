package com.greenfoxacademy.islandfoxtribes.models.player;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long tokenid;

    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(targetEntity = Player.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "id")
    private Player player;

    public ConfirmationToken() {
    }

    public ConfirmationToken(Player player) {
        this.player = player;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }
}
