package com.greenfoxacademy.islandfoxtribes.models.building;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter

@Entity
public class Building implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BuildingType buildingType;

    private Integer level;
    private Integer cost;
    private Integer constructionTime;
    private boolean isCreating;
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "kingdom_id")
    private Kingdom kingdom;

    public Building(BuildingType buildingType, Integer cost, Integer constructionTime, Integer capacity) {
        this.buildingType = buildingType;
        this.level = 1;
        this.cost = cost;
        this.constructionTime = constructionTime;
        this.isCreating = false;
        this.capacity = capacity;
    }

    public boolean canBuildingBeBuilt(Building townHall) {
        if (this.level < townHall.getLevel() || this.getBuildingType().equals(BuildingType.TOWN_HALL)) {
            return true;
        } else
            return false;
    }

}
