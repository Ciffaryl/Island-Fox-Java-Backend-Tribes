package com.greenfoxacademy.islandfoxtribes.models.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Player implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long id;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    private String email;

    private boolean isEnabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private List<Kingdom> kingdomList;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "player")
    private List<Message> messageList;
  
    public Player(String userName, String password, String email) {

        this.userName = userName;
        this.password = password;
        this.email = email;
        this.isEnabled = false;
        this.kingdomList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public void addKingdom(Kingdom kingdom) {
        this.kingdomList.add(kingdom);
    }

    public void addMessage(Message message) {
        this.messageList.add(message);
    }
}
