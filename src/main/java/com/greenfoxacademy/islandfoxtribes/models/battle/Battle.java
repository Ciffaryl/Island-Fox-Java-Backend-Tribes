package com.greenfoxacademy.islandfoxtribes.models.battle;


import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
@Transactional

public class Battle {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long attacker;
    private Long target;
    private String battleType;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Troop> troops;

    public Battle(Long target, Long attacker, String battleType) {
        this.target = target;
        this.battleType = battleType;
        this.attacker = attacker;
        this.troops = new ArrayList<>();
    }

    public Battle() {
        this.troops = new ArrayList<>();
    }

    public void addTroopToBattle(Troop troop) {
        this.troops.add(troop);
    }
}
