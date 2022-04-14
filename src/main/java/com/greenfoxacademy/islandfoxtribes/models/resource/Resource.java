package com.greenfoxacademy.islandfoxtribes.models.resource;

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
@Table(name = "resources")
public class Resource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private Integer amount;
    private Integer generation;
    private Integer updatedAt;

    @ManyToOne
    private Kingdom kingdom;


    public Resource(ResourceType resourceType, Integer amount, Kingdom kingdom) {
        this.resourceType = resourceType;
        this.amount = amount;
        this.kingdom = kingdom;
    }

    public Resource(ResourceType resourceType, Integer amount, Kingdom kingdom, Integer generation, Integer updatedAt) {
        this.resourceType = resourceType;
        this.amount = amount;
        this.kingdom = kingdom;
        this.generation = generation;
        this.updatedAt = updatedAt;

    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", resourceType=" + resourceType +
                ", amount=" + amount +
                ", kingdom=" + kingdom +
                '}';
    }
}
