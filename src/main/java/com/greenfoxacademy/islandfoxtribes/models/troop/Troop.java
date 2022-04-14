package com.greenfoxacademy.islandfoxtribes.models.troop;

import com.greenfoxacademy.islandfoxtribes.models.battle.Battle;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "troops")
public class Troop implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Integer level;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer cost;
    private Integer speed;

    private Integer constructionTime;

    @Enumerated(EnumType.STRING)
    private TroopType troopType;

    @ManyToOne
    private Kingdom kingdom;

    @ManyToOne
    private Battle battle;


    public Troop(int level, int hp, int attack, int defense, int cost, int constructionTime, int speed,
                 TroopType troopType) {
        this.level = level;
        this.hp = hp * level;
        this.attack = attack * level;
        this.defense = defense * level;
        this.cost = cost * level;
        this.constructionTime = constructionTime;
        this.troopType = troopType;
        this.speed = speed;
    }

    public Troop(long id, Integer level, Integer hp, Integer attack, Integer defense, Integer cost,
                 Integer constructionTime, TroopType troopType, Kingdom kingdom) {
        this.id = id;
        this.level = level;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.cost = cost;
        this.constructionTime = constructionTime;
        this.troopType = troopType;
        this.kingdom = kingdom;
    }

    public void increaseLevelByBarrack(Integer level) {
        this.hp = (hp / this.level) * level;
        this.attack = (attack / this.level) * level;
        this.defense = (defense / this.level) * level;
        this.cost = (cost / this.level) * level;
        this.level = level;
    }

}
