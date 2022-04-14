package com.greenfoxacademy.islandfoxtribes.models.message;

import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@AllArgsConstructor
@Setter

@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private String sender;

    private String object;

    private String text;

    private String sentAt;

    public Message(Player player, String text, String object, String sender) {
        this.player = player;
        this.text = text;
        this.sender = sender;
        this.object = object;
    }

    public Message() {
        this.sentAt = dateFormatter();
    }

    // controller for test purposes
    public Message(Player player, String sender, String object, String text, String sentAt) {
        this.player = player;
        this.sender = sender;
        this.object = object;
        this.text = text;
        this.sentAt = sentAt;
    }

    public String dateFormatter() {
        Date date = new Date();
        SimpleDateFormat dateFor = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        return dateFor.format(date);
    }
}
