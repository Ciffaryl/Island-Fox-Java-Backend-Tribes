package com.greenfoxacademy.islandfoxtribes.models.DTOs;

import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import lombok.Data;

@Data
public class TroopDTO {

    private Long id;
    private int level;
    private int hp;
    private int attack;
    private int defence;
    private String type;

    public TroopDTO(Troop troop) {
        this.id = troop.getId();
        this.level = troop.getLevel();
        this.hp = troop.getHp();
        this.attack = troop.getAttack();
        this.defence = troop.getDefense();
        this.type = troop.getTroopType().label;
    }
}
